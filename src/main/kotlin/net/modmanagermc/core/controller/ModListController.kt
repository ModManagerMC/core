package net.modmanagermc.core.controller

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.modmanagermc.core.Core
import net.modmanagermc.core.model.Category
import net.modmanagermc.core.model.Mod
import net.modmanagermc.core.store.IStoreService
import kotlin.reflect.KFunction

@OptIn(DelicateCoroutinesApi::class)
class ModListController(private val view: View) {

    private val limit = 20
    private val storeService: IStoreService by Core.di
    private var page = 0
    var query: String = ""
    var nextPageAvailable = true
    var previousPageAvailable = false
    var selectedCategories: List<Category> = emptyList()
    var mods: List<Mod> = emptyList()
    var categories: List<Category> = emptyList()

    fun init() {
        categories = storeService.categories
        search()
    }

    fun search() = GlobalScope.launch(Dispatchers.Default) {
        mods = storeService.search(query, selectedCategories, page, limit)
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

    interface View {

       fun setMods(mods: List<Mod>)

    }

}