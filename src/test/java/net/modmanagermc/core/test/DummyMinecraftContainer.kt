package net.modmanagermc.core.test

import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.api.metadata.ModMetadata
import net.fabricmc.loader.api.metadata.ModOrigin
import net.fabricmc.loader.impl.metadata.ModOriginImpl
import java.nio.file.Path
import java.util.*

class DummyMinecraftContainer : ModContainer {
    override fun getMetadata(): ModMetadata {
        return DummyMetadata("minecraft", "1.18.2")
    }

    override fun getRootPaths(): MutableList<Path> {
        return mutableListOf()
    }

    override fun getOrigin(): ModOrigin {
        return ModOriginImpl(mutableListOf())
    }

    override fun getContainingMod(): Optional<ModContainer> {
        return Optional.empty()
    }

    override fun getContainedMods(): MutableCollection<ModContainer> {
        return mutableListOf()
    }

    override fun getRootPath(): Path? {
        return null
    }

    override fun getPath(file: String?): Path? {
        return null
    }
}