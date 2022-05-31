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
import kotlin.collections.ArrayList

internal class ModService(di: DI) : IModService {

    private val fabricLoader: FabricLoader by di
    private val logger = LogManager.getLogger(ModService::class.java)
    private val providerList: MutableList<IUpdateProvider> = mutableListOf(Modrinth(di))
    override val providers: List<IUpdateProvider> get() = providerList
    private val discoveryService: IModDiscoveryService by di

    override suspend fun createJarFileInfo(): List<JarFileInfo> {
        val mods = discoveryService.getMods()
        logger.info("Processing {} mods", mods.size)
        val modInfos = ArrayList<JarFileInfo>()
        for (mod in mods) {
            val path = discoveryService.getJar(mod.id)
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
                continue
            }
            versions.add(version);
        }
        return versions
    }

    override fun registerProvider(provider: IUpdateProvider) {
        logger.debug("Registering provider ${provider.name}")
        this.providerList.add(provider)
    }
}