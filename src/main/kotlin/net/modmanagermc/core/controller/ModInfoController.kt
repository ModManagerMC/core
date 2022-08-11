/*
 * Copyright (c) 2022 DeathsGun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.modmanagermc.core.controller

import kotlinx.coroutines.*
import net.modmanagermc.core.Core
import net.modmanagermc.core.mod.IModService
import net.modmanagermc.core.mod.State
import net.modmanagermc.core.model.Mod
import net.modmanagermc.core.store.IStoreService
import kotlin.time.ExperimentalTime

class ModInfoController(var mod: Mod, private val view: View) {

    private val state: State get() = modService.getModState(mod.id)
    private val modService: IModService by Core.di
    private val storeService: IStoreService by Core.di
    private var loading = false

    @OptIn(DelicateCoroutinesApi::class)
    fun init() {
        loading = true
        GlobalScope.launch(Dispatchers.IO) {
            delay(2000L)
            if (loading) {
                view.setLoading(true)
            }
        }
        val actionText = when (state) {
            State.INSTALLED, State.OUTDATED -> "modmanager.button.remove"
            State.DOWNLOADABLE -> "modmanager.button.install"
        }
        view.updateActionText(actionText)
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
    }

    interface View {
        fun updateActionText(translationId: String)
        fun error(e: Exception)
        fun setLoading(loading: Boolean)
    }

}