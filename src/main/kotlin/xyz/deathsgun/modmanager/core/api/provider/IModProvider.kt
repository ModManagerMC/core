/*
 * Copyright 2021-2022 ModManagerMC
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

package xyz.deathsgun.modmanager.core.api.provider

import xyz.deathsgun.modmanager.core.api.Category
import xyz.deathsgun.modmanager.core.api.http.CategoriesResult
import xyz.deathsgun.modmanager.core.api.http.ModResult
import xyz.deathsgun.modmanager.core.api.http.ModsResult

/**
 * Main entrypoint for developers who want to extend
 * the list of mods which ModManager shows.
 */
interface IModProvider {

    /**
     * Name of the provider. This will be shown
     * in the GUI
     *
     * @return a user-friendly name of the mod provider implementation
     */
    fun getName(): String

    /**
     * Returns a list of all possible mod categories also with containing a translatable text
     *
     * @return a list of all mod categories
     */
    fun getCategories(): CategoriesResult

    /**
     * Returns a limited number of [xyz.deathsgun.modmanager.core.api.mod.Mod]'s sorted
     * in a given way.
     *
     * @param sorting the requested sorting of the mods
     * @param page    the requested from the UI starting at 1
     * @param limit   to not overfill the ui and for shorter loading times the amount of returned mods needs to limited
     * @return a list of sorted mods
     */
    fun getMods(sorting: Sorting, page: Int, limit: Int): ModsResult

    /**
     * Returns a limited number of [xyz.deathsgun.modmanager.core.api.mod.Mod]'s from the specified category
     *
     * @param categories the categories of the mods
     * @param sorting    the sorting order
     * @param page       the requested from the UI starting at 1
     * @param limit      to not overfill the ui and for shorter loading times the amount of returned mods needs to limited
     * @return a list of sorted mods
     */
    fun getMods(categories: List<Category>, sorting: Sorting, page: Int, limit: Int): ModsResult

    /**
     * Returns a limited number of [xyz.deathsgun.modmanager.core.api.mod.Mod]'s from a given search.
     *
     * @param query the search string
     * @param categories the categories in which should be searched
     * @param page  the current requested page starts at 0
     * @param limit the amount of mods to return
     * @return a list of mods matching the search term
     */
    fun search(query: String, categories: List<Category>, sorting: Sorting, page: Int, limit: Int): ModsResult

    /**
     * Returns a more detailed representation of the mod
     *
     * @param id the [xyz.deathsgun.modmanager.core.api.mod.Mod] id which is used to receive
     * @return a more detailed representation of [xyz.deathsgun.modmanager.core.api.mod.Mod]
     */
    fun getMod(id: String): ModResult
}