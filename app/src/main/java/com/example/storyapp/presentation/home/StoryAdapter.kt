package com.example.storyapp.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.example.storyapp.data.network.model.Story
import com.example.storyapp.databinding.StoryCardBinding

class StoryAdapter :
    PagingDataAdapter<Story, StoryAdapter.StoryViewHolder>(diffCallback = differCallback) {

    private var onItemClickCallback: ((Story) -> Unit)? = null
    fun setOnItemClickCallback(callback: (Story) -> Unit) {
        onItemClickCallback = callback
    }

    inner class StoryViewHolder(private val binding: StoryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            with(binding) {
                tvTitle.text = story.name
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
        private val differCallback = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }

}