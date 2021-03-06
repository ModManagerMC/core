package net.modmanagermc.core.update.provider.modrinth.model

import kotlinx.serialization.Serializable
import net.modmanagermc.core.exceptions.ModManagerException

@Serializable
data class ErrorResponse(
    val description: String?,
    val error: String?
) {

    fun toException(message: String): ModManagerException {
        return when (error) {
            "not_found" -> ModManagerException(String.format(message, description))
            else -> ModManagerException(String.format(message, description ?: "no detailed error message provided"))
        }
    }
}
