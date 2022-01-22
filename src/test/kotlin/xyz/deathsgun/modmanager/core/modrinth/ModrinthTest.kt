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

package xyz.deathsgun.modmanager.core.modrinth

import net.minecraft.text.TranslatableText
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import xyz.deathsgun.modmanager.core.ModManager
import xyz.deathsgun.modmanager.core.api.Category
import xyz.deathsgun.modmanager.core.api.http.CategoriesResult
import xyz.deathsgun.modmanager.core.api.http.ModResult
import xyz.deathsgun.modmanager.core.api.http.ModsResult
import xyz.deathsgun.modmanager.core.api.mod.Mod
import xyz.deathsgun.modmanager.core.api.provider.Sorting
import java.util.*

import kotlin.test.*

internal class ModrinthTest {

    private val modrinth = Modrinth()

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            val properties = Properties()
            properties.setProperty("version.target", "1.18")
            properties.setProperty("version.minecraft", "1.18.1")
            ModManager.init(properties)
        }
    }

    @Test
    fun getCategories() {
        val result = modrinth.getCategories()
        if (result is CategoriesResult.Error) {
            fail("Failed to retrieve categories", result.cause)
        }
        val categories = (result as CategoriesResult.Success).categories
        assertTrue(categories.isNotEmpty())
        categories.forEach {
            assertTrue(it.id.isNotEmpty())
            assertEquals(String.format("modmanager.category.%s", it.id), it.text.key)
            assertNotEquals("fabric", it.id)
            assertNotEquals("forge", it.id)
        }
    }
    @Test
    fun getModsBySorting() {
        val result = modrinth.getMods(Sorting.NEWEST, 0, 10)
        if (result is ModsResult.Error) {
            result.cause?.let {
                fail(result.text.key, it)
            }
            fail(result.text.key)
        }
        val mods = (result as ModsResult.Success).mods
        checkMods(mods)
    }

    @Test
    fun getModsByCategory() {
        val result = modrinth.getMods(listOf(Category("misc", TranslatableText(""))), Sorting.RELEVANCE, 0, 10)
        if (result is ModsResult.Error) {
            result.cause?.let {
                fail(result.text.key, it)
            }
            fail(result.text.key)
        }
        val mods = (result as ModsResult.Success).mods
        checkMods(mods)
    }

    @Test
    fun getModsByQuery() {
        val result = modrinth.search("Mod", emptyList(), Sorting.DOWNLOADS, 0, 10)
        if (result is ModsResult.Error) {
            result.cause?.let {
                fail(result.text.key, it)
            }
            fail(result.text.key)
        }
        val mods = (result as ModsResult.Success).mods
        checkMods(mods)
    }

    private fun checkMods(mods: List<Mod>, modsMode: Boolean = true) {
        assertTrue(mods.isNotEmpty())
        if (modsMode) {
            assertEquals(mods.size, 10)
        }
        mods.forEach {
            assertTrue(it.id.isNotEmpty())
            assertTrue(it.slug.isNotEmpty())
            assertTrue(it.name.isNotEmpty())
            assertNotNull(it.iconUrl)
            assertTrue(it.shortDescription.isNotEmpty())
            assertTrue(it.categories.isNotEmpty())

            if (modsMode) {
                assertNotNull(it.author)
                // Only filled when getMod(id) is called
                assertNull(it.description, "description should be null as it's only loaded by getMod")
                assertNull(it.license, "description should be null as it's only loaded by getMod")
                return@forEach
            }
            assertNotNull(it.description, "description shouldn't be null as it's loaded by getMod")
            assertNotNull(it.license, "license shouldn't be null as it's loaded by getMod")
        }
    }

    @Test
    fun getMod() {
        val testMod = modrinth.getMods(Sorting.NEWEST, 0, 1)
        if (testMod is ModsResult.Error) {
            testMod.cause?.let {
                fail(testMod.text.key, it)
            }
            fail(testMod.text.key)
        }
        val result = modrinth.getMod((testMod as ModsResult.Success).mods[0].id)
        if (result is ModResult.Error) {
            result.cause?.let {
                fail(result.text.key, it)
            }
            fail(result.text.key)
        }
        checkMods(listOf((result as ModResult.Success).mod), false)
    }
}