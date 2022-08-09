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
