package com.example.storyapp.presentation.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.network.repository.StoryRepository
import com.example.storyapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            storyRepository.login(email, password)
                .collect {
                    _loginUiState.value = when (it) {
                        is Resource.Loading -> {
                            LoginUiState(isLoading = true)
                        }

                        is Resource.Success -> {
                            LoginUiState(
                                isLoading = false,
                                isSuccess = true,
                                loginResult = it.data
                            )
                        }

                        is Resource.Error -> {
                            LoginUiState(
                                isLoading = false,
                                isError = true,
                                errorMessage = it.msg
                            )
                        }
                    }
                }
        }
    }
}