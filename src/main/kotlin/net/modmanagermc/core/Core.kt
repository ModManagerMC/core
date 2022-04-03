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

    val minecraftVersion: String
        get() {
            return FabricLoader.getInstance().getModContainer("minecraft")
                .get().metadata.version.friendlyString
        }

    @JvmStatic
    val di = DI {
        bind<Config> { Config.loadConfig() }
        bind<IModDiscoveryService> { ModDiscoveryService() }
        bind<IModService> { ModService(this) }
        bind<IUpdateService> { UpdateService(this) }
    }

}