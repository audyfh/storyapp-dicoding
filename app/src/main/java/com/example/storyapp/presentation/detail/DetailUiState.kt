package com.example.storyapp.presentation.detail

import com.example.storyapp.data.network.model.Story

data class DetailUiState(
    val isLoading : Boolean = false,
    val story : Story? = null,
    val errorMessage : String? = null,
    val isSuccess : Boolean = false,
    val isError : Boolean = false
)
