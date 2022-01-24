package xyz.deathsgun.modmanager.core.gui.controller

import net.minecraft.text.TranslatableText
import xyz.deathsgun.modmanager.core.api.Category
import xyz.deathsgun.modmanager.core.api.mod.Mod
import xyz.deathsgun.modmanager.core.api.provider.Sorting
import xyz.deathsgun.modmanager.core.gui.View
import xyz.deathsgun.modmanager.core.icon.IconCache

class OverviewController(private val view: View) {

    var query = ""
    var mod: Mod? = null
    var categories = ArrayList<Category>()
    var page = 0
    var limit = 30
    var sorting: Sorting = Sorting.DOWNLOADS
    var scrollPercentage = 0.0
    var error: TranslatableText? = null

    fun shouldShowUpdateAll(): Boolean {
        return categories.any { it.id == "updatable" }
    }

    fun hasNextPage(): Boolean {
        return false
    }

    fun nextPage() {
        page++
    }

    fun hasPreviousPage(): Boolean {
        return 0 < page
    }

    fun previousPage() {
        page--
        if (page < 0) {
            page = 0
        }
    }

    fun close() {
        IconCache.destroyAll()
    }


}