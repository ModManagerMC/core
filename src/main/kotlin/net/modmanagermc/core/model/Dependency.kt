package net.modmanagermc.core.model

/**
 * A dependency for a mod which can also have dependencies
 *
 * @author DeathsGun
 * @since 1.0.0
 */
data class Dependency(
    val downloadUrl: String,
    val filename: String,
    val required: Boolean,
    val hashes: Map<String, String>,
    val dependencies: List<Dependency>
)
