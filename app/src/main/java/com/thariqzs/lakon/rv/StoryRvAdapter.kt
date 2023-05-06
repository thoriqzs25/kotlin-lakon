package com.thariqzs.lakon.rv

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thariqzs.lakon.api.ListStoryItem
import com.thariqzs.lakon.databinding.StoryCardBinding

class StoryRvAdapter(private val stories: List<ListStoryItem>, private val onPressCard: (ListStoryItem, View) -> Unit) :
    RecyclerView.Adapter<StoryRvAdapter.ViewHolder>() {
    inner class ViewHolder(var binding: StoryCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            itemView.setOnClickListener {
                onPressCard(story, binding.ivStoryImage)
            }

            binding.tvStoryName.text = story.name
            binding.tvStoryDesc.text = story.description
            binding.tvStoryDate.text = story.createdAt

            Glide.with(binding.root)
                .load(story.photoUrl)
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