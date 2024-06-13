package com.example.modernplayground.data.source

import com.example.modernplayground.data.Result.Success
import com.example.modernplayground.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Test

private val task1 = Task("Title1", "Description1")
private val task2 = Task("Title2", "Description2")
private val task3 = Task("Title3", "Description3")
private val remoteTasks = listOf(task1, task2).sortedBy { it.id }
private val localTasks = listOf(task3).sortedBy { it.id }
private val newTasks = listOf(task3).sortedBy { it.id }

class DefaultTasksRepositoryTest {
    private lateinit var tasksRemoteDataSource: FakeDataSource
    private lateinit var tasksLocalDataSource: FakeDataSource

    // Class under test
    private lateinit var tasksRepository: DefaultTasksRepository

    @Before
    fun createRepository() {
        tasksRemoteDataSource = FakeDataSource(remoteTasks.toMutableList())
        tasksLocalDataSource = FakeDataSource(localTasks.toMutableList())
        // Get a reference to the class under test
        tasksRepository = DefaultTasksRepository(
            // TODO: Dispatchers.Unconfined should be replaced with Dispatchers.Main
            // this requires understading more about coroutines + testing
            // so we will keep this as Unconfined for now.
            tasksRemoteDataSource, tasksLocalDataSource, Dispatchers.Unconfined
        )
    }

    @Test
    fun getTasks_requestAllTasksFromRemoteDataSource() = runTest {  // runBlockingTest deprecated
        // When tasks are requested from the tasks repository
        val tasks = tasksRepository.getTasks(true) as Success

        // Then tasks are loaded from the remote data source
        assertThat(tasks.data, IsEqual(remoteTasks))
    }
}