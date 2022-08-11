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

package net.modmanagermc.core

import net.fabricmc.loader.api.FabricLoader
import net.modmanagermc.core.config.Config
import net.modmanagermc.core.di.DI
import net.modmanagermc.core.discover.IModDiscoveryService
import net.modmanagermc.core.discover.ModDiscoveryService
import net.modmanagermc.core.mod.IModService
import net.modmanagermc.core.mod.ModService
import net.modmanagermc.core.store.IStoreService
import net.modmanagermc.core.store.StoreService
import net.modmanagermc.core.update.IUpdateService
import net.modmanagermc.core.update.UpdateService

/**
 * Initializer for ModManager Core
 *
 * @since 1.0.0
 * @author DeathsGun
 */
object Core {

    private var initialized = false

    private lateinit var dependencyInjection: DI

    @JvmStatic
    val di: DI get() = dependencyInjection

    fun init() {
        if (initialized) {
            throw RuntimeException("ModManager core is already initialized!")
        }
        dependencyInjection = DI {
            bind<Config> { Config.loadConfig() }
            bind<IModDiscoveryService> { ModDiscoveryService(this) }
            bind<IModService> { ModService(this) }
            bind<IUpdateService> { UpdateService(this) }
            bind<FabricLoader> { FabricLoader.getInstance() }
            bind<IStoreService> { StoreService(this) }
        }
        initialized = true
    }

    fun getMinecraftVersion(di: DI): String {
        val loader: FabricLoader by di
        return loader.getModContainer("minecraft").get().metadata.version.friendlyString
    }

    fun getCoreVersion(di: DI): String {
        val loader: FabricLoader by di
        return loader.getModContainer("modmanager-core").get().metadata.version.friendlyString
    }
}
