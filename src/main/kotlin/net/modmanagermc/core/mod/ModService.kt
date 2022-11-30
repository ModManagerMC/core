/*
 * Copyright (c) 2022 DeathsGun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.modmanagermc.core.mod

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.SemanticVersion
import net.modmanagermc.core.Core
import net.modmanagermc.core.config.Config
import net.modmanagermc.core.di.DI
import net.modmanagermc.core.discover.IModDiscoveryService
import net.modmanagermc.core.exceptions.ModManagerException
import net.modmanagermc.core.extensions.generateHashes
import net.modmanagermc.core.extensions.getUpdateProvider
import net.modmanagermc.core.model.Dependency
import net.modmanagermc.core.model.JarFileInfo
import net.modmanagermc.core.model.Mod
import net.modmanagermc.core.model.Version
import net.modmanagermc.core.update.IUpdateProvider
import net.modmanagermc.core.update.Update
import net.modmanagermc.core.update.provider.modrinth.Modrinth
import net.modmanagermc.core.update.provider.modrinth.model.ErrorResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.apache.logging.log4j.LogManager
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.absolutePathString
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists

@OptIn(ExperimentalSerializationApi::class)
internal class ModService(val di: DI) : IModService {

    private val config: Config by di
    private val fabricLoader: FabricLoader by di
    private val logger = LogManager.getLogger(ModService::class.java)
    private val providerList: MutableList<IUpdateProvider> = mutableListOf(Modrinth(di))
    override val providers: List<IUpdateProvider> get() = providerList
    private val discoveryService: IModDiscoveryService by di
    private val modStates = mutableMapOf<String, State>()
    private val client = HttpClients.createDefault()
    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun createJarFileInfo(): List<JarFileInfo> {
        val mods = discoveryService.getMods()
        logger.info("Processing {} mods", mods.size)
        val modInfos = ArrayList<JarFileInfo>()
        for (mod in mods) {
            val path = discoveryService.getJar(fabricLoader, mod.id)
            if (path == null) {
                logger.debug(
                    "Skipping update check for {} because it has no jar in {}",
                    mod.id,
                    fabricLoader.gameDir.resolve("mods")
                )
                continue
            }
            logger.debug("Adding {} to update check", mod.id)
            modInfos.add(
                JarFileInfo(
                    mod.id,
                    mod.version.friendlyString,
                    mod.getUpdateProvider(),
                    path.generateHashes()
                )
            )
        }
        logger.info("Processed {} mods {} can be used", mods.size, modInfos.size)
        return modInfos
    }

    override fun getNewerVersions(fileInfo: JarFileInfo): List<Version> {
        val versions = mutableListOf<Version>()
        for (providerName in fileInfo.provider.keys) {
            val provider = providers.find { it.name == providerName }
            if (provider == null) {
                logger.warn("Mod ${fileInfo.modId} requested provider $providerName but it's not available")
                continue
            }
            val version = provider.getVersion(fileInfo) ?: continue
            discoveryService.setProjectIdForModId(version.providerModId, fileInfo.modId)
            if (Objects.equals(version.hashes["sha512"], fileInfo.hashes["SHA-512"])) {
                modStates[version.providerModId] = State.INSTALLED
                modStates[fileInfo.modId] = State.INSTALLED
                continue
            }
            modStates[version.providerModId] = State.OUTDATED
            modStates[fileInfo.modId] = State.OUTDATED
            versions.add(version);
        }
        return versions
    }

    override fun registerProvider(provider: IUpdateProvider) {
        logger.debug("Registering provider ${provider.name}")
        this.providerList.add(provider)
    }

    override fun getModState(modId: String): State {
        return modStates.getOrDefault(modId, State.DOWNLOADABLE)
    }

    override fun install(mod: Mod) {
        val provider = providerList.find { it.name == mod.provider } ?: return
        val version = provider.getLatestVersion(mod.id) ?: return
        installVersion(version)
    }

    override fun installVersion(version: Version) {
        val path = fabricLoader.gameDir.resolve("mods").resolve(version.id) // Store all relevant files in one dir
        try {
            Files.createDirectories(path)
        } catch (e: Exception) {
            throw ModManagerException("modmanager.error.install.directory", e.message!!)
        }
        val files = resolve(version.dependencies)
        files[version.filename] = version.downloadUrl
        for ((key, value) in files) {
            download(value, path.resolve(key))
        }
    }

    private fun resolve(dependencies: List<Dependency>): MutableMap<String, String> {
        val files = mutableMapOf<String, String>()
        for (dependency in dependencies) {
            if (!dependency.required) {
                continue // Ignore optional dependencies
            }
            val jar = discoveryService.findJarByHashes(fabricLoader, dependency.hashes)
            if (jar != null) {
                continue // Already installed
            }
            val metadata = discoveryService.getModMetadata(fabricLoader, dependency.projectId)
            if (metadata != null) { // Maybe there is a newer version installed
                val ver = net.fabricmc.loader.api.Version.parse(dependency.version)
                if (ver is SemanticVersion) {
                    if (metadata.version > ver) {
                        continue // Dependency is newer
                    }
                }
            }
            files[dependency.filename] = dependency.downloadUrl
            if (files.isEmpty()) {
                continue
            }
            files.putAll(resolve(dependency.dependencies))
        }
        return files
    }

    private fun download(url: String, path: Path) {
        val request = HttpGet(url)
        request.setHeader("User-Agent", "ModManager-Core ${Core.getCoreVersion(di)}")

        val resp = client.execute(request)
        if (resp.statusLine.statusCode != 200) {
            val error = json.decodeFromStream<ErrorResponse>(resp.entity.content)
            EntityUtils.consume(resp.entity)
            throw error.toException("Received invalid status code ${resp.statusLine.statusCode}.")
        }
        val output = Files.newOutputStream(path)
        resp.entity.writeTo(output)
    }

    override fun update(update: Update) {
        installVersion(update.version)
        removeMod(update.modId)
    }

    override fun removeMod(modId: String) {
        val jar =
            discoveryService.getJar(fabricLoader, modId) ?: throw ModManagerException("modmanager.error.jar.notFound")
        jar.deleteIfExists()
        if (jar.exists()) {
            config.remove.add(jar.absolutePathString())
            config.save()
        }
    }

}