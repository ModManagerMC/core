/*
 * Copyright (c) 2021-2022 DeathsGun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.modmanagermc.core.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.modmanagermc.core.config.Config.UpdateChannel
import net.modmanagermc.core.config.Config.UpdateChannel.*
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Paths
import java.util.Arrays
import java.util.StringJoiner

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
    var updateChannel: UpdateChannel = ALL,
    var hidden: ArrayList<String> = ArrayList(),
    var remove: ArrayList<String> = ArrayList()
) {

    companion object {

        private val json = Json {
            prettyPrint = true
            encodeDefaults = true
        }

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
                Config().save()
            }
        }
    }

    fun save(): Config {
        try {
            val dir = Paths.get("config", "modmanager")
            dir.toFile().mkdirs()
            val file = dir.resolve("modmanager.json")
            val data = json.encodeToString(this)
            Files.write(file, data.encodeToByteArray())
        } catch (ignored: Exception) {
        }
        return this
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