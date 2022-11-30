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

package net.modmanagermc.core.model

/**
 * A representation of a basic version
 *
 * @author DeathsGun
 * @since 1.0.0
 */
data class Version(
    /**
     * The id of the version **not** the mod id
     */
    val id: String,
    /**
     * Provider specific id for the mod
     */
    val providerModId: String,
    val provider: String,
    val version: String,
    val changelog: String,
    /**
     * A list of dependencies for the mod
     */
    val dependencies: List<Dependency>,
    val filename: String,
    val downloadUrl: String,
    val hashes: Map<String, String>,
)
