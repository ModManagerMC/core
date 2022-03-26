package net.modmanagermc.core.discover

import net.fabricmc.loader.api.metadata.ModMetadata
import java.nio.file.Path

/**
 * This service helps to interact with
 * the mod's directory.
 *
 * @author DeathsGun
 * @see ModDiscoveryService
 * @since Core 1.0.0
 */
interface IModDiscoveryService {

    /**
     * Returns a list of all mods which should be checked
     * for updates.
     * Mods which have update checking disabled are excluded.
     * Additional to the java and minecraft dummy mod.
     * @return a list of [ModMetadata]
     */
    fun getMods(): List<ModMetadata>

    /**
     * Returns the JAR of the given mod id.
     *
     * @return the [Path] of the JAR or null if not found
     */
    fun getJar(modId: String): Path?

}