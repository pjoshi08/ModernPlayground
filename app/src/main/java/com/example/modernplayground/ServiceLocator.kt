package com.example.modernplayground

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.example.modernplayground.data.source.DefaultTasksRepository
import com.example.modernplayground.data.source.TasksDataSource
import com.example.modernplayground.data.source.TasksRepository
import com.example.modernplayground.data.source.local.TasksLocalDataSource
import com.example.modernplayground.data.source.local.ToDoDatabase
import com.example.modernplayground.data.source.remote.TasksRemoteDataSource
import kotlinx.coroutines.runBlocking

object ServiceLocator {

    private var database: ToDoDatabase? = null
    @Volatile
    var tasksRepository: TasksRepository? = null
        @VisibleForTesting set
        // This annotation is a way to express that the reason the setter is public is because of testing.

    // Whether you run your test alone or in a group of tests, your tests should run exactly the same.
    // What this means is that your tests should have no behavior that is dependent on one another
    // (which means avoiding sharing objects between tests).
    //
    // Since the ServiceLocator is a singleton, it has the possibility of being accidentally shared
    // between tests. To help avoid this, create a method that properly resets the ServiceLocator
    // state between tests.
    private val lock = Any()

    fun provideTasksRepository(context: Context): TasksRepository {
        synchronized(this) {
            return tasksRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): TasksRepository {
        val newRepo = DefaultTasksRepository(TasksRemoteDataSource, createTaskLocalDataSource(context))
        return newRepo
    }

    private fun createTaskLocalDataSource(context: Context): TasksDataSource {
        val database = database ?: createDataBase(context)
        return TasksLocalDataSource(database.taskDao())
    }

    private fun createDataBase(context: Context): ToDoDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            ToDoDatabase::class.java, "Tasks.db"
        ).build()
        database = result
        return result
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking { TasksRemoteDataSource.deleteAllTasks() }
        }
        // Clear al data to avoid test pollution
        database?.apply {
            clearAllTables()
            close()
        }
        database = null
        tasksRepository = null
    }
}