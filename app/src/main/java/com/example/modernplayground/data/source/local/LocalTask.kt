package com.example.modernplayground.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.modernplayground.data.Task

@Entity(
    tableName = "task"
)
data class LocalTask(
    @PrimaryKey val id: String,
    var title: String,
    var description: String,
    var isCompleted: Boolean
)

/**
 *  You're just copying identical fields from one data type to another; why couldn't you just
 *  use LocalTask everywhere?
 *
 * The reasons are:
 *
 * 1. Separation of concerns. LocalTask is specifically concerned with how the task is stored in
 * the database and includes extra information (for example, the @Entity Room annotation),
 * which isn't relevant to the other architectural layers.
 * 2. Flexibility. By separating the internal and external models you gain flexibility. You can
 * change the internal storage structure without affecting the other layers. For example, if you
 * want to switch from using Room to DataStore for local storage you could do without breaking
 * the data layer API.
 */
// Convert a LocalTask to a Task
fun LocalTask.toExternal() = Task(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted
)

// Convenience function which converts a list of LocalTasks to a list of Tasks
fun List<LocalTask>.toExternal() = map(LocalTask::toExternal) // Equivalent to map { it.toExternal() }

fun Task.toLocal() = LocalTask(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted
)