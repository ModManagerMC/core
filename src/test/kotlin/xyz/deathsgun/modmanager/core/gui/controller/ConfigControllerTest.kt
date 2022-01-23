/*
 * Copyright 2022 ModManagerMC
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

package xyz.deathsgun.modmanager.core.gui.controller

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import xyz.deathsgun.modmanager.core.ModManager
import xyz.deathsgun.modmanager.core.config.Config
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

internal class ConfigControllerTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setUp() {
            ModManager.init(Properties())
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            Paths.get("config", "modmanager", "modmanager.json").toFile().delete()
        }
    }

    @Test
    fun saveConfig() {
        assertTrue(Files.exists(Paths.get("config", "modmanager", "modmanager.json")))
        val localController = ConfigController()
        assertEquals(ModManager.config.defaultProvider, localController.provider)
        assertEquals(ModManager.config.hidden, localController.hidden)
        assertEquals(ModManager.config.updateChannel, localController.updateChannel)

        localController.hidden.add("sodium")
        localController.provider = "curseforge"
        localController.updateChannel = Config.UpdateChannel.STABLE
        assertNotEquals(localController.hidden, ModManager.config.hidden)
        assertNotEquals(localController.provider, ModManager.config.defaultProvider)
        assertNotEquals(localController.updateChannel, ModManager.config.updateChannel)

        localController.saveConfig()
        assertEquals(localController.hidden, ModManager.config.hidden)
        assertEquals(localController.provider, ModManager.config.defaultProvider)
        assertEquals(localController.updateChannel, ModManager.config.updateChannel)
    }

    @Test
    fun nextChannel() {
        ModManager.config = Config()
        val controller = ConfigController()
        controller.updateChannel = Config.UpdateChannel.values()[0]
        for (k in 0..3) {
            for (i in 0 until Config.UpdateChannel.values().size) {
                assertEquals(Config.UpdateChannel.values()[i], controller.updateChannel)
                controller.nextChannel()
            }
        }
    }
}