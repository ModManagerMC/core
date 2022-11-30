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
import java.nio.file.Path
import java.util.StringJoiner

/**
 * This service helps to interact with
 * the mod's directory.
 *
 * @author DeathsGun
 * @see ModDiscoveryService
 * @since 1.0.0
 */
interface IModDiscoveryService {

    /**
     * Returns a list of all mods which should be checked
     * for updates.
     * Mods which have update checking disabled are excluded.
     * Additional to the java and minecraft dummy mod.
     * @return a list of [ModMetadata]
     */
    fun getMods(): List<ModMetadata>

    /**
     * Returns the JAR of the given mod id.
     *
     * @return the [Path] of the JAR or null if not found
     */
    fun getJar(fabricLoader: FabricLoader, modId: String): Path?

    /**
     * Finds mod by the given hashes
     */
    fun findJarByHashes(fabricLoader: FabricLoader, hashes: Map<String, String>): Path?
    fun getModMetadata(fabricLoader: FabricLoader, projectId: String): ModMetadata?

    fun setProjectIdForModId(projectId: String, modId: String)

}