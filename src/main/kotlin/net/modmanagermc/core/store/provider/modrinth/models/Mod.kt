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

package net.modmanagermc.core.store.provider.modrinth.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.modmanagermc.core.model.Category

@Serializable
data class Mod(
    val title: String,
    val description: String,
    val downloads: Int,
    @SerialName("icon_url")
    val iconUrl: String,
    @SerialName("id")
    val projectId: String,
    val license: LicenseModel,
    val categories: List<String>,
    val body: String? = null
) {

    private val blacklisted = listOf("fabric", "quilt", "forge")

    fun toMod(): net.modmanagermc.core.model.Mod {
        return net.modmanagermc.core.model.Mod(
            projectId,
            "modrinth",
            title,
            iconUrl,
            description,
            license.name,
            categories.filter { !blacklisted.contains(it) }.map { Category(it, "modmanager.category.$it") },
            body
        )
    }
}