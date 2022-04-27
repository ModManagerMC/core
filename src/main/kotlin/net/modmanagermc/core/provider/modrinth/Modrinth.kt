package net.modmanagermc.core.provider.modrinth

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.modmanagermc.core.Core
import net.modmanagermc.core.di.DI
import net.modmanagermc.core.exceptions.ModManagerException
import net.modmanagermc.core.exceptions.NoHashException
import net.modmanagermc.core.model.JarFileInfo
import net.modmanagermc.core.model.Version
import net.modmanagermc.core.provider.IProvider
import net.modmanagermc.core.provider.modrinth.model.ErrorResponse
import net.modmanagermc.core.provider.modrinth.model.ModrinthVersion
import net.modmanagermc.core.provider.modrinth.model.UpdateRequest
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients

class Modrinth(private val di: DI) : IProvider {

    override val name: String = "modrinth"
    private val client = HttpClients.createDefault()
    private val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * Uses the following url: https://api.modrinth.com/v2/version_file/{hash}/update?algorithm={algorithm}
     *
     * @throws NoHashException if no SHA-512 or SHA-1 is provided
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Throws(ModManagerException::class)
    override fun getVersions(fileInfo: JarFileInfo): List<Version> {
        val hash = fileInfo.hashes["SHA-512"] ?: fileInfo.hashes["SHA-1"]
        ?: throw NoHashException("No SHA-512 or SHA-1 for ${fileInfo.modId}")

        val algorithm = if (fileInfo.hashes.containsKey("SHA-512")) "sha512" else "sha1"

        val request = HttpPost("https://api.modrinth.com/v2/version_file/$hash/update?algorithm=$algorithm")
        request.setHeader("Accept", "application/json")
        request.setHeader("Content-Type", "application/json")
        request.entity = StringEntity(
            Json.encodeToString(
                UpdateRequest(
                    listOf("fabric"),
                    listOf(Core.getMinecraftVersion(di))
                )
            )
        )

        val response = client.execute(request)
        if (response.statusLine.statusCode != 200) {
            val error = json.decodeFromStream<ErrorResponse>(response.entity.content)
            throw error.toException("Received invalid status code ${response.statusLine.statusCode}. Message: %s")
        }
        val updateResponse = try {
            json.decodeFromStream<ModrinthVersion>(response.entity.content)
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
        return listOf(updateResponse.toVersion())
    }
}