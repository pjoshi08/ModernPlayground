package com.example.modernplayground.data.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM task")
    fun observeAll(): Flow<List<LocalTask>>

    @Query("SELECT * FROM task WHERE id = :taskId")
    fun observeById(taskId: String): Flow<LocalTask>

    @Query("SELECT * FROM task")
    suspend fun getAll(): List<LocalTask>

    @Query("SELECT * FROM task WHERE id = :taskId")
    suspend fun getById(taskId: String): LocalTask?

    @Upsert
    suspend fun upsert(task: LocalTask)

    @Upsert
    suspend fun upsertAll(tasks: List<LocalTask>)

    @Query("UPDATE task SET isCompleted = :completed WHERE id = :taskId")
    suspend fun updateCompleted(taskId: String, completed: Boolean)

    @Query("DELETE FROM task WHERE id = :taskId")
    suspend fun deleteById(taskId: String): Int

    @Query("DELETE FROM task")
    suspend fun deleteAll()

    @Query("DELETE FROM task WHERE isCompleted = 1")
    suspend fun deleteCompleted(): Int
}