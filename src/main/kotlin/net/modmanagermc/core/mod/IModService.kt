package net.modmanagermc.core.mod

import net.modmanagermc.core.model.JarFileInfo
import net.modmanagermc.core.model.Version
import net.modmanagermc.core.provider.IProvider

/**
 *
 * @author DeathsGun
 * @since Core 1.0.0
 */
interface IModService {

    val providers: List<IProvider>

    suspend fun createJarFileInfo(): List<JarFileInfo>

    fun getVersions(fileInfo: JarFileInfo): List<Version>

    fun registerProvider(provider: IProvider)

}