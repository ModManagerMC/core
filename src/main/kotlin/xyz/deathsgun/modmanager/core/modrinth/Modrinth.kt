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

import kotlinx.serialization.SerializationException
import net.minecraft.text.TranslatableText
import org.apache.http.client.utils.URIBuilder
import org.apache.logging.log4j.LogManager
import xyz.deathsgun.modmanager.core.ModManager
import xyz.deathsgun.modmanager.core.api.Category
import xyz.deathsgun.modmanager.core.api.http.CategoriesResult
import xyz.deathsgun.modmanager.core.api.http.HttpClient
import xyz.deathsgun.modmanager.core.api.http.ModResult
import xyz.deathsgun.modmanager.core.api.http.ModsResult
import xyz.deathsgun.modmanager.core.api.mod.Mod
import xyz.deathsgun.modmanager.core.api.provider.IModProvider
import xyz.deathsgun.modmanager.core.api.provider.Sorting
import xyz.deathsgun.modmanager.core.modrinth.models.DetailedMod
import xyz.deathsgun.modmanager.core.modrinth.models.SearchResult
import java.io.IOException
import java.net.SocketTimeoutException

class Modrinth : IModProvider {

    private val baseUri: String = "https://api.modrinth.com"
    private val logger = LogManager.getLogger("Modrinth")
    private val categories = ArrayList<Category>()

    override fun getName() = "Modrinth"

    override fun getCategories(): CategoriesResult {
        logger.debug("Getting categories...")
        if (categories.isNotEmpty()) {
            logger.debug("Using cached categories({})", categories.size)
            return CategoriesResult.Success(categories)
        }
        logger.debug("Categories not cached retrieving...")
        return try {
            val categories = HttpClient.getJson<List<String>>("${baseUri}/api/v1/tag/category")
            for (category in categories) {
                if (category == "fabric" || category == "forge") { // Fabric and Forge are not really a categories
                    continue
                }
                this.categories.add(
                    Category(
                        category,
                        TranslatableText(String.format("modmanager.category.%s", category))
                    )
                )
            }
            logger.debug("Retrieved {} categories!", this.categories.size)
            CategoriesResult.Success(this.categories)
        } catch (e: Exception) {
            logger.error("Error while getting categories: {}", e.message)
            CategoriesResult.Error(e.toTranslatable(), e)
        }
    }

    override fun getMods(sorting: Sorting, page: Int, limit: Int): ModsResult {
        val builder = URIBuilder(baseUri).apply {
            path = "/api/v1/mod"
            addParameter("filters", "categories=\"fabric\" AND NOT client_side=\"unsupported\"")
        }
        return getMods(builder, sorting, page, limit)
    }

    override fun getMods(categories: List<Category>, sorting: Sorting, page: Int, limit: Int): ModsResult {
        val builder = URIBuilder(baseUri).apply {
            path = "/api/v1/mod"
            addParameter(
                "filters",
                "categories=\"fabric\" AND NOT client_side=\"unsupported\"${filterFromCategories(categories)}"
            )
        }
        return getMods(builder, sorting, page, limit)
    }

    override fun search(
        query: String,
        categories: List<Category>,
        sorting: Sorting,
        page: Int,
        limit: Int
    ): ModsResult {
        val builder = URIBuilder(baseUri).apply {
            path = "/api/v1/mod"
            addParameter("query", query)
            addParameter(
                "filters",
                "categories=\"fabric\" AND NOT client_side=\"unsupported\"${filterFromCategories(categories)}"
            )
        }
        return getMods(builder, sorting, page, limit)
    }

    override fun getMod(id: String): ModResult {
        val modId = if (id.contains("-")) id.split("-")[1] else id
        return try {
            val result = HttpClient.getJson<DetailedMod>("${baseUri}/api/v1/mod/$modId")
            val categoriesList = ArrayList<Category>()
            result.categories.forEach { categoryId ->
                categoriesList.add(
                    Category(
                        categoryId,
                        TranslatableText("modmanager.category.${categoryId}")
                    )
                )
            }
            ModResult.Success(
                Mod(
                    id = if (result.id.contains("-")) result.id.split("-")[1] else id,
                    slug = result.slug,
                    author = null,
                    name = result.title,
                    shortDescription = result.description,
                    iconUrl = result.iconUrl,
                    description = result.body,
                    license = result.license.name,
                    categories = categoriesList
                )
            )
        } catch (e: Exception) {
            ModResult.Error(e.toTranslatable(), e)
        }
    }

    private fun filterFromCategories(categories: List<Category>): String {
        var categoriesFilter = ""
        for (category in categories) {
            categoriesFilter += "AND categories=\"${category.id}\""
        }
        categoriesFilter = categoriesFilter.replaceFirst("AND ", " AND (")
        if (categories.isNotEmpty()) {
            categoriesFilter += ")"
        }
        return categoriesFilter
    }

    private fun getMods(builder: URIBuilder, sorting: Sorting, page: Int, limit: Int): ModsResult {
        builder.apply {
            addParameter(
                "version",
                String.format(
                    "versions=\"%s\" OR versions=\"%s\"",
                    ModManager.getMinecraftReleaseTarget(),
                    ModManager.getMinecraftVersion()
                )
            )
            addParameter("index", sorting.name.lowercase())
            addParameter("offset", (page * limit).toString())
            addParameter("limit", limit.toString())
        }
        return try {
            ModsResult.Success(HttpClient.getJson<SearchResult>(builder.build()).toList())
        } catch (e: Exception) {
            logger.error("Error while requesting mods {}", e)
            ModsResult.Error(e.toTranslatable(), e)
        }
    }

    private fun Exception.toTranslatable(): TranslatableText {
        return when (this) {
            is HttpClient.InvalidStatusCodeException -> TranslatableText(
                "modmanager.error.invalidStatus",
                statusCode
            )
            is SerializationException -> TranslatableText("modmanager.error.failedToParse", message)
            is SocketTimeoutException -> TranslatableText("modmanager.error.network", message)
            is IOException -> TranslatableText("modmanager.error.network", message)
            else -> TranslatableText("modmanager.error.unknown", message)
        }
    }

}