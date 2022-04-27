package net.modmanagermc.core.extensions

import net.fabricmc.loader.api.metadata.ModMetadata

/**
 * Extracts the update provider from `modmanager` custom space in `fabric.mod.json`
 *
 * @author DeathsGun
 * @since Core 1.0.0
 */
fun ModMetadata.getUpdateProvider(): Map<String, String> {
    val providers = mutableMapOf<String, String>()
    if (!containsCustomValue("modmanager")) {
        return providers
    }
    getCustomValue("modmanager").asObject.forEach {
        if (it.key != "disable-checking") {
            return@forEach
        }
        providers[it.key] = it.value.asString
    }
    if (providers.isEmpty()) {
        providers["modrinth"] = ""
    }
    return providers
}

/**
 * Checks for the disable-checking in `modmanager` custom space in `fabric.mod.json`
 *
 * @author DeathsGun
 * @since Core 1.0.0
 */
fun ModMetadata.updatesDisabled(): Boolean {
    if (!this.containsCustomValue("modmanager")) {
        return false
    }
    val modmanager = this.getCustomValue("modmanager").asObject
    return modmanager.containsKey("disable-checking") && modmanager.get("disable-checking").asBoolean
}
