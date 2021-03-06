package net.modmanagermc.core.update

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.modmanagermc.core.di.DI
import net.modmanagermc.core.mod.IModService
import net.modmanagermc.core.model.JarFileInfo
import net.modmanagermc.core.model.ProcessingStatus
import org.apache.logging.log4j.LogManager

@OptIn(DelicateCoroutinesApi::class)
internal class UpdateService(di: DI) : IUpdateService {

    private val modService: IModService by di
    private val logger = LogManager.getLogger("ModManager|UpdateService")
    private var status: ProcessingStatus = ProcessingStatus.PENDING
    private val updates: MutableList<Update> = mutableListOf()

    override val processingStatus: ProcessingStatus
        get() {
            return status
        }

    override fun checkUpdate() {
        logger.info("Checking for updates...")
        GlobalScope.launch(Dispatchers.IO) {
            val modInfos = modService.createJarFileInfo()
            if (modInfos.isEmpty()) {
                logger.info("Skipping update check because 0 mods could be checked!")
                return@launch
            }
            for (fileInfo in modInfos) {
                try {
                    checkUpdate(fileInfo)
                } catch (e: Exception) {
                    logger.error("Failed to check for updates for mod '{}': {}", fileInfo.modId, e)
                }
            }
            status = ProcessingStatus.DONE
        }
    }

    private fun checkUpdate(fileInfo: JarFileInfo) {
        val versions = modService.getNewerVersions(fileInfo)
        if (versions.isEmpty()) {
            logger.info("No updates for {} found", fileInfo.modId)
            return
        }
        val update = Update(fileInfo.modId, versions[0])
        logger.info("Update for {} ({} -> {}) found", fileInfo.modId, fileInfo.version, update.version.version)
        updates.add(update)
    }

}

