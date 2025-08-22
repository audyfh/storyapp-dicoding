package com.example.storyapp.presentation.map

import com.example.storyapp.data.network.model.Story

data class MapUIState(
    val stories: List<Story> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
