package com.example.modernplayground.data.source.local

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase

// This class is included so that the codelab start point will compile. It can be safely deleted
// after the 'LocalTask' entity is created during the codelab.
@Entity
data class BlankEntity (
    @PrimaryKey val id: String
)

/**
 * The room database that contains the Task table.
 *
 * Note that exportSchema should be true in production databases.
 */
@Database(entities = [LocalTask::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {}