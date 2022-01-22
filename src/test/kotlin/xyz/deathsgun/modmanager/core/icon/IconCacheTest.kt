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

package xyz.deathsgun.modmanager.core.icon

import net.minecraft.util.Identifier
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.time.StopWatch
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import xyz.deathsgun.modmanager.core.dummy.DummyUtil
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile
import java.nio.file.Files
import java.util.concurrent.TimeUnit
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

internal object IconCacheTest {

    private val cacheDirectory = File("test_icons").toPath()

    @BeforeAll
    @JvmStatic
    fun setUp() {
        IconCache.init(cacheDirectory, this::iconLoader)
    }

    @Test
    fun get() {
        val mod = DummyUtil.getDummyMod() ?: fail("Couldn't load dummy mod data!")
        var identifier: Identifier
        val stopWatch = StopWatch()
        while (true) {
            identifier = IconCache.get(mod)
            if (identifier.path.contains(mod.id, ignoreCase = true)) {
                break
            }
            if (identifier.namespace == "minecraft") {
                fail("Error while loading icon check the log for details")
            }
            if (!stopWatch.isStarted) {
                stopWatch.start()
            }
            if (stopWatch.time >= 5000) {
                stopWatch.stop()
                fail("Retrieving the icon took longer than ${stopWatch.getTime(TimeUnit.SECONDS)} seconds!")
            }
        }
        assertEquals("modmanager", identifier.namespace)
        assertContains("mod_icons/${mod.id}", identifier.path, ignoreCase = true)
        assert(Files.exists(cacheDirectory.resolve(mod.id)))
    }

    @Test
    fun clear() {
        val f = RandomAccessFile(cacheDirectory.resolve("bigFile").toFile(), "rw")
        f.setLength((1024 * 1024 * 20).toLong())
        f.close()
        IconCache.clear()
        assertTrue(Files.notExists(cacheDirectory.resolve("bigFile")))
        assertTrue(FileUtils.sizeOfDirectory(cacheDirectory.toFile()) <= 10000000, "Directory caching has not worked!")
    }

    @AfterAll
    @JvmStatic
    fun tearDown() {
        cacheDirectory.toFile().deleteRecursively()
    }

    private fun iconLoader(stream: InputStream) {
        stream.close()
    }
}