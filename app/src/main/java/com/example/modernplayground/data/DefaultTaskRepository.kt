package com.example.modernplayground.data

import com.example.modernplayground.data.source.local.TaskDao
import com.example.modernplayground.data.source.local.toExternal
import com.example.modernplayground.data.source.local.toLocal
import com.example.modernplayground.data.source.network.TaskNetworkDataSource
import com.example.modernplayground.data.source.network.toLocal
import com.example.modernplayground.data.source.network.toNetwork
import com.example.modernplayground.di.ApplicationScope
import com.example.modernplayground.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

/**
 *  If you know that the task ID creation is complex, why don't you just hardcode it to be
 *  executed using Dispatchers.Default?
 *
 * By specifying it as a parameter to the repository, a different dispatcher can be injected
 * in tests, where it is often desirable to have all instructions executed on the same thread
 * for deterministic behavior.
 *
 * This is a very basic data synchronization strategy, which is not suitable for a production app.
 * For more robust and efficient synchronization strategies, check out the [offline-first guidance]
 * {https://developer.android.com/topic/architecture/data-layer/offline-first}.
 */
class DefaultTaskRepository @Inject constructor(
    private val localDataSource: TaskDao,
    private val networkDataSource: TaskNetworkDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope
) {

    fun observeAll(): Flow<List<Task>> {
        return localDataSource.observeAll()
            .map { tasks -> tasks.toExternal() }
    }

    /**
     *  Why don't you use withContext to wrap insertTask or toLocalModel, like you did before?
     *
     * Firstly, insertTask is provided by the Room library, which takes responsibility for
     * ensuring a non-UI thread is used.
     *
     * Secondly, toLocalModel is an in-memory copy of a single, small object. If it were CPU
     * or I/O bound, or an operation on a collection of objects, then withContext should be used.
     */
    suspend fun create(title: String, description: String): String {
        val taskId = withContext(dispatcher) {
            createTaskId()
        }

        val task = Task(
            title = title,
            description = description,
            id = taskId
        )

        localDataSource.upsert(task.toLocal())
        saveTasksToNetwork()
        return taskId
    }

    suspend fun complete(taskId: String) {
        localDataSource.updateCompleted(taskId, true)
        saveTasksToNetwork()
    }

    // This method might be computationally expensive
    private fun createTaskId(): String {
        return UUID.randomUUID().toString()
    }

    suspend fun refresh() {
        val networkTasks = networkDataSource.loadTasks()
        localDataSource.deleteAll()
        // withContext is used for the bulk toLocal operation because there are an unknown number
        // of tasks, and each mapping operation could be computationally expensive.
        val localTasks = withContext(dispatcher) {
            networkTasks.toLocal()
        }
        localDataSource.upsertAll(networkTasks.toLocal())
        //localDataSource.upsertAll(localTasks)
    }

    /**
     * If you were to run this code, you'd notice that saveTasksToNetwork blocks.
     * This means that callers of create and complete are forced to wait until the data is
     * saved to the network before they can be sure that the operation has completed. In the
     * simulated network data source this is only two seconds, but in a real app it might be
     * much longer—or never if there's no network connection.
     *
     * This is unnecessarily restrictive and will likely cause a poor user experience—no one
     * wants to wait to create a task, especially when they're busy!
     *
     * A better solution is to use a different coroutine scope in order to save the data to the
     * network. This allows the operation to complete in the background without making the caller
     * wait for the result.
     *
     * Note: An even better solution is to have a separate network synchronization object that
     * is [scheduled using WorkManager]
     * {https://developer.android.com/topic/libraries/architecture/workmanager}.
     */
    private suspend fun saveTasksToNetwork() {
        scope.launch {
            val localTasks = localDataSource.observeAll().first()
            val networkTasks = withContext(dispatcher) {
                localTasks.toNetwork()
            }
            networkDataSource.saveTasks(networkTasks)
        }
    }
}