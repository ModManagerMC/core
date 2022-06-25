package net.modmanagermc.core.store

import net.modmanagermc.core.model.Category
import net.modmanagermc.core.model.Mod

interface IStoreService {

    var store: String
    val categories: List<Category>

    fun search(
        query: String,
        categories: List<Category> = emptyList(),
        sort: Sort,
        page: Int,
        limit: Int
    ): List<Mod>

    fun getMod(modId: Mod): Mod?

}
