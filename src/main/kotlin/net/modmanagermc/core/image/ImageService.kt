package net.modmanagermc.core.image

import kotlinx.coroutines.*
import net.fabricmc.loader.api.FabricLoader
import net.modmanagermc.core.di.DI
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.apache.logging.log4j.LogManager
import java.io.InputStream
import java.nio.file.Files
import java.security.MessageDigest

@OptIn(DelicateCoroutinesApi::class)
internal class ImageService(di: DI) : IImageService {

    private val logger = LogManager.getLogger("ModManager|ImageService")
    private val fabricLoader: FabricLoader by di
    private val imagePath = fabricLoader.configDir.resolve("modmanager").resolve("icons")
    private val states: MutableMap<String, ImageState> = mutableMapOf()
    private val client = HttpClients.createDefault()

    init {
        Files.createDirectories(imagePath)
    }

    override fun downloadImage(image: String) {
        logger.debug("Downloading {}", image)
        states[image] = ImageState.DOWNLOADING
        if (Files.exists(imagePath.resolve(id(image)))) {
            logger.debug("Already downloaded {}", image)
            states[image] = ImageState.DOWNLOADED
            return
        }
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val request = HttpGet(image)
                val response = client.execute(request)
                Files.copy(response.entity.content, imagePath.resolve(id(image)))
                EntityUtils.consume(response.entity)
            } catch (e: Exception) {
                states[image] = ImageState.ERRORED

                logger.error("Error while dowloading image {}:\n{}", image, e)
                return@launch
            }
            logger.debug("Downloaded {}", image)
            states[image] = ImageState.DOWNLOADED
        }
    }

    override fun openImage(image: String): InputStream {
        return Files.newInputStream(imagePath.resolve(id(image)))
    }

    override fun getImageState(image: String): ImageState {
        return states.getOrDefault(image, ImageState.DOWNLOADED)
    }

    override fun setImageState(image: String, state: ImageState) {
        states[image] = state
    }

    private fun id(image: String): String {
        return MessageDigest.getInstance("SHA-1").digest(image.encodeToByteArray())
            .joinToString("") {
                "%02x".format(it)
            }
    }

}