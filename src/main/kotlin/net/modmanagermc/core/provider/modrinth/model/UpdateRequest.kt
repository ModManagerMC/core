package net.modmanagermc.core.provider.modrinth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRequest(
    val loaders: List<String>,
    @SerialName("game_versions")
    val gameVersions: List<String>
)
