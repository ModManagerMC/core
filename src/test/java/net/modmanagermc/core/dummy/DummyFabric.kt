package net.modmanagermc.core.dummy

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.MappingResolver
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.api.ObjectShare
import net.fabricmc.loader.api.entrypoint.EntrypointContainer
import java.io.File
import java.nio.file.Path
import java.util.*

class DummyFabric : FabricLoader {

    private val baseDir = File(System.getProperty("java.io.tmpdir"), "mm-core-tests")

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

    override fun getModContainer(id: String?): Optional<ModContainer> {
        return Optional.empty()
    }

    override fun getAllMods(): MutableCollection<ModContainer> {
        return mutableListOf()
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

    override fun getGameInstance(): Any {
        return ""
    }

    override fun getGameDir(): Path {
        return baseDir.toPath()
    }

    override fun getGameDirectory(): File {
        return baseDir
    }

    override fun getConfigDir(): Path {
        return configDirectory.toPath()
    }

    override fun getConfigDirectory(): File {
        return baseDir.resolve("config")
    }

    override fun getLaunchArguments(sanitize: Boolean): Array<String> {
        return emptyArray()
    }
}