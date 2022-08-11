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

import net.modmanagermc.core.di.DI
import net.modmanagermc.core.model.Category
import net.modmanagermc.core.model.Mod
import net.modmanagermc.core.store.provider.modrinth.Modrinth

internal class StoreService(di: DI) : IStoreService {

    // Store to use
    override var store: String = "modrinth"

    // Cache stores
    private val _categories: MutableMap<String, List<Category>> = mutableMapOf()
    private val stores: MutableList<IStore> = mutableListOf(Modrinth(di))
    private val modCache: MutableMap<String, List<Mod>> = mutableMapOf()
    override val licenses: Map<String, String>

    init {
        licenses = getStore()?.getLicences() ?: emptyMap()
    }

    override val categories: List<Category>
        get() {
            var cate = _categories.getOrDefault(store, emptyList())
            if (cate.isNotEmpty()) {
                return cate
            }
            val store = getStore() ?: return emptyList()
            cate = store.getCategories()
            _categories[store.name] = cate
            return cate
        }

    override fun search(query: String, categories: List<Category>, sort: Sort, page: Int, limit: Int): List<Mod> {
        val store = getStore() ?: return emptyList()
        val id = searchId(query, categories, sort, page, limit)
        var mods = modCache.getOrDefault(id, emptyList())
        if (mods.isEmpty()) {
            mods = store.search(query, categories, sort, page, limit)
            modCache[id] = mods
        }
        return mods
    }

    override fun getMod(mod: Mod): Mod? {
        return getStore()?.getMod(mod)
    }

    private fun getStore(): IStore? {
        return stores.find { it.name == store }
    }

    private fun searchId(query: String, categories: List<Category>, sort: Sort, page: Int, limit: Int): String {
        return "${query}|${categories.joinToString(".")}|${sort}|${page}|${limit}"
    }

}