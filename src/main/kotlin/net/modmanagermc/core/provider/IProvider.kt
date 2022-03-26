package net.modmanagermc.core.provider

import net.modmanagermc.core.model.JarFileInfo
import net.modmanagermc.core.model.Version

interface IProvider {

    val name: String

    fun getVersions(fileInfo: JarFileInfo): List<Version>

}