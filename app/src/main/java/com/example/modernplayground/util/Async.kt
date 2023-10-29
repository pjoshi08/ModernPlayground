package com.example.modernplayground.util

sealed class Async<out T> {
    // TODO: data object?
    object Loading : Async<Nothing>()

    data class Error(val errorMessage: Int) : Async<Nothing>()

    data class Success<out T>(val data: T) : Async<T>()
}