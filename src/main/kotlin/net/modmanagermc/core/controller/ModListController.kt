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

package net.modmanagermc.core.controller

import kotlinx.coroutines.*
import net.modmanagermc.core.Core
import net.modmanagermc.core.model.Category
import net.modmanagermc.core.model.Mod
import net.modmanagermc.core.store.IStoreService
import net.modmanagermc.core.store.Sort

@OptIn(DelicateCoroutinesApi::class)
class ModListController(private val view: View) {

    var scrollAmount: Double = 0.0
    private val limit = 20
    private val storeService: IStoreService by Core.di
    private var page = 0
    var query: String = ""
    var sorting: Sort = Sort.DOWNLOADS
    var nextPageAvailable = true
    var previousPageAvailable = false
    var selectedCategories: List<Category> = emptyList()
    var mods: List<Mod> = emptyList()
    var categories: MutableList<Category> = mutableListOf(Category("updatable", "modmanager.category.updatable"))
    var selectedMod: Mod? = null
    var loading: Boolean = false

    fun init() = GlobalScope.launch(Dispatchers.IO) {
        loading = true
        GlobalScope.launch(Dispatchers.Default) {
            delay(1500L)
            if (loading) {
                view.setLoading(true)
            }
        }
        categories.addAll(storeService.categories)
        view.setCategories(categories)
        search()
        loading = false
        view.setLoading(false)
    }

    fun search() = GlobalScope.launch(Dispatchers.IO) {
        loading = true
        GlobalScope.launch(Dispatchers.Default) {
            delay(2000L)
            if (loading) {
                view.setLoading(true)
            }
        }
        try {
            mods = storeService.search(query, selectedCategories, sorting, page, limit)
        } catch (e: Exception) {
            view.error(e)
            return@launch
        }
        nextPageAvailable = mods.size >= limit
        previousPageAvailable = page != 0
        view.setMods(mods)
        view.setScrollAmount(scrollAmount)
        loading = false
        view.setLoading(false)
    }

    fun tick() {
    }

    fun close() {
    }

    fun previousPage() {
        page--
        if (page < 0) {
            page = 0
        }
        search()
    }

    fun nextPage() {
        page++
        search()
    }

    fun reset() {
        query = ""
        page = 0
        sorting = Sort.RELEVANCE
        nextPageAvailable = true
        previousPageAvailable = false
    }

    fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (view.searchFieldFocused && keyCode == 257) {
            page = 0
            view.setScrollAmount(0.0)
            search()
            return true
        }
        return false
    }

    interface View {

        val searchFieldFocused: Boolean

        fun setMods(mods: List<Mod>)
        fun error(e: Exception)
        fun setCategories(categories: List<Category>)
        fun setScrollAmount(scrollAmount: Double)
        fun setLoading(loading: Boolean)

    }

}