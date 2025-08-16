package com.example.storyapp.data.network.model

data class LoginResponse(
    val error: Boolean,
    val loginResult: LoginResult,
    val message: String
)