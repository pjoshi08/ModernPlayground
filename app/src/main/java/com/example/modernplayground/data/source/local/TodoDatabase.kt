package com.example.modernplayground.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The room database that contains the Task table.
 *
 * Note that exportSchema should be true in production databases.
 */
@Database(entities = [LocalTask::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
}