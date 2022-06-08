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
        categories: List<Category> = emptyList(),
        sort: Sort,
        page: Int,
        limit: Int
    ): List<Mod>

    fun getCategories(): List<Category>

}