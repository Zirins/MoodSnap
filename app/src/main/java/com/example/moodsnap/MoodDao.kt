package com.example.moodsnap

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MoodDao {
    @Insert
    suspend fun insertMood(moodEntry: MoodEntry)

    @Query("SELECT * FROM moods ORDER BY timestamp DESC")
    suspend fun getAllMoods(): List<MoodEntry>

    @Delete
    fun deleteMood(moodEntry: MoodEntry)
}
