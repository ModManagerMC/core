package net.modmanagermc.core.store.provider.modrinth.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val hits: List<Mod>,
) {
    @Serializable
    data class Mod(
        val title: String,
        val description: String,
        val downloads: Int,
        @SerialName("icon_url")
        val iconUrl: String,
        @SerialName("project_id")
        val projectId: String,
        val author: String,
        val license: String
    ) {
        fun toMod(): net.modmanagermc.core.model.Mod {
            return net.modmanagermc.core.model.Mod(projectId, title, iconUrl, description)
        }
    }
}
