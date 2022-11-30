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

package net.modmanagermc.core.update

import net.modmanagermc.core.model.Mod
import net.modmanagermc.core.model.ProcessingStatus
import net.modmanagermc.core.model.Version

/**
 * Processes the installed mods and sends them to
 * the ModManager backend.
 *
 * @since 1.0.0
 * @author DeathsGun
 */
interface IUpdateService {

    val processingStatus: ProcessingStatus
    val updates: List<Update>

    fun checkUpdate()
    fun getUpdate(mod: Mod): Update?

}