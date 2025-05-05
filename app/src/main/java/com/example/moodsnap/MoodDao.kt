package com.example.moodsnap

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MoodDao {
    @Insert
    suspend fun insertMood(moodEntry: MoodEntry)

    @Query("SELECT * FROM MoodEntry ORDER BY timestamp DESC")
    suspend fun getAllMoods(): List<MoodEntry>
}
