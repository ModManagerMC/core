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

package net.modmanagermc.core.update

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.modmanagermc.core.di.DI
import net.modmanagermc.core.mod.IModService
import net.modmanagermc.core.model.JarFileInfo
import net.modmanagermc.core.model.Mod
import net.modmanagermc.core.model.ProcessingStatus
import net.modmanagermc.core.model.Version
import org.apache.logging.log4j.LogManager

@OptIn(DelicateCoroutinesApi::class)
internal class UpdateService(di: DI) : IUpdateService {

    private val modService: IModService by di
    private val logger = LogManager.getLogger("ModManager|UpdateService")
    private var status: ProcessingStatus = ProcessingStatus.PENDING
    private val _updates: MutableList<Update> = mutableListOf()

    override val processingStatus: ProcessingStatus
        get() {
            return status
        }
    override val updates: List<Update>
        get() = _updates

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

    override fun getUpdate(mod: Mod): Update? {
        return updates.find { it.storeIds.containsValue(mod.id) }
    }

    private fun checkUpdate(fileInfo: JarFileInfo) {
        val versions = modService.getNewerVersions(fileInfo)
        if (versions.isEmpty()) {
            logger.info("No updates for {} found", fileInfo.modId)
            return
        }
        val storeIds = mutableMapOf(versions[0].provider to versions[0].providerModId)
        val update = Update(fileInfo.modId, storeIds, versions[0])
        logger.info("Update for {} ({} -> {}) found", fileInfo.modId, fileInfo.version, update.version.version)
        _updates.add(update)
    }

}

