package com.example.moodsnap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moodsnap.databinding.ItemMoodBinding

class MoodAdapter(private val moodList: List<MoodEntry>) :
    RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    class MoodViewHolder(private val binding: ItemMoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(moodEntry: MoodEntry) {
            binding.textViewMood.text = moodEntry.mood
            binding.textViewNote.text = moodEntry.note
            binding.textViewTimestamp.text =
                java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US)
                    .format(java.util.Date(moodEntry.timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemMoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(moodList[position])
    }

    override fun getItemCount(): Int = moodList.size
}
