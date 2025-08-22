package com.example.storyapp.presentation.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.network.model.Story
import com.example.storyapp.data.network.repository.StoryRepository
import com.example.storyapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MapViewModel @Inject constructor(
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(MapUIState())
    var uiState : LiveData<MapUIState> = _uiState

    init {
        getAllStoriesWithMap()
    }

    private fun getAllStoriesWithMap(){
        viewModelScope.launch {
            _uiState.value = MapUIState(isLoading = true)
            val data = storyRepository.getAllStoryWithMaps()
            if (data is Resource.Success){
                _uiState.value = MapUIState(
                    isLoading = false,
                    stories = data.data!!
                )
            } else {
                _uiState.value = MapUIState(
                    isLoading = false,
                    error = data.msg
                )
            }
        }
    }
}