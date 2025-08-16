package com.example.storyapp.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.network.repository.StoryRepository
import com.example.storyapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _detailUiState = MutableStateFlow(DetailUiState())
    val detailUiState = _detailUiState.asStateFlow()

    fun getStory(id: String){
        viewModelScope.launch {
            storyRepository.getDetailStory(id).collect{
                _detailUiState.value = when(it){
                    is Resource.Loading -> {
                        DetailUiState(isLoading = true)
                    }
                    is Resource.Error -> {
                        DetailUiState(
                            isLoading = false,
                            isError = true,
                            errorMessage = it.msg
                        )
                    }
                    is Resource.Success -> {
                        DetailUiState(
                            isLoading = false,
                            isSuccess = true,
                            story = it.data
                        )
                    }
                }
            }
        }
    }
}