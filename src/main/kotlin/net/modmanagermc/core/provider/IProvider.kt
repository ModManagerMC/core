package net.modmanagermc.core.provider

import net.modmanagermc.core.model.JarFileInfo
import net.modmanagermc.core.model.Version

/**
 * Provides versions for a mod by using [JarFileInfo]
 *
 * @since Core 1.0.0
 * @author DeathsGun
 */
interface IProvider {

    val name: String

    fun getVersions(fileInfo: JarFileInfo): List<Version>

}