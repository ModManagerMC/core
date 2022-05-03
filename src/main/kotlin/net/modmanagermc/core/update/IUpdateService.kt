package net.modmanagermc.core.update

import net.modmanagermc.core.model.ProcessingStatus

/**
 * Processes the installed mods and sends them to
 * the ModManager backend.
 *
 * @since 1.0.0
 * @author DeathsGun
 */
interface IUpdateService {

    val processingStatus: ProcessingStatus

    fun checkUpdate()

}