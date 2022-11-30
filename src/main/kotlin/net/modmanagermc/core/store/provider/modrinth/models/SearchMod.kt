package net.modmanagermc.core.store.provider.modrinth.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.modmanagermc.core.model.Category

@Serializable
data class SearchMod(
    val title: String,
    val description: String,
    val downloads: Int,
    @SerialName("icon_url")
    val iconUrl: String,
    @SerialName("project_id")
    val projectId: String,
    val license: String,
    val categories: List<String>,
    val body: String? = null
) {

    private val blacklisted = listOf("fabric", "quilt", "forge")

    fun toMod(licenses: Map<String, String>): net.modmanagermc.core.model.Mod {
        return net.modmanagermc.core.model.Mod(
            projectId,
            "modrinth",
            title,
            iconUrl,
            description,
            licenses.getOrDefault(license, license.uppercase()),
            categories.filter { !blacklisted.contains(it) }.map { Category(it, "modmanager.category.$it") },
            body
        )
    }
}