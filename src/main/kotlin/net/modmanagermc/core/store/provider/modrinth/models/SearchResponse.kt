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
        val license: String,
        val categories: List<String>
    ) {

        private val blacklisted = listOf("fabric", "quilt", "forge")

        fun toMod(licenses: Map<String, String>): net.modmanagermc.core.model.Mod {
            return net.modmanagermc.core.model.Mod(
                projectId,
                title,
                iconUrl,
                description,
                author,
                licenses.getOrDefault(license, license.uppercase()),
                categories.filter { !blacklisted.contains(it) }.map { Category(it, "modmanager.category.$it") },
                null
            )
        }
    }
}
