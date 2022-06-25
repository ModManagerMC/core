package net.modmanagermc.core.store.provider.modrinth.models

import net.modmanagermc.core.model.Mod

@kotlinx.serialization.Serializable
data class DetailedMod(
    val body: String?
) {

    fun toMod(mod: Mod): Mod {
        return mod.copy(fullDescription = body)
    }
}
