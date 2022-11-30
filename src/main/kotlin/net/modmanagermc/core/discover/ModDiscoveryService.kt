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

package net.modmanagermc.core.discover

import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.metadata.ModMetadata
import net.modmanagermc.core.di.DI
import net.modmanagermc.core.extensions.generateHashes
import net.modmanagermc.core.extensions.readMetadata
import net.modmanagermc.core.extensions.updatesDisabled
import java.nio.file.Files
import java.nio.file.Path
import java.util.StringJoiner
import kotlin.io.path.absolutePathString
import kotlin.io.path.extension
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrNull

/**
 * Implementation of [IModDiscoveryService]
 *
 * @author DeathsGun
 * @since 1.0.0
 */
internal class ModDiscoveryService(di: DI) : IModDiscoveryService {

    private val fabricLoader: FabricLoader by di
    private val blocked = listOf("minecraft", "java")
    private val projectIdModIs = mutableMapOf<String, String>()
    private val modIdJars = mutableMapOf<String, String>()
    private val hashJars = mutableMapOf<String, String>()

    override fun getMods(): List<ModMetadata> {
        return fabricLoader.allMods.map { it.metadata }
            .filter {
                !it.updatesDisabled() && !blocked.contains(it.id) &&
                        !it.containsCustomValue("fabric-api:module-lifecycle") &&
                        !it.containsCustomValue("fabric-loom:generated")
            }
    }

    override fun getJar(fabricLoader: FabricLoader, modId: String): Path? {
        if (modIdJars.containsKey(modId)) {
            return Path.of(modIdJars[modId]!!)
        }
        val files =
            Files.walk(fabricLoader.gameDir.resolve("mods")).filter { "jar".equals(it.extension, true) }
        files.forEach {
            val metadata = it.readMetadata(fabricLoader) ?: return@forEach
            modIdJars[metadata.id] = it.absolutePathString()
        }
        val path = modIdJars[modId] ?: return null
        return Path.of(path)
    }

    override fun findJarByHashes(fabricLoader: FabricLoader, hashes: Map<String, String>): Path? {
        for ((key, value) in hashJars) {
            if (hashes.containsValue(key)) {
                return Path.of(value)
            }
        }
        val files = Files.walk(fabricLoader.gameDir.resolve("mods")).filter { "jar".equals(it.extension, true) }
        files.forEach { path ->
            path.generateHashes().forEach {
                hashJars[it.value] = path.absolutePathString()
            }
        }
        for ((key, value) in hashJars) {
            if (hashes.containsValue(key)) {
                return Path.of(value)
            }
        }
        return null
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun getModMetadata(fabricLoader: FabricLoader, projectId: String): ModMetadata? {
        val modId = projectIdModIs[projectId] ?: return null
        return fabricLoader.getModContainer(modId).getOrNull()?.metadata
    }

    override fun setProjectIdForModId(projectId: String, modId: String) {
        projectIdModIs[projectId] = modId
    }

}