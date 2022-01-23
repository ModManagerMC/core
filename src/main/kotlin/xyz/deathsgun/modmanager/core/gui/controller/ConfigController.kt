/*
 * Copyright 2022 ModManagerMC
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

package xyz.deathsgun.modmanager.core.gui.controller

import xyz.deathsgun.modmanager.core.ModManager
import xyz.deathsgun.modmanager.core.config.Config

class ConfigController {

    var provider: String = ModManager.config.defaultProvider + ""
    var updateChannel: Config.UpdateChannel = Config.UpdateChannel.values()[ModManager.config.updateChannel.ordinal]
    var hidden: ArrayList<String> = ArrayList(ModManager.config.hidden)

    fun nextChannel() {
        var pos = Config.UpdateChannel.values().indexOf(updateChannel).inc()
        if (pos >= Config.UpdateChannel.values().size) {
            pos = 0
        }
        updateChannel = Config.UpdateChannel.values()[pos]
    }

    fun nextProvider() {
        var pos = ModManager.provider.indexOfFirst { it.getName().equals(provider, true) }.inc()
        if (pos >= ModManager.provider.size) {
            pos = 0
        }
        provider = ModManager.provider[pos].getName().lowercase()
    }

    fun saveConfig() {
        ModManager.config = Config.saveConfig(Config(provider, updateChannel, hidden))
    }

}