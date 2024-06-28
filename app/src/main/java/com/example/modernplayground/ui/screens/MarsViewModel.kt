package com.example.modernplayground.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modernplayground.network.MarsApi
import kotlinx.coroutines.launch

/**
 * UI state for the Home screen
 */
sealed interface MarsUiState {
    data class Success(val photos: String) : MarsUiState

    // In the case of Loading and Error states, you don't need to set new data and create
    // new objects; you are just passing the web response.
    data object Error : MarsUiState
    data object Loading : MarsUiState
}

class MarsViewModel : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var marsUiState: MarsUiState by mutableStateOf(MarsUiState.Loading)
        private set

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getMarsPhotos()
    }

    /**
     * Gets Mars photos information from the Mars API Retrofit service and updates the
     * [MarsPhoto] [List] [MutableList].
     *
     * You can use viewModelScope to launch the coroutine and make the web service request in
     * the background. Since the viewModelScope belongs to the ViewModel, the request continues
     * even if the app goes through a configuration change.
     */
    private fun getMarsPhotos() {
        viewModelScope.launch {
            marsUiState = try {
                val listResult = MarsApi.retrofitService.getPhotos()
                MarsUiState.Success(
                    "Success : ${listResult.size} Mars photos retrieved"
                )
            } catch (e: Exception) {
                MarsUiState.Error
            }
        }
    }
}