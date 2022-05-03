package net.modmanagermc.core.mod

import net.modmanagermc.core.model.JarFileInfo
import net.modmanagermc.core.model.Version
import net.modmanagermc.core.update.IUpdateProvider

/**
 * @author DeathsGun
 * @since 1.0.0
 */
interface IModService {

    val providers: List<IUpdateProvider>

    suspend fun createJarFileInfo(): List<JarFileInfo>

    fun getNewerVersions(fileInfo: JarFileInfo): List<Version>

    fun registerProvider(provider: IUpdateProvider)

}