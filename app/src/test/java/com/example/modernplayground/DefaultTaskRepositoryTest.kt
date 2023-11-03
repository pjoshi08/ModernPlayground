package com.example.modernplayground

import com.example.modernplayground.data.DefaultTaskRepository
import com.example.modernplayground.data.source.local.LocalTask
import com.example.modernplayground.data.source.local.toExternal
import com.example.modernplayground.data.source.network.NetworkTask
import com.example.modernplayground.data.source.network.TaskNetworkDataSource
import com.example.modernplayground.data.source.network.toLocal
import com.example.modernplayground.source.local.FakeTaskDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultTaskRepositoryTest {

    private var testDispatcher = UnconfinedTestDispatcher()
    private var testScope = TestScope(testDispatcher)

    private val localTasks = listOf(
        LocalTask(id = "1", title = "title1", description = "description1", isCompleted = false),
        LocalTask(id = "2", title = "title2", description = "description2", isCompleted = true),
    )

    /**
     * In a real app, you'd also create a fake dependency to replace TaskNetworkDataSource
     * (by having the fake and the real objects implement a common interface), but for the
     * purposes of this codelab you use it directly.
     */
    private val localDataSource = FakeTaskDao(localTasks)
    private val networkDataSource = TaskNetworkDataSource()
    private val taskRepository = DefaultTaskRepository(
        localDataSource = localDataSource,
        networkDataSource = networkDataSource,
        dispatcher = testDispatcher,
        scope = testScope
    )

    // There are three main areas you should test: reads, writes, and data sync.
    @Test
    fun observeAll_exposesLocalData() = runTest {
        val tasks = taskRepository.observeAll().first()
        assertEquals(localTasks.toExternal(), tasks)
    }

    @Test
    fun onTaskCreation_localAndNetworkAreUpdated() = testScope.runTest {
        val newTaskId = taskRepository.create(
            localTasks[0].title,
            localTasks[0].description
        )
        val localTasks = localDataSource.observeAll().first()
        assertEquals(true, localTasks.map { it.id }.contains(newTaskId))

        val networkTasks = networkDataSource.loadTasks()
        assertEquals(true, networkTasks.map { it.id }.contains(newTaskId))
    }

    @Test
    fun onTaskCompletion_localAndNetworkAreUpdated() = testScope.runTest {
        taskRepository.complete("1")

        val localTasks = localDataSource.observeAll().first()
        val isLocalTaskComplete = localTasks.firstOrNull { it.id == "1" }?.isCompleted
        assertEquals(true, isLocalTaskComplete)

        val networkTasks = networkDataSource.loadTasks()
        val isNetworkTaskComplete =  networkTasks
            .firstOrNull { it.id == "1" }?.status == NetworkTask.TaskStatus.COMPLETE
        assertEquals(true, isNetworkTaskComplete)
    }

    @Test
    fun onRefresh_localIsEqualToNetwork() = runTest {
        val networkTasks = listOf(
            NetworkTask(id = "3", title = "title3", shortDescription = "desc3"),
            NetworkTask(id = "4", title = "title4", shortDescription = "desc4"),
        )
        networkDataSource.saveTasks(networkTasks)

        taskRepository.refresh()

        assertEquals(networkTasks.toLocal(), localDataSource.observeAll().first())
    }
}