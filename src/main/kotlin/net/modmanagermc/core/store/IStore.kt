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

package net.modmanagermc.core.store

import net.modmanagermc.core.model.Category
import net.modmanagermc.core.model.Mod

/**
 * Provides info about categories and mods
 *
 * @since 1.0.0
 * @author DeathsGun
 */
interface IStore {

    val name: String

    fun search(
        query: String,
        categories: List<Category>,
        sort: Sort,
        page: Int,
        limit: Int
    ): List<Mod>

    fun getCategories(): List<Category>
    fun getMod(mod: Mod): Mod?

    fun getMod(mod: String): Mod?

    fun getLicences(): Map<String, String>

}