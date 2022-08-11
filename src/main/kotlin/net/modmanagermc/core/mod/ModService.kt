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

import net.fabricmc.loader.api.FabricLoader
import net.modmanagermc.core.di.DI
import net.modmanagermc.core.discover.IModDiscoveryService
import net.modmanagermc.core.extensions.generateHashes
import net.modmanagermc.core.extensions.getUpdateProvider
import net.modmanagermc.core.model.JarFileInfo
import net.modmanagermc.core.model.Version
import net.modmanagermc.core.update.IUpdateProvider
import net.modmanagermc.core.update.provider.modrinth.Modrinth
import org.apache.logging.log4j.LogManager
import java.util.*

internal class ModService(di: DI) : IModService {

    private val fabricLoader: FabricLoader by di
    private val logger = LogManager.getLogger(ModService::class.java)
    private val providerList: MutableList<IUpdateProvider> = mutableListOf(Modrinth(di))
    override val providers: List<IUpdateProvider> get() = providerList
    private val discoveryService: IModDiscoveryService by di
    private val modStates = mutableMapOf<String, State>()

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
}