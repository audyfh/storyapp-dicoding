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
class RegisterViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _registUiState = MutableStateFlow(RegistUiState())
    val registUiState : StateFlow<RegistUiState> = _registUiState.asStateFlow()

    fun register(
        name: String,
        email: String,
        password:String
    ) {
        viewModelScope.launch {
            storyRepository.register(
                name = name,
                email = email,
                password = password
            ).collect{
                _registUiState.value = when(it){
                    is Resource.Error -> {
                        RegistUiState(
                            isLoading = false,
                            isError = true,
                            errorMessage = it.msg
                        )
                    }
                    is Resource.Loading ->{
                        RegistUiState(
                            isLoading = true
                        )
                    }
                    is Resource.Success -> {
                        RegistUiState(
                            isLoading = false,
                            isSuccess = true,
                            loginMessage = it.data
                        )
                    }
                }
            }
        }
    }
}