/*
 * Copyright 2022 ModManagerMC
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

package xyz.deathsgun.modmanager.core

import net.fabricmc.loader.api.FabricLoader
import xyz.deathsgun.modmanager.core.api.provider.IModProvider
import xyz.deathsgun.modmanager.core.config.Config
import java.util.*

object ModManager {

    private lateinit var modManagerBuildInfo: Properties
    private val buildInfo = Properties()
    val config = Config.loadConfig()
    val provider: List<IModProvider>
        get() = FabricLoader.getInstance().getEntrypoints("modmanager", IModProvider::class.java)

    init {
        buildInfo.load(ModManager::class.java.getResourceAsStream("/core-build.info"))
    }

    @JvmStatic
    fun init(properties: Properties) {
        this.modManagerBuildInfo = properties
    }

    @JvmStatic
    fun getProvider(name: String): IModProvider? {
        return provider.find { it.getName().equals(name, true) }
    }

    fun getCoreVersion(): String = buildInfo["version.core"] as String

    fun getMinecraftReleaseTarget(): String = modManagerBuildInfo["version.target"] as String
    fun getMinecraftVersion(): String = modManagerBuildInfo["version.minecraft"] as String

}