/*
 * Copyright (c) 2022 DeathsGun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.modmanagermc.core.extensions

import net.fabricmc.loader.api.metadata.ModMetadata

/**
 * Extracts the update provider from `modmanager` custom space in `fabric.mod.json`
 *
 * @author DeathsGun
 * @since 1.0.0
 */
fun ModMetadata.getUpdateProvider(): Map<String, String> {
    val providers = mutableMapOf<String, String>()
    if (!containsCustomValue("modmanager")) {
        providers["modrinth"] = ""
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
 * @since 1.0.0
 */
fun ModMetadata.updatesDisabled(): Boolean {
    if (!this.containsCustomValue("modmanager")) {
        return false
    }
    val modmanager = this.getCustomValue("modmanager").asObject
    return modmanager.containsKey("disable-checking") && modmanager.get("disable-checking").asBoolean
}
