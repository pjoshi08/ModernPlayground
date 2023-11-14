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