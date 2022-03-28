package net.modmanagermc.core.mod

import net.fabricmc.loader.api.FabricLoader
import net.modmanagermc.core.di.DI
import net.modmanagermc.core.discover.IModDiscoveryService
import net.modmanagermc.core.extensions.generateHashes
import net.modmanagermc.core.extensions.getUpdateProvider
import net.modmanagermc.core.model.JarFileInfo
import net.modmanagermc.core.model.Version
import net.modmanagermc.core.provider.IProvider
import net.modmanagermc.core.provider.modrinth.Modrinth
import org.apache.logging.log4j.LogManager

class ModService(di: DI) : IModService {

    private val logger = LogManager.getLogger(ModService::class.java)
    private val providerList: MutableList<IProvider> = mutableListOf(Modrinth(di))
    override val providers: List<IProvider> get() = providerList
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
                    FabricLoader.getInstance().gameDir.resolve("mods")
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
        logger.info("Checking {} from {} mods", modInfos.size, mods.size)
        return modInfos
    }

    override fun getVersions(fileInfo: JarFileInfo): List<Version> {
        val versions = mutableListOf<Version>()
        for ((providerName) in fileInfo.provider) {
            val provider = providers.find { it.name == providerName }
            if (provider == null) {
                logger.warn("Mod ${fileInfo.modId} requested provider $providerName but it's not available")
                continue
            }
            val providerVersions = provider.getVersions(fileInfo)

        }
        return versions
    }

    override fun registerProvider(provider: IProvider) {
        logger.debug("Registering provider ${provider.name}")
        this.providerList.add(provider)
    }
}