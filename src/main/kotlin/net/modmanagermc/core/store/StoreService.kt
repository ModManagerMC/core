package net.modmanagermc.core.store

import net.modmanagermc.core.model.Category
import net.modmanagermc.core.model.Mod
import net.modmanagermc.core.store.provider.modrinth.Modrinth

internal class StoreService : IStoreService {

    // Store to use
    override var store: String = "modrinth"

    // Cache stores
    private val _categories: MutableMap<String, List<Category>> = mutableMapOf()
    private val stores: MutableList<IStore> = mutableListOf(Modrinth())
    private val modCache: MutableMap<String, List<Mod>> = mutableMapOf()

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

    private fun getStore(): IStore? {
        return stores.find { it.name == store }
    }

    private fun searchId(query: String, categories: List<Category>, sort: Sort, page: Int, limit: Int): String {
        return "${query}|${categories.joinToString(".")}|${sort}|${page}|${limit}"
    }

}