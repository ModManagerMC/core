package net.modmanagermc.core.controller

import net.modmanagermc.core.Core
import net.modmanagermc.core.mod.IModService
import net.modmanagermc.core.mod.State
import net.modmanagermc.core.model.Mod
import net.modmanagermc.core.store.IStoreService

class ModInfoController(var mod: Mod, private val view: View) {

    private val modService: IModService by Core.di
    private val storeService: IStoreService by Core.di
    val state: State get() = modService.getModState(mod.id)

    fun init() {
        try {
            mod = storeService.getMod(mod) ?: throw Exception("Unknown error")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun tick() {
    }

    fun doAction() {
        view.updateActionText("modmanager.button.install")
    }

    interface View {
        fun updateActionText(translationId: String)
    }

}