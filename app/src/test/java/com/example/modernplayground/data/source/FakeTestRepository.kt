package com.example.modernplayground.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.modernplayground.data.Result.Success
import com.example.modernplayground.data.Result
import com.example.modernplayground.data.Task
import kotlinx.coroutines.runBlocking

class FakeTestRepository : TasksRepository {

    var tasksServiceData: LinkedHashMap<String, Task> = LinkedHashMap()

    private val observableTasks = MutableLiveData<Result<List<Task>>>()

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> =
        Success(tasksServiceData.values.toList())

    override suspend fun refreshTasks() {
        observableTasks.value = getTasks()
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        // For your test double, use runBlocking. runBlocking, which is a closer simulation of
        // what a "real" implementation of the repository would do, and it's preferable for Fakes,
        // so that their behavior more closely matches the real implementation.
        //
        // When you're in test classes, meaning classes with @Test functions , use runBlockingTest
        // to get deterministic behavior.
        runBlocking { refreshTasks() }
        return observableTasks
    }

    override suspend fun refreshTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        TODO("Not yet implemented")
    }

    override suspend fun saveTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun completeTask(task: Task) {
        val completedTask = task.copy(isCompleted = true)
        tasksServiceData[task.id] = completedTask
        refreshTasks()
    }

    override suspend fun completeTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearCompletedTasks() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllTasks() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(taskId: String) {
        TODO("Not yet implemented")
    }

    fun addTasks(vararg tasks: Task) {
        for (task in tasks) {
            tasksServiceData[task.id] = task
        }
        runBlocking { refreshTasks() }
    }

    fun isTaskCompleted(taskId: String): Boolean? {
        return tasksServiceData[taskId]?.isCompleted
    }

}