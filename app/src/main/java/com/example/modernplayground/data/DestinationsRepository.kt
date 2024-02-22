package com.example.modernplayground.data

import javax.inject.Inject

class DestinationsRepository @Inject constructor(
    private val destinationsLocalDataSource: DestinationsLocalDataSource
) {
    val destinations: List<ExploreModel> = destinationsLocalDataSource.craneDestinations
    val hotels: List<ExploreModel> = destinationsLocalDataSource.craneHotels
    val restaurants: List<ExploreModel> = destinationsLocalDataSource.craneRestaurants

    fun getDestination(cityName: String): ExploreModel? {
        return destinationsLocalDataSource.craneDestinations.firstOrNull {
            it.city.name == cityName
        }
    }
}