package com.example.storyapp.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.databinding.StoryCardBinding

class StoryAdapter :
    PagingDataAdapter<StoryEntity, StoryAdapter.StoryViewHolder>(diffCallback = differCallback) {

    private var onItemClickCallback: ((StoryEntity) -> Unit)? = null
    fun setOnItemClickCallback(callback: (StoryEntity) -> Unit) {
        onItemClickCallback = callback
    }

    inner class StoryViewHolder(private val binding: StoryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryEntity) {
            with(binding) {
                tvTitle.text = story.name
                tvDesc.text = story.description
                ivStory.load(story.photoUrl) {
                    crossfade(true)
                }
                root.setOnClickListener {
                    onItemClickCallback?.invoke(story)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = StoryCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        story?.let { holder.bind(it) }
    }

    companion object {
         val differCallback = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

}