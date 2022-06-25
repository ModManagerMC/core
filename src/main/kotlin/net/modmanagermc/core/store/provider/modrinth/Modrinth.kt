package net.modmanagermc.core.store.provider.modrinth

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.modmanagermc.core.Core
import net.modmanagermc.core.exceptions.ModManagerException
import net.modmanagermc.core.model.Category
import net.modmanagermc.core.model.Mod
import net.modmanagermc.core.store.IStore
import net.modmanagermc.core.store.Sort
import net.modmanagermc.core.store.provider.modrinth.models.DetailedMod
import net.modmanagermc.core.store.provider.modrinth.models.SearchResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

@OptIn(ExperimentalSerializationApi::class)
class Modrinth : IStore {

    private val uri = "https://api.modrinth.com/v2"
    private val client = HttpClients.createDefault()
    private val json = Json {
        ignoreUnknownKeys = true
    }

    override val name: String = "modrinth"

    override fun search(query: String, categories: List<Category>, sort: Sort, page: Int, limit: Int): List<Mod> {
        val uri = URIBuilder("${uri}/search").apply {
            addParameter("query", query)
            addParameter("offset", "${page * limit}")
            addParameter("limit", "$limit")
            addParameter("index", sort.toModrinth())
            addParameter("facets", buildFacets(categories))
        }
        val request = HttpGet(uri.build())
        request.setHeader("Accept", "application/json")
        val response = client.execute(request)

        if (response.statusLine.statusCode != 200) {
            EntityUtils.consume(response.entity)
            throw ModManagerException("modmanager.error.status", response.statusLine.statusCode)
        }

        val searchResponse = json.decodeFromStream<SearchResponse>(response.entity.content)
        EntityUtils.consume(response.entity)
        return searchResponse.hits.map { it.toMod() }
    }

    private fun buildFacets(categories: List<Category>): String {
        var result =
            "[[\"categories:fabric\"],[\"versions:${Core.getMinecraftVersion(Core.di)}\"],[\"project_type:mod\"]"
        for (category in categories) {
            result += ",[\"categories:${category.id}\"]"
        }
        return "$result]"
    }

    override fun getCategories(): List<Category> {
        val request = HttpGet("${uri}/tag/category")
        request.addHeader("Accept", "application/json")
        val response = client.execute(request)
        if (response.statusLine.statusCode != 200) {
            EntityUtils.consume(response.entity)
            throw ModManagerException("modmanager.error.status", response.statusLine.statusCode)
        }
        val categories =
            json.decodeFromStream<List<net.modmanagermc.core.store.provider.modrinth.models.Category>>(response.entity.content)
        EntityUtils.consume(response.entity)
        return categories.filter { it.projectType == "mod" }.map { it.toCategory() }
    }

    override fun getMod(mod: Mod): Mod {
        val request = HttpGet("${uri}/project/${mod.id}")
        request.addHeader("Accept", "application/json")
        val response = client.execute(request)
        if (response.statusLine.statusCode != 200) {
            EntityUtils.consume(response.entity)
            throw ModManagerException("modmanager.error.status", response.statusLine.statusCode)
        }
        val detailedMod = json.decodeFromStream<DetailedMod>(response.entity.content)
        EntityUtils.consume(response.entity)
        return detailedMod.toMod(mod)
    }

    private fun Sort.toModrinth(): String {
        return name.lowercase()
    }

}