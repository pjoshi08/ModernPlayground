package com.example.modernplayground.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modernplayground.data.DestinationsRepository
import com.example.modernplayground.data.ExploreModel
import com.example.modernplayground.di.DefaultDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

const val MAX_PEOPLE = 4

@HiltViewModel
class MainViewModel @Inject constructor(
    private val destinationsRepository: DestinationsRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _suggestedDestinations = MutableStateFlow<List<ExploreModel>>(emptyList())

    /// The extension function asStateFlow converts the flow from mutable to immutable.
    /// UI State: https://developer.android.com/topic/architecture/ui-layer/stateholders#elements-ui
    /// UI State Production: https://developer.android.com/topic/architecture/ui-layer/state-production#output-types
    /// Output types in state production pipelines: https://developer.android.com/topic/architecture/ui-layer/state-production#output-types
    val suggestedDestinations: StateFlow<List<ExploreModel>> = _suggestedDestinations.asStateFlow()

    val hotels: List<ExploreModel> = destinationsRepository.hotels
    val restaurants: List<ExploreModel> = destinationsRepository.restaurants

    init {
        _suggestedDestinations.value = destinationsRepository.destinations
    }

    fun updatePeople(people: Int) {
        viewModelScope.launch {
            if (people > MAX_PEOPLE) {
                // COMPLETED: reset suggestedDestinations
                _suggestedDestinations.value = emptyList()
            } else {
                val newDestinations = withContext(defaultDispatcher) {
                    destinationsRepository.destinations
                        .shuffled(Random(people * (1..100).shuffled().first()))
                }
                // COMPLETED: update suggestedDestinations
                _suggestedDestinations.value = newDestinations
            }
        }
    }

    fun toDestinationChanged(newDestination: String) {
        viewModelScope.launch {
            val newDestinations = withContext(defaultDispatcher) {
                destinationsRepository.destinations
                    .filter { it.city.nameToDisplay.contains(newDestination) }
            }
            // COMPLETED: update suggestedDestinations
            _suggestedDestinations.value = newDestinations
        }
    }
}