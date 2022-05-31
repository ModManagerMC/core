package net.modmanagermc.core.store.provider.modrinth.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.modmanagermc.core.model.Category

@Serializable
data class Category(
    val name: String,
    @SerialName("project_type")
    val projectType: String
) {
    fun toCategory(): Category {
        return Category(name, "modmanager.category.${name.lowercase()}")
    }
}
