package net.modmanagermc.core.update.provider.modrinth

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.modmanagermc.core.Core
import net.modmanagermc.core.di.DI
import net.modmanagermc.core.exceptions.ModManagerException
import net.modmanagermc.core.exceptions.NoHashException
import net.modmanagermc.core.model.Dependency
import net.modmanagermc.core.model.JarFileInfo
import net.modmanagermc.core.model.Version
import net.modmanagermc.core.update.IUpdateProvider
import net.modmanagermc.core.update.provider.modrinth.model.ErrorResponse
import net.modmanagermc.core.update.provider.modrinth.model.ModrinthVersion
import net.modmanagermc.core.update.provider.modrinth.model.UpdateRequest
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients

/**
 * Modrinth Provider
 */
class Modrinth(private val di: DI) : IUpdateProvider {

    override val name: String = "modrinth"
    private val client = HttpClients.createMinimal()
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

        val asset = updateResponse.files.firstOrNull() ?: return emptyList()

        return listOf(
            Version(
                updateResponse.id,
                updateResponse.projectId,
                updateResponse.dependencies.mapNotNull { toDependency(it) },
                asset.filename,
                asset.url,
                asset.hashes,
            )
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun getVersion(id: String): ModrinthVersion? {
        val request = HttpGet("https://api.modrinth.com/v2/version/${id}")
        request.setHeader("Accept", "application/json")

        val response = client.execute(request)
        if (response.statusLine.statusCode != 200) {
            val error = json.decodeFromStream<ErrorResponse>(response.entity.content)
            throw error.toException("Received invalid status code ${response.statusLine.statusCode}. Message: %s")
        }
        return try {
            json.decodeFromStream<ModrinthVersion>(response.entity.content)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Creates a dependency tree
    private fun toDependency(dep: ModrinthVersion.Dependency): Dependency? {
        if (dep.versionId == null) {
            return null // Currently, only direct versions supported
        }
        try {
            val version = getVersion(dep.versionId) ?: return null
            val asset = version.files.firstOrNull() ?: return null
            return Dependency(
                asset.url,
                asset.filename,
                dep.dependencyType == "required",
                asset.hashes,
                version.dependencies.mapNotNull { toDependency(it) }
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}