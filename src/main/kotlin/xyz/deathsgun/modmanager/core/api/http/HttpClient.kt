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

package xyz.deathsgun.modmanager.core.api.http

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import xyz.deathsgun.modmanager.core.ModManager
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.min

object HttpClient {

    val json = Json {
        ignoreUnknownKeys = true
    }

    fun get(url: String): ByteArray {
        return get(URI.create(url))
    }

    fun get(uri: URI): ByteArray {
        val connection = uri.toURL().openConnection() as HttpURLConnection
        connection.readTimeout = 10000
        connection.requestMethod = "GET"
        connection.setRequestProperty("User-Agent", "ModManager Core ${ModManager.getCoreVersion()}")
        connection.connect()
        if (connection.responseCode != 200) {
            connection.disconnect()
            throw InvalidStatusCodeException(connection.responseCode)
        }
        val content = connection.inputStream.readBytes()
        connection.disconnect()
        return content
    }

    fun getInputStream(url: String): InputStream {
        return getInputStream(URI.create(url))
    }

    fun getInputStream(uri: URI): InputStream {
        return ByteArrayInputStream(get(uri))
    }

    fun download(url: String, path: Path, listener: ((Double) -> Unit)? = null) {
        val output = Files.newOutputStream(path)
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.readTimeout = 10000
        connection.requestMethod = "GET"
        connection.connect()
        if (connection.responseCode != 200) {
            connection.disconnect()
            throw InvalidStatusCodeException(connection.responseCode)
        }
        val size = connection.contentLength
        var downloaded = 0
        while (true) {
            val buffer = ByteArray(min(1024, size - downloaded))
            val read = connection.inputStream.read(buffer)
            if (read == -1) {
                break
            }
            output.write(buffer, 0, read)
            downloaded += read
            listener?.invoke((downloaded / size).toDouble())
        }
        connection.disconnect()
        output.flush()
        output.close()
    }

    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> getJson(url: String): T {
        return json.decodeFromStream(getInputStream(url))
    }

    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> getJson(url: URI): T {
        return json.decodeFromStream(getInputStream(url))
    }

    class InvalidStatusCodeException(val statusCode: Int) : Exception("Received invalid status code: $statusCode")
}
