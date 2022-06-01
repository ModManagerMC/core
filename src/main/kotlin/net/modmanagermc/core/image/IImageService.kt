package net.modmanagermc.core.image

import java.io.InputStream

interface IImageService {

    fun downloadImage(image: String)

    fun openImage(image: String): InputStream

    fun getImageState(image: String): ImageState

    fun setImageState(image: String, state: ImageState)
}