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