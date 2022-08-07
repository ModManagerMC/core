package net.modmanagermc.core.extensions

import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.metadata.ModMetadata
import net.fabricmc.loader.impl.metadata.DependencyOverrides
import net.fabricmc.loader.impl.metadata.ModMetadataParser
import net.fabricmc.loader.impl.metadata.VersionOverrides
import java.nio.file.Path
import java.security.MessageDigest
import java.util.jar.JarFile
import kotlin.io.path.absolutePathString
import kotlin.io.path.isDirectory
import kotlin.io.path.readBytes

val hashes = listOf("SHA-1", "SHA-512", "MD5")

/**
 * Opens the file as a JAR and reads the fabric.mod.json from it
 * @return [ModMetadata] if not found or else null
 *
 * @author DeathsGun
 * @since 1.0.0
 */
fun Path.readMetadata(fabricLoader: FabricLoader): ModMetadata? {
    try {
        val jar = JarFile(this.toFile())
        val entry = jar.getJarEntry("fabric.mod.json") ?: return null
        jar.getInputStream(entry).use {
            return ModMetadataParser.parseMetadata(
                it,
                this.absolutePathString(),
                emptyList(),
                VersionOverrides(),
                DependencyOverrides(fabricLoader.configDir)
            )
        }
    } catch (_: Exception) {
    }
    return null
}

/**
 * Hashes the file using the algorithms declared in [hashes].
 *
 * @author DeathsGun
 * @since 1.0.0
 * @return A map of the hashes or if the path is a directory an empty map
 */
fun Path.generateHashes(): Map<String, String> {
    if (this.isDirectory()) {
        return emptyMap()
    }
    val map = mutableMapOf<String, String>()
    for (hash in hashes) {
        map[hash] = hash(hash)
    }
    return map
}

/**
 * Hashes the path using the provided algorithm
 *
 * @param algorithm the hashing algorithm
 */
fun Path.hash(algorithm: String): String {
    return MessageDigest.getInstance(algorithm).digest(readBytes())
        .joinToString("") {
            "%02x".format(it)
        }
}
