@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.modernplayground

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.example.modernplayground.data.source.local.LocalTask
import com.example.modernplayground.data.source.local.TodoDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * The tests in this codelab all follow the given, when, then structure:
 *
 * Given: An empty database
 * When: A task is inserted and you start observing the the tasks stream
 * Then: The first item in the tasks stream matches the task that was inserted
 */
class TaskDaoTest {
    private lateinit var database: TodoDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            TodoDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Test
    fun insertTaskAndGetTasks() = runTest {
        val task = LocalTask(
            title = "title",
            description = "description",
            id = "id",
            isCompleted = false
        )
        database.taskDao().upsert(task)

        val tasks = database.taskDao().observeAll().first()

        assertEquals(1, tasks.size)
        assertEquals(task, tasks[0])
    }
}