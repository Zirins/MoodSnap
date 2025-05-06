package com.example.moodsnap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoodHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db: MoodDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_history)

        recyclerView = findViewById(R.id.main)
        recyclerView.layoutManager = LinearLayoutManager(this)
        db = MoodDatabase.getDatabase(this)

        loadMoods()
    }

    private fun loadMoods() {
        CoroutineScope(Dispatchers.IO).launch {
            val moods = db.moodDao().getAllMoods()
            withContext(Dispatchers.Main) {
                recyclerView.adapter = MoodAdapter(moods, this@MoodHistoryActivity::deleteMood)
            }
        }
    }

    private fun deleteMood(mood: MoodEntry) {
        CoroutineScope(Dispatchers.IO).launch {
            db.moodDao().deleteMood(mood)
            loadMoods()
        }
    }
}
