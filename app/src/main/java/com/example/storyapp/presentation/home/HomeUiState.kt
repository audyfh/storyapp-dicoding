package com.example.storyapp.presentation.home

import com.example.storyapp.data.network.model.Story

data class HomeUiState(
    val isLoading : Boolean = false,
    val listStory : List<Story>? = emptyList(),
    val errorMessage: String? = null,
    val isSuccess : Boolean = false,
    val isError : Boolean = false
)
