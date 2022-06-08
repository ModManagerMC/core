package net.modmanagermc.core.exceptions

open class ModManagerException(val translationId: String, vararg val args: Any) : Exception() {
}