package com.example.modernplayground.data

import com.example.modernplayground.data.source.local.LocalTask
import com.example.modernplayground.data.source.network.NetworkTask
import com.example.modernplayground.data.source.network.NetworkTask.TaskStatus

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


// External to local
fun Task.toLocal() = LocalTask(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
)

fun List<Task>.toLocal() = map(Task::toLocal)

// Local to External
fun LocalTask.toExternal() = Task(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
)

// Note: JvmName is used to provide a unique name for each extension function with the same name.
// Without this, type erasure will cause compiler errors because these methods will have the same
// signature on the JVM.
@JvmName("localToExternal")
fun List<LocalTask>.toExternal() = map(LocalTask::toExternal)

// Network to Local
fun NetworkTask.toLocal() = LocalTask(
    id = id,
    title = title,
    description = shortDescription,
    isCompleted = (status == TaskStatus.COMPLETE),
)

@JvmName("networkToLocal")
fun List<NetworkTask>.toLocal() = map(NetworkTask::toLocal)

// Local to Network
fun LocalTask.toNetwork() = NetworkTask(
    id = id,
    title = title,
    shortDescription = description,
    status = if (isCompleted) {
        TaskStatus.COMPLETE
    } else {
        TaskStatus.ACTIVE
    }
)

fun List<LocalTask>.toNetwork() = map(LocalTask::toNetwork)

// External to Network
fun Task.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetwork")
fun List<Task>.toNetwork() = map(Task::toNetwork)

// Network to External
fun NetworkTask.toExternal() = toLocal().toExternal()

@JvmName("networkToExternal")
fun List<NetworkTask>.toExternal() = map(NetworkTask::toExternal)