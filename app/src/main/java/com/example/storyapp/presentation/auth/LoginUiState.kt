package com.example.storyapp.presentation.auth
import com.example.storyapp.data.network.model.LoginResult

data class LoginUiState(
    val isLoading: Boolean = false,
    val loginResult : LoginResult? = null,
    val errorMessage: String? = null,
    val isSuccess : Boolean = false,
    val isError : Boolean = false
)
