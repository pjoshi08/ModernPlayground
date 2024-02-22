package com.example.modernplayground.base

import java.lang.Exception

/**
 * A generic class that holds a value.
 * @param <T>
 */
sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}