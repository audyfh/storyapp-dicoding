package com.example.storyapp.data.network.model

data class DetailStoryResponse(
    val error: Boolean,
    val message: String,
    val story: Story
)