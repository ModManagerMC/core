/*
 * Copyright 2021-2022 ModManagerMC
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

package xyz.deathsgun.modmanager.core.config

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import java.io.FileNotFoundException
import java.nio.file.Files

data class Config(
    var defaultProvider: String = "modrinth",
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
                val file = FabricLoader.getInstance().configDir.resolve("modmanager.json")
                Files.createDirectories(file.parent)
                val data = Files.readAllBytes(file).decodeToString()
                json.decodeFromString(data)
            } catch (e: Exception) {
                if (e !is FileNotFoundException) {
                    e.printStackTrace()
                }
                saveConfig(Config())
            }
        }

        @OptIn(ExperimentalSerializationApi::class)
        fun saveConfig(config: Config): Config {
            try {
                val file = FabricLoader.getInstance().configDir.resolve("modmanager.json")
                val data = json.encodeToString(config)
                Files.write(file, data.encodeToByteArray())
            } catch (ignored: Exception) {
            }
            return config
        }
    }

    enum class UpdateChannel {
        ALL, STABLE, UNSTABLE;

        fun translation(): Text {
            return TranslatableText(String.format("modmanager.channel.%s", name.lowercase()))
        }
    }

}