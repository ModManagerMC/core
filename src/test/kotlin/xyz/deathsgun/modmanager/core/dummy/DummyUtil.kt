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

package xyz.deathsgun.modmanager.core.dummy

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import xyz.deathsgun.modmanager.core.api.mod.Mod

@OptIn(ExperimentalSerializationApi::class)
object DummyUtil {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun getDummyMod(): Mod? {
        val dummyJson = DummyUtil::class.java.getResourceAsStream("/dummy_mod.json") ?: return null
        return json.decodeFromStream<DummyMod>(dummyJson).toMod()
    }


}