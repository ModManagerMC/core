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

package net.modmanagermc.core.di

import kotlin.reflect.KProperty

/**
 * Custom DependencyInjection implementation for minecraft.
 * Because we can't use things like Kodein-DI.
 *
 * @author DeathsGun
 * @since 1.0.0
 */
//TODO: Move to own repo
class DI(init: DI.(DI) -> Unit) {

    val instances = mutableMapOf<String, Any>()

    init {
        init(this)
    }

    inline fun <reified T> bind(init: DI.(DI) -> Any) {
        instances[T::class.qualifiedName!!] = init(this)
    }

    inline operator fun <C : Any, reified T : Any> getValue(updateService: C?, property: KProperty<*>): T {
        return instances[T::class.qualifiedName] as T
    }

}