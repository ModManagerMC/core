package net.modmanagermc.core

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.fabricmc.api.ClientModInitializer
import net.modmanagermc.core.config.Config
import net.modmanagermc.core.di.DI
import net.modmanagermc.core.discover.IModDiscoveryService
import net.modmanagermc.core.discover.ModDiscoveryService
import net.modmanagermc.core.mod.IModService
import net.modmanagermc.core.mod.ModService
import net.modmanagermc.core.provider.IProvider
import net.modmanagermc.core.provider.modrinth.Modrinth
import net.modmanagermc.core.update.IUpdateService
import net.modmanagermc.core.update.UpdateService

/**
 * Initializer for ModManager Core
 *
 * @since Core 1.0.0
 * @author DeathsGun
 */
object Core {

    @OptIn(ExperimentalSerializationApi::class)
    private val buildInfo: Map<String, String> =
        Json.decodeFromStream(Core::class.java.getResourceAsStream("/buildInfo.json")!!)
    val minecraftVersion: String = buildInfo["releaseTarget"]!!

    @JvmStatic
    val di = DI {
        bind<Config> { Config.loadConfig() }
        bind<IModDiscoveryService> { ModDiscoveryService() }
        bind<IModService> { ModService(this) }
        bind<IUpdateService> { UpdateService(this) }
    }

}