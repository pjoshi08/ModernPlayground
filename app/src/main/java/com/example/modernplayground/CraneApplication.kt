package com.example.modernplayground

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.modernplayground.util.UnsplashSizingInterceptor
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CraneApplication : Application(), ImageLoaderFactory {

    /**
     * Create a singleton [ImageLoader].
     * This is used by [rememberImagePainter] to load images in the app.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components { add(UnsplashSizingInterceptor) }
            .build()
    }
}