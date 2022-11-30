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

import net.modmanagermc.core.model.JarFileInfo
import net.modmanagermc.core.model.Mod
import net.modmanagermc.core.model.Version
import net.modmanagermc.core.update.IUpdateProvider
import net.modmanagermc.core.update.Update

/**
 * @author DeathsGun
 * @since 1.0.0
 */
interface IModService {

    val providers: List<IUpdateProvider>

    suspend fun createJarFileInfo(): List<JarFileInfo>

    fun getNewerVersions(fileInfo: JarFileInfo): List<Version>

    fun registerProvider(provider: IUpdateProvider)

    fun getModState(modId: String): State

    fun install(mod: Mod)

    fun installVersion(version: Version)

    fun removeMod(modId: String)
    fun update(update: Update)
}