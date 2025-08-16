package com.example.storyapp.presentation.auth

data class RegistUiState(
    val isLoading : Boolean = false,
    val loginMessage : String? = null,
    val errorMessage : String? = null,
    val isSuccess : Boolean = false,
    val isError : Boolean = false
)