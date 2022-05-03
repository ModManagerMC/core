package net.modmanagermc.core.model

/**
 * Extracted info about a mod
 *
 * @author DeathsGun
 * @since 1.0.0
 */
data class JarFileInfo(
    val modId: String,
    val version: String,
    val provider: Map<String, String>,
    val hashes: Map<String, String>
)
