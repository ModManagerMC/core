package net.modmanagermc.core.model

data class JarFileInfo(
    val modId: String,
    val version: String,
    val provider: Map<String, String>,
    val hashes: Map<String, String>
)
