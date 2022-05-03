package net.modmanagermc.core.mod

import net.fabricmc.loader.api.FabricLoader
import net.modmanagermc.core.di.DI
import net.modmanagermc.core.model.JarFileInfo
import net.modmanagermc.core.test.DummyFabricLoader
import org.junit.Test
import kotlin.test.assertEquals

internal class ModServiceTest {

    private val di = DI {
        bind<FabricLoader> { DummyFabricLoader(mutableListOf()) }
        bind<IModService> { ModService(this) }
    }
    private val modService: IModService by di

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

    @Test
    fun dependencyTest() {
        val info = JarFileInfo(
            "modmanager",
            "1.2.2+1.18",
            mapOf("modrinth" to "6kq7BzRK"),
            mapOf(
                "SHA-1" to "ecf4da053cc570500eaf17e478e331277397f7d0",
                "SHA-512" to "c5b1d99cc39c9fb7f2f51c4f9544a2e1e7c6b8be671d7d63e2b06bef0c53587289bad8e2c11b2f865c960ddf3929bdad4c682d808455ce38e33b26ccf05e696a"
            )
        )
        val versions = modService.getNewerVersions(info)
        assert(versions.isNotEmpty())
        for (version in versions) {
            assertEquals("6kq7BzRK", version.providerModId)
        }
        val latest = versions[0]
        assert(latest.dependencies.isNotEmpty())
        assert(latest.dependencies.size == 2)
        for (dep in latest.dependencies) {
            assert(dep.required)
            assert(dep.filename.contains("modmenu") || dep.filename.contains("fabric"))
        }
    }
}