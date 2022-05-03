package net.modmanagermc.core.update

import net.modmanagermc.core.model.JarFileInfo
import net.modmanagermc.core.model.Version

/**
 * Provides versions for a mod by using [JarFileInfo]
 *
 * @since 1.0.0
 * @author DeathsGun
 */
interface IUpdateProvider {

    val name: String

    fun getVersions(fileInfo: JarFileInfo): List<Version>

}