package com.example.modernplayground

import android.app.Application
import com.example.modernplayground.data.source.TasksRepository
import timber.log.Timber

class TodoApplication : Application() {

    val tasksRepository: TasksRepository
        get() = ServiceLocator.provideTasksRepository(this)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}