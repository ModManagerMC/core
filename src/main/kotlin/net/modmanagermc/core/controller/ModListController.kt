package net.modmanagermc.core.controller

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.modmanagermc.core.Core
import net.modmanagermc.core.model.Category
import net.modmanagermc.core.model.Mod
import net.modmanagermc.core.store.IStoreService
import net.modmanagermc.core.store.Sort

@OptIn(DelicateCoroutinesApi::class)
class ModListController(private val view: View) {

    private val limit = 20
    private val storeService: IStoreService by Core.di
    private var page = 0
    var query: String = ""
    var sorting: Sort = Sort.DOWNLOADS
    var nextPageAvailable = true
    var previousPageAvailable = false
    var selectedCategories: List<Category> = emptyList()
    var mods: List<Mod> = emptyList()
    var categories: List<Category> = emptyList()

    fun init() = GlobalScope.launch(Dispatchers.IO) {
        categories = storeService.categories
        view.setCategories(categories)
        search()
    }

    fun search() = GlobalScope.launch(Dispatchers.Default) {
        try {
            mods = storeService.search(query, selectedCategories, sorting, page, limit)
        } catch (e: Exception) {
            view.error(e)
            return@launch
        }
        nextPageAvailable = mods.size >= limit
        previousPageAvailable = page != 0
        view.setMods(mods)
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

    interface View {

        fun setMods(mods: List<Mod>)
        fun error(e: Exception)
        fun setCategories(categories: List<Category>)

    }

}