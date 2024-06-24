package com.example.modernplayground.util

import androidx.test.espresso.idling.CountingIdlingResource

// Two Idling Resources: One to deal with data binding synchronization for your views, and another
// to deal with the long running operation in your repository.

// Basically, whenever the app starts doing some work, increment the counter. When that work
// finishes, decrement the counter. Therefore, CountingIdlingResource will only have a "count"
// of zero if there is no work being done. This is a singleton so that you can access this
// idling resource anywhere in the app where long-running work might be done.
object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    // When the counter is greater than zero, the app is considered working.
    fun increment() {
        countingIdlingResource.increment()
    }

    // When the counter is zero, the app is considered idle.
    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    // Espresso does not work well with coroutines yet.
    // See: https://github.com/Kotlin/kotlinx.coroutines/issues/982
    EspressoIdlingResource.increment() // Set app as busy
    return try {
        function() // Long running task
    } finally {
        EspressoIdlingResource.decrement() // Set app as idle
    }
}