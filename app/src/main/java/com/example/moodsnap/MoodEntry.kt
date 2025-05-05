package com.example.moodsnap

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mood: String,
    val note: String,
    val timestamp: Long = System.currentTimeMillis()
)
