package com.example.modernplayground.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.modernplayground.base.Result
import com.example.modernplayground.data.DestinationsRepository
import com.example.modernplayground.data.ExploreModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val destinationsRepository: DestinationsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cityName = savedStateHandle.get<String>(KEY_ARG_DETAILS_CITY_NAME)!!

    val cityDetails: Result<ExploreModel>
        get() {
            val destination = destinationsRepository.getDestination(cityName)
            return if (destination != null) {
                Result.Success(destination)
            } else {
                Result.Error(IllegalArgumentException("City doesn't exist"))
            }
        }
}