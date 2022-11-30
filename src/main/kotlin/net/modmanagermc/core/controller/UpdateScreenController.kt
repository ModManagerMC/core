package net.modmanagermc.core.controller

import kotlinx.coroutines.*
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.metadata.ModMetadata
import net.modmanagermc.core.Core
import net.modmanagermc.core.model.Mod
import net.modmanagermc.core.update.IUpdateService
import net.modmanagermc.core.update.Update

class UpdateScreenController(val mod: Mod, private val view: View) {

    lateinit var metadata: ModMetadata
    lateinit var update: Update
    private val updateService: IUpdateService by Core.di
    private val fabricLoader: FabricLoader by Core.di
    private var loading = false

    @OptIn(DelicateCoroutinesApi::class)
    fun init() {
        loading = true
        GlobalScope.launch(Dispatchers.IO) {
            delay(1500L)
            if (loading) {
                view.setLoading(true)
            }
        }
        GlobalScope.launch(Dispatchers.IO) {
            try {
                update = updateService.getUpdate(mod) ?: throw Exception("Unknown error")
                view.setDescription(update.version.changelog)
            } catch (e: Exception) {
                view.error(e)
            }
            metadata = fabricLoader.getModContainer(update.modId).get().metadata
            loading = false
            view.setLoading(false)
        }
    }

    interface View {
        fun error(e: Exception)
        fun setLoading(loading: Boolean)
        fun setDescription(text: String)
    }

}
