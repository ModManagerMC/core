package net.modmanagermc.core.mod

import net.fabricmc.loader.api.FabricLoader
import net.modmanagermc.core.di.DI
import net.modmanagermc.core.model.JarFileInfo
import net.modmanagermc.core.test.DummyFabricLoader
import org.junit.Test

internal class ModServiceTest {

    private val di = DI {
        bind<FabricLoader> { DummyFabricLoader(mutableListOf()) }
        bind<IModService> { ModService(this) }
    }
    private val modService: IModService by di;

    @Test
    fun getVersions() {
        val info = JarFileInfo(
            "fabric",
            "0.47.10+1.18.2",
            mapOf("modrinth" to ""),
            mapOf(
                "SHA-1" to "ff78c08167a3e4ea68c63dd17a2236662f98aefd",
                "SHA-256" to "30b506ebb379f8b30c1a78f21417278cca89b89e194f965ca63cbec97434d132",
                "MD5" to "0444045edc8b7f9a3472a54b6fea2744"
            )
        )
        val versions = modService.getNewerVersions(info)
        assert(versions.isNotEmpty())
    }
}