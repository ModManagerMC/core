package net.modmanagermc.core.discover

import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.metadata.ModMetadata
import net.modmanagermc.core.extensions.readMetadata
import net.modmanagermc.core.extensions.updatesDisabled
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension

/**
 * Implementation of [IModDiscoveryService]
 *
 * @author DeathsGun
 * @since Core 1.0.0
 */
class ModDiscoveryService : IModDiscoveryService {

    private val blocked = listOf("minecraft", "java")
    private val jars = mutableMapOf<String, String>()

    override fun getMods(): List<ModMetadata> {
        return FabricLoader.getInstance().allMods.map { it.metadata }
            .filter {
                !it.updatesDisabled() && !blocked.contains(it.id) &&
                        !it.containsCustomValue("fabric-api:module-lifecycle") &&
                        !it.containsCustomValue("fabric-loom:generated")
            }
    }

    override fun getJar(modId: String): Path? {
        if (jars.containsKey(modId)) {
            return Path.of(jars[modId]!!)
        }
        val files =
            Files.walk(FabricLoader.getInstance().gameDir.resolve("mods")).filter { "jar".equals(it.extension, true) }
        files.forEach {
            val metadata = it.readMetadata() ?: return@forEach
            jars[metadata.id] = it.toFile().absolutePath
        }
        val path = jars[modId] ?: return null
        return Path.of(path)
    }

}