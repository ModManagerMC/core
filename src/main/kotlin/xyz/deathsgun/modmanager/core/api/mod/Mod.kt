/*
 * Copyright 2021-2022 ModManagerMC
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

package xyz.deathsgun.modmanager.core.api.mod

import xyz.deathsgun.modmanager.core.api.Category

data class Mod(
    val id: String,
    val slug: String,
    var author: String?,
    val name: String,
    var shortDescription: String,
    val iconUrl: String?,
    var description: String?,
    val license: String?,
    val categories: List<Category>,
)
