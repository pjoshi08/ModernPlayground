package com.example.modernplayground.data

import com.example.modernplayground.data.source.local.TaskDao
import com.example.modernplayground.data.source.network.TaskNetworkDataSource
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
import javax.inject.Singleton

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
 *
 * ------------------------------------------------------------
 * Default implementation of [TaskRepository]. Single entry point of managing tasks' data.
 *
 * @param networkDataSource - the network data source
 * @param localDataSource - local data source
 * @param dispatcher - the dispatcher to be used for long running or complex operations, such as ID
 * generation of mapping many models
 * @param scope - the coroutine scope used for deferred jobs where the result isn't important, such
 * as sending data to the network
 */
@Singleton
class DefaultTaskRepository @Inject constructor(
    private val localDataSource: TaskDao,
    private val networkDataSource: TaskNetworkDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope
): TaskRepository {

    override suspend fun createTask(title: String, description: String): String {
        // ID creation might be a complex operation so it's executed using the supplied coroutine
        // dispatcher
        val taskId = withContext(dispatcher) {
            UUID.randomUUID().toString()
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

    override suspend fun updateTask(taskId: String, title: String, description: String) {
        val task = getTask(taskId)?.copy(
            title = title,
            description = description
        ) ?: throw Exception("Task (id $taskId) not found")

        localDataSource.upsert(task.toLocal())
        saveTasksToNetwork()
    }

    override suspend fun getTasks(forceUpdate: Boolean): List<Task> {
        if (forceUpdate)
            refresh()

        return withContext(dispatcher) {
            localDataSource.getAll().toExternal()
        }
    }

    override fun getTasksStream(): Flow<List<Task>> {
        return localDataSource.observeAll().map { tasks ->
            withContext(dispatcher) {
                tasks.toExternal()
            }
        }
    }

    override suspend fun refreshTask(taskId: String) = refresh()

    override fun getTaskStream(taskId: String): Flow<Task?> {
        return localDataSource.observeById(taskId).map { it.toExternal() }
    }

    /**
     * Get a Task with the given ID. Will return null if the task cannot be found.
     *
     * @param taskId - The ID of the task
     * @param forceUpdate - true if the task should be updated from the network data source first.
     */
    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Task? {
        if (forceUpdate)
            refresh()

        return localDataSource.getById(taskId)?.toExternal()
    }

    override suspend fun completeTask(taskId: String) {
        localDataSource.updateCompleted(taskId = taskId, completed = true)
        saveTasksToNetwork()
    }

    override suspend fun activateTask(taskId: String) {
        localDataSource.updateCompleted(taskId = taskId, completed = false)
        saveTasksToNetwork()
    }

    override suspend fun clearCompletedTask() {
        localDataSource.deleteCompleted()
        saveTasksToNetwork()
    }

    override suspend fun deleteAllTasks() {
        localDataSource.deleteAll()
        saveTasksToNetwork()
    }

    override suspend fun deleteTask(taskId: String) {
        localDataSource.deleteById(taskId)
        saveTasksToNetwork()
    }

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

    /**
     * The following methods load tasks from (refresh), and save tasks to, the network.
     *
     * Real apps may want to do a proper sync, rather than the "one-way sync everything" approach
     * below. See https://developer.android.com/topic/architecture/data-layer/offline-first
     * for more efficient and robust synchronisation strategies.
     *
     * Note that the refresh operation is a suspend function (forces callers to wait) and the save
     * operation is not. It returns immediately so callers don't have to wait.
     */

    /**
     * Delete everything in the local data source and replace it with everything from the network
     * data source.
     *
     * withContext is used for the bulk toLocal operation because there are an unknown number
     * of tasks, and each mapping operation could be computationally expensive.
     */
    override suspend fun refresh() {
        withContext(dispatcher) {
            val remoteTasks = networkDataSource.loadTasks()
            localDataSource.deleteAll()
            localDataSource.upsertAll(remoteTasks.toLocal())
        }
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
            try {
                val localTasks = localDataSource.observeAll().first()
                val networkTasks = withContext(dispatcher) {
                    localTasks.toNetwork()
                }
                networkDataSource.saveTasks(networkTasks)
            } catch (e: Exception) {
                // In a real app you'd handle the exception e.g. by exposing a `networkStatus` flow
                // to an app level UI state holder which could then display a Toast message.
            }
        }
    }
}