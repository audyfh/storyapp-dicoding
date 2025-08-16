package com.example.storyapp.presentation.add

data class AddUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val isError: Boolean = false

)
