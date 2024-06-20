package com.example.modernplayground.data.source

import com.example.modernplayground.MainCoroutineRule
import com.example.modernplayground.data.Result.Success
import com.example.modernplayground.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private val task1 = Task("Title1", "Description1")
private val task2 = Task("Title2", "Description2")
private val task3 = Task("Title3", "Description3")
private val remoteTasks = listOf(task1, task2).sortedBy { it.id }
private val localTasks = listOf(task3).sortedBy { it.id }
private val newTasks = listOf(task3).sortedBy { it.id }

@ExperimentalCoroutinesApi
class DefaultTasksRepositoryTest {
    // Set the main coroutines dispatcher for unit testing
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

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
            // COMPLETED: Dispatchers.Unconfined should be replaced with Dispatchers.Main
            // this requires understading more about coroutines + testing
            // so we will keep this as Unconfined for now.
            // Use Dispatcher.Main, instead of Dispatcher.Unconfined when defining your repository
            // under test. Similar to TestCoroutineDispatcher, Dispatchers.Unconfined executes
            // tasks immediately. But, it doesn't include all of the other testing benefits of
            // TestCoroutineDispatcher, such as being able to pause execution
            tasksRemoteDataSource, tasksLocalDataSource, Dispatchers.Main
        )
    }

    // MainCoroutineRule swaps the Dispatcher.Main for a TestCoroutineDispatcher.
    //
    // Generally, only create one TestCoroutineDispatcher to run a test. Whenever you call
    // runBlockingTest, it will create a new TestCoroutineDispatcher if you don't specify one.
    // MainCoroutineRule includes a TestCoroutineDispatcher. So, to ensure that you don't
    // accidentally create multiple instances of TestCoroutineDispatcher, use the
    // mainCoroutineRule.runBlockingTest instead of just runBlockingTest.
    @Test
    fun getTasks_requestAllTasksFromRemoteDataSource() = mainCoroutineRule.runBlockingTest {  // runBlockingTest deprecated
        // When tasks are requested from the tasks repository
        val tasks = tasksRepository.getTasks(true) as Success

        // Then tasks are loaded from the remote data source
        assertThat(tasks.data, IsEqual(remoteTasks))
    }
}