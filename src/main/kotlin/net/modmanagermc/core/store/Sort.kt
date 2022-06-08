package net.modmanagermc.core.store

enum class Sort {
    RELEVANCE, DOWNLOADS, FOLLOWS, NEWEST, UPDATED;

    fun translation(): String {
        return "modmanager.sorting.%s".format(name.lowercase())
    }

}