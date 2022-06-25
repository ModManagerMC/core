package net.modmanagermc.core.model

data class Mod(
    val id: String,
    val name: String,
    val iconUrl: String?,
    val description: String,
    val author: String,
    val license: String,
    val categories: List<Category>,
    val fullDescription: String?,
)
