package com.example.storyapp.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.network.repository.StoryRepository
import com.example.storyapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _addUiState = MutableStateFlow(AddUiState())
    val addUiState = _addUiState.asStateFlow()

    fun addStory(
        file: File,
        desc: String,
        lat: Float?,
        lon: Float?
    ) {
        viewModelScope.launch {
            storyRepository.uploadStory(
                imageFile = file,
                description = desc,
                lat = lat,
                lon = lon
            ).collect {
                _addUiState.value = when (it) {
                    is Resource.Loading -> {
                        AddUiState(
                            isLoading = true
                        )
                    }

                    is Resource.Error -> {
                        AddUiState(
                            isError = true,
                            errorMessage = "Failed to add story"
                        )
                    }

                    is Resource.Success -> {
                        AddUiState(
                            isSuccess = true,
                            successMessage = "Success add story"
                        )
                    }
                }
            }
        }
    }
}