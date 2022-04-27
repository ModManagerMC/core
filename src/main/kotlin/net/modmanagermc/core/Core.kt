package net.modmanagermc.core

import net.fabricmc.loader.api.FabricLoader
import net.modmanagermc.core.config.Config
import net.modmanagermc.core.di.DI
import net.modmanagermc.core.discover.IModDiscoveryService
import net.modmanagermc.core.discover.ModDiscoveryService
import net.modmanagermc.core.mod.IModService
import net.modmanagermc.core.mod.ModService
import net.modmanagermc.core.update.IUpdateService
import net.modmanagermc.core.update.UpdateService

/**
 * Initializer for ModManager Core
 *
 * @since Core 1.0.0
 * @author DeathsGun
 */
object Core {

    fun getMinecraftVersion(di: DI): String {
        val loader: FabricLoader by di
        return loader.getModContainer("minecraft").get().metadata.version.friendlyString
    }

    private lateinit var _di: DI
    @JvmStatic
    val di: DI get() = _di

    fun init() {
        _di = DI {
            bind<Config> { Config.loadConfig() }
            bind<IModDiscoveryService> { ModDiscoveryService(this) }
            bind<IModService> { ModService(this) }
            bind<IUpdateService> { UpdateService(this) }
            bind<FabricLoader> { FabricLoader.getInstance() }
        }
    }

}