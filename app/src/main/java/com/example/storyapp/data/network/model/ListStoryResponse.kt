package com.example.storyapp.data.network.model

data class ListStoryResponse(
    val error: Boolean,
    val listStory: List<Story>,
    val message: String
)