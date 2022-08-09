package net.modmanagermc.core.controller

import kotlinx.coroutines.*
import net.modmanagermc.core.Core
import net.modmanagermc.core.mod.IModService
import net.modmanagermc.core.mod.State
import net.modmanagermc.core.model.Mod
import net.modmanagermc.core.store.IStoreService
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

class ModInfoController(var mod: Mod, private val view: View) {

    val state: State get() = modService.getModState(mod.id)
    private val modService: IModService by Core.di
    private val storeService: IStoreService by Core.di
    private var loading = false

    @OptIn(DelicateCoroutinesApi::class, ExperimentalTime::class)
    fun init() {
        loading = true
        GlobalScope.launch(Dispatchers.IO) {
            delay(2000L)
            if (loading) {
                view.setLoading(true)
            }
        }
        GlobalScope.launch(Dispatchers.IO) {
            try {
                mod = storeService.getMod(mod) ?: throw Exception("Unknown error")
            } catch (e: Exception) {
                view.error(e)
            }
            loading = false
            view.setLoading(false)
        }
    }

    fun tick() {
    }

    fun doAction() {
        view.updateActionText("modmanager.button.install")
    }

    interface View {
        fun updateActionText(translationId: String)
        fun error(e: Exception)
        fun setLoading(loading: Boolean)
    }

}