package net.modmanagermc.core.test

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.MappingResolver
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.api.ObjectShare
import net.fabricmc.loader.api.entrypoint.EntrypointContainer
import java.io.File
import java.nio.file.Path
import java.util.*

class DummyFabricLoader(private val mods: MutableList<ModContainer>) : FabricLoader {

    init {
        mods.add(DummyMinecraftContainer())
    }

    override fun <T : Any?> getEntrypoints(key: String?, type: Class<T>?): MutableList<T> {
        return mutableListOf()
    }

    override fun <T : Any?> getEntrypointContainers(
        key: String?,
        type: Class<T>?
    ): MutableList<EntrypointContainer<T>> {
        return mutableListOf()
    }

    override fun getObjectShare(): ObjectShare? {
        return null
    }

    override fun getMappingResolver(): MappingResolver? {
        return null
    }

    override fun getModContainer(id: String): Optional<ModContainer> {
        return mods.stream().filter { it.metadata.id == id }.findFirst()
    }

    override fun getAllMods(): MutableCollection<ModContainer> {
        return mods
    }

    override fun isModLoaded(id: String?): Boolean {
        return false
    }

    override fun isDevelopmentEnvironment(): Boolean {
        return true
    }

    override fun getEnvironmentType(): EnvType {
        return EnvType.CLIENT
    }

    override fun getGameInstance(): Any? {
        return null
    }

    override fun getGameDir(): Path {
        TODO("Not yet implemented")
    }

    override fun getGameDirectory(): File {
        TODO("Not yet implemented")
    }

    override fun getConfigDir(): Path {
        TODO("Not yet implemented")
    }

    override fun getConfigDirectory(): File {
        TODO("Not yet implemented")
    }

    override fun getLaunchArguments(sanitize: Boolean): Array<String> {
        return arrayOf()
    }

}
