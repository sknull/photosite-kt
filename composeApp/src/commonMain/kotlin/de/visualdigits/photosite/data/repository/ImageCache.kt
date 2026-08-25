package de.visualdigits.photosite.data.repository

import coil3.ImageLoader

expect class ImageCache {

    fun getImageLoader(): ImageLoader

    suspend fun prefetchImages(
        urls: List<String>
    )
}
