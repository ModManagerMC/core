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
@Deprecated("This will be moved to a own repo in a future version")
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