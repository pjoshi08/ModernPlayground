package com.example.modernplayground.data.source.network

import com.example.modernplayground.data.source.local.LocalTask

data class NetworkTask(
    val id: String,
    val title: String,
    val shortDescription: String,
    val priority: Int? = null,
    val status: TaskStatus = TaskStatus.ACTIVE
) {
    enum class TaskStatus {
        ACTIVE,
        COMPLETE
    }
}

fun NetworkTask.toLocal() = LocalTask(
    id = id,
    title = title,
    description = shortDescription,
    isCompleted = (status == NetworkTask.TaskStatus.COMPLETE)
)

fun List<NetworkTask>.toLocal() = map(NetworkTask::toLocal)

fun LocalTask.toNetwork() = NetworkTask(
    id = id,
    title = title,
    shortDescription = description,
    status = if (isCompleted) { NetworkTask.TaskStatus.COMPLETE } else { NetworkTask.TaskStatus.ACTIVE }
)

fun List<LocalTask>.toNetwork() = map(LocalTask::toNetwork)