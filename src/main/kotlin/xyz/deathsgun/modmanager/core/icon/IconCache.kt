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

package xyz.deathsgun.modmanager.core.icon

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.LogManager
import xyz.deathsgun.modmanager.core.api.http.HttpClient
import xyz.deathsgun.modmanager.core.api.mod.Mod
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors

object IconCache {

    private val logger = LogManager.getLogger("IconCache")
    private val unknownIcon = Identifier("textures/misc/unknown_pack.png")
    private val loadingIcon = Identifier("modmanager", "textures/gui/loading.png")
    private val state: ConcurrentHashMap<String, IconState> = ConcurrentHashMap()

    // FabricLoader.getInstance().configDir.resolve("modmanager").resolve("icons")
    private lateinit var iconsDir: Path

    // MinecraftClient.getInstance().textureManager.registerTexture(icon, NativeImageBackedTexture(image))
    private lateinit var loader: (image: InputStream) -> Unit

    /**
     * Initializes the cache with a directory and a texture loader method
     */
    @JvmStatic
    fun init(dir: Path, textureLoader: (image: InputStream) -> Unit) {
        iconsDir = Files.createDirectories(dir)
        loader = textureLoader
    }

    @JvmStatic
    @OptIn(DelicateCoroutinesApi::class)
    fun get(mod: Mod): Identifier =
        when (this.state[mod.id] ?: IconState.NOT_FOUND) {
            IconState.NOT_FOUND -> {
                GlobalScope.launch {
                    downloadIcon(mod)
                }
                loadingIcon
            }
            IconState.DOWNLOADED -> {
                readIcon(mod)
            }
            IconState.LOADED -> {
                Identifier("modmanager", "mod_icons/${mod.id.lowercase()}")
            }
            IconState.DOWNLOADING -> loadingIcon
            IconState.ERRORED -> unknownIcon
        }

    private fun readIcon(mod: Mod): Identifier {
        return try {
            loader(Files.newInputStream(iconsDir.resolve(mod.id)))
            this.state[mod.id] = IconState.LOADED
            Identifier("modmanager", "mod_icons/${mod.id.lowercase()}")
        } catch (e: Exception) {
            this.state[mod.id] = IconState.ERRORED
            logger.error("Error while loading icon for {}: {}", mod.slug, e.message)
            unknownIcon
        }
    }

    private fun downloadIcon(mod: Mod) {
        if (mod.iconUrl == null) {
            state[mod.id] = IconState.ERRORED
            return
        }
        val iconState = state[mod.id] ?: IconState.NOT_FOUND
        if (iconState != IconState.NOT_FOUND) {
            return
        }
        state[mod.id] = IconState.DOWNLOADING
        try {
            HttpClient.download(mod.iconUrl, iconsDir.resolve(mod.id))
            state[mod.id] = IconState.DOWNLOADED
        } catch (e: Exception) {
            state[mod.id] = IconState.ERRORED
            logger.error("Error while downloading icon for {}: {}", mod.slug, e.message)
        }
    }

    @JvmStatic
    fun destroyAll() {
        for ((mod, state) in state) {
            if (state != IconState.LOADED) {
                continue
            }
            val icon = Identifier("modmanager", "mod_icons/${mod.lowercase()}")
            MinecraftClient.getInstance().textureManager.destroyTexture(icon)
            this.state[mod] = IconState.DOWNLOADED
        }
    }

    @JvmStatic
    fun clear() {
        logger.info("Cleaning icon cache...")
        val files = Files.list(iconsDir)
            .sorted { o1, o2 ->
                o1.toFile().lastModified().compareTo(o2.toFile().lastModified())
            }.collect(Collectors.toList())
        if (files.isEmpty()) {
            logger.info("No cleanup required")
            return
        }
        var i = 0;
        while (Files.list(iconsDir).mapToLong { it.toFile().length() }.sum() >= 10000000) {
            if (files.size <= i) {
                return
            }
            logger.debug("Deleting {}", files[i].fileName)
            Files.delete(files[i])
            i++
        }
        logger.info("Cache cleanup done!")
    }

    private enum class IconState {
        NOT_FOUND, DOWNLOADING, DOWNLOADED, LOADED, ERRORED
    }

}