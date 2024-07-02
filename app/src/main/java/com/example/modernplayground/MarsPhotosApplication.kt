package com.example.modernplayground

import android.app.Application
import com.example.modernplayground.data.AppContainer
import com.example.modernplayground.data.DefaultAppContainer

class MarsPhotosApplication : Application() {
    /** AppContainer instance used by the rest of classes to obtain dependencies */
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}