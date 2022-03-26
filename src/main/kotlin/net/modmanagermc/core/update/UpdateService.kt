package net.modmanagermc.core.update

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import net.modmanagermc.core.config.Config
import net.modmanagermc.core.di.DI
import net.modmanagermc.core.mod.IModService
import net.modmanagermc.core.model.JarFileInfo
import net.modmanagermc.core.model.ProcessingStatus
import org.apache.http.impl.client.HttpClients
import org.apache.logging.log4j.LogManager

@OptIn(DelicateCoroutinesApi::class)
class UpdateService(di: DI) : IUpdateService {

    private val modService: IModService by di
    private val config: Config by di
    private val logger = LogManager.getLogger(UpdateService::class.java)
    private var status: ProcessingStatus = ProcessingStatus.PENDING
    private val client = HttpClients.createDefault()

    override val processingStatus: ProcessingStatus
        get() {
            return status
        }


    @OptIn(ExperimentalSerializationApi::class)
    override fun checkUpdate() {
        GlobalScope.launch(Dispatchers.IO) {
            val modInfos = modService.createJarFileInfo()
            if (modInfos.isEmpty()) {
                logger.info("Skipping update check because 0 mods could be checked!")
                return@launch
            }
            for (fileInfo in modInfos) {
                checkUpdate(fileInfo)
            }
        }
    }

    private fun checkUpdate(fileInfo: JarFileInfo) {
        val versions = modService.getVersions(fileInfo)

    }

}

