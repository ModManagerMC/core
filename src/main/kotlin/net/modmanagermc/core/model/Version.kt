package net.modmanagermc.core.model

/**
 * A representation of a basic version
 *
 * @author DeathsGun
 * @since 1.0.0
 */
data class Version(
    /**
     * The id of the version **not** the mod id
     */
    val id: String,
    /**
     * Provider specific id for the mod
     */
    val providerModId: String,
    /**
     * A list of dependencies for the mod
     */
    val dependencies: List<Dependency>,
    val filename: String,
    val downloadUrl: String,
    val hashes: Map<String, String>,
)
