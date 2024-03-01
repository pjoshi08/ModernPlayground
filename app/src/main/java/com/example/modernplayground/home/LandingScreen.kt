package com.example.modernplayground.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.modernplayground.R
import kotlinx.coroutines.delay

private const val SplashWaitTime: Long = 2000

/// Lifecycle of Composable: https://developer.android.com/jetpack/compose/lifecycle
@Composable
fun LandingScreen(onTimeout: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // COMPLETED: LaunchedEffect and rememberUpdatedState step
        /// You should use rememberUpdatedState when a long-lived lambda or object expression
        /// references parameters or values computed during composition, which might be common
        /// when working with LaunchedEffect.

        // This will always refer to the latest onTimeout function that
        // LandingScreen was recomposed with
        val currentOnTimeout by rememberUpdatedState(onTimeout)

        /// A side-effect in Compose is a change to the state of the app that happens outside the
        /// scope of a composable function. For example, opening a new screen when the user taps
        /// on a button, or showing a message when the app doesn't have Internet connection.

        /// Changing the state to show/hide the landing screen will happen in the onTimeout
        /// callback and since before calling onTimeout you need to load things using coroutines,
        /// the state change needs to happen in the context of a coroutine!

        /// LaunchedEffect triggers a coroutine-scoped side-effect in Compose.
        /// The coroutine will be canceled if LaunchedEffect leaves the composition.
        /// LaunchedEffect takes a variable number of keys as a parameter that are used to restart
        /// the effect whenever one of those keys changes. ***

        /// To trigger the side-effect only once during the lifecycle of this composable,
        /// use a constant as a key, for example LaunchedEffect(Unit) { ... }. However,
        /// now there's a different issue.
        /// If onTimeout changes while the side-effect is in progress, there's no guarantee that
        /// the last onTimeout is called when the effect finishes. To guarantee that the last
        /// onTimeout is called, remember onTimeout using the rememberUpdatedState API.

        // Create an effect that matches the lifecycle of LandingScreen.
        // If LandingScreen recomposes or onTimeout changes,
        // the delay shouldn't start again.
        LaunchedEffect(Unit) {
            delay(SplashWaitTime)
            currentOnTimeout()
        }
        // TODO: Make LandingScreen disappear after loading data
        Image(painterResource(id = R.drawable.ic_crane_drawer), contentDescription = null)
    }
}