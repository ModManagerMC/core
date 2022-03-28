package net.modmanagermc.core.config

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths

/**
 * ModManager's config representation
 *
 * Currently, only holds information about the [UpdateChannel]
 * and the mods to hide from the update page.
 *
 * @author DeathsGun
 * @since ModManager 1.0.0
 */
@Serializable
data class Config(
    var updateChannel: UpdateChannel = UpdateChannel.ALL,
    var hidden: ArrayList<String> = ArrayList()
) {

    companion object {

        private val json = Json {
            prettyPrint = true
            encodeDefaults = true
        }

        @OptIn(ExperimentalSerializationApi::class)
        fun loadConfig(): Config {
            return try {
                val file = Paths.get("config", "modmanager", "modmanager.json")
                Files.createDirectories(file.parent)
                val data = Files.readAllBytes(file).decodeToString()
                json.decodeFromString(data)
            } catch (e: Exception) {
                if (e !is FileNotFoundException && e !is NoSuchFileException) {
                    e.printStackTrace()
                }
                saveConfig(Config())
            }
        }

        @OptIn(ExperimentalSerializationApi::class)
        fun saveConfig(config: Config): Config {
            try {
                val dir = Paths.get("config", "modmanager")
                dir.toFile().mkdirs()
                val file = dir.resolve("modmanager.json")
                val data = json.encodeToString(config)
                Files.write(file, data.encodeToByteArray())
            } catch (ignored: Exception) {
            }
            return config
        }
    }

    /**
     * The [UpdateChannel] allows the user to control which type
     * of update he receives.
     * [ALL] -> Release, Alpha and Beta
     * [STABLE] -> Only Release
     * [UNSTABLE] -> Only Alpha and Beta's
     */
    enum class UpdateChannel {
        ALL, STABLE, UNSTABLE;

        fun translationKey(): String {
            return String.format("modmanager.channel.%s", name.lowercase())
        }
    }

}