package com.thariqzs.lakon.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramclone.getTimeAgo
import com.thariqzs.lakon.R
import com.thariqzs.lakon.database.StoryItem
import com.thariqzs.lakon.databinding.StoryCardBinding

class StoryRvAdapter(private val stories: List<StoryItem>, private val onPressCard: (StoryItem, View) -> Unit) :
    RecyclerView.Adapter<StoryRvAdapter.ViewHolder>() {
    inner class ViewHolder(var binding: StoryCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryItem) {
            itemView.setOnClickListener {
                onPressCard(story, binding.ivStoryImage)
            }

            binding.tvStoryName.text = story.name
            binding.tvStoryDesc.text = story.description
            binding.tvStoryDate.text = getTimeAgo(story.createdAt)

            Glide.with(binding.root)
                .load(story.photoUrl)
                .placeholder(R.drawable.bg_placeholder_iv)
                .into(binding.ivStoryImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = stories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    companion object {
        val TAG = "sathoriq"
    }
}