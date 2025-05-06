package com.example.moodsnap

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moodsnap.databinding.ItemMoodBinding
import java.text.SimpleDateFormat
import java.util.*
import android.net.Uri

class MoodAdapter(private val moodList: List<MoodEntry>,
                  private val onDelete: (MoodEntry) -> Unit) :
    RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    class MoodViewHolder(private val binding: ItemMoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(moodEntry: MoodEntry, onDelete: (MoodEntry) -> Unit) {
            binding.textViewMood.text = moodEntry.mood
            binding.textViewNote.text = moodEntry.note
            binding.textViewTimestamp.text =
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
                    .format(Date(moodEntry.timestamp))

            if (!moodEntry.imagePath.isNullOrEmpty()) {
                val uri = Uri.parse(moodEntry.imagePath)
                binding.imageViewSelfie.setImageURI(uri)
                binding.imageViewSelfie.visibility = View.VISIBLE
            } else {
                binding.imageViewSelfie.visibility = View.GONE
            }
            binding.btnDelete.setOnClickListener {
                onDelete(moodEntry)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemMoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(moodList[position], onDelete)
    }

    override fun getItemCount(): Int = moodList.size
}