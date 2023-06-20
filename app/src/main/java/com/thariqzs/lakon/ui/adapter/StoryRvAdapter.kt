package com.thariqzs.lakon.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagramclone.getTimeAgo
import com.thariqzs.lakon.R
import com.thariqzs.lakon.data.model.Story
import com.thariqzs.lakon.databinding.StoryCardBinding

class StoryRvAdapter(private val onPressCard: (Story, View) -> Unit) :
    PagingDataAdapter<Story,StoryRvAdapter.ViewHolder>(DIFF_CALLBACK) {
    inner class ViewHolder(var binding: StoryCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        const val TAG = "srathoriq"
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}