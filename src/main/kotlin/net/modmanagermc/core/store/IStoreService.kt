package net.modmanagermc.core.store

import net.modmanagermc.core.model.Category
import net.modmanagermc.core.model.Mod
import java.util.StringJoiner

interface IStoreService {

    var store: String
    val categories: List<Category>
    val licenses: Map<String, String>

    fun search(
        query: String,
        categories: List<Category> = emptyList(),
        sort: Sort,
        page: Int,
        limit: Int
    ): List<Mod>

    fun getMod(mod: Mod): Mod?

}
