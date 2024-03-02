package com.example.modernplayground.details

import android.os.Bundle
import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.modernplayground.R
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.MapView
import java.lang.IllegalStateException

/**
 * Remembers a MapView and gives it the lifecycle of the current LifecycleOwner
 */
@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    // COMPLETED: DisposableEffect step. Make MapView follow the lifecycle
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map
            onCreate(Bundle())
        }
    }

    // Now, you need to add this observer to the current lifecycle, which you can get using the
    // current LifecycleOwner with the LocalLifecycleOwner composition local. However, it's not
    // enough to add the observer; you also need to be able to remove it! You need a side effect
    // that tells you when the effect is leaving the Composition so that you can perform some
    // cleanup code. The side-effect API you're looking for is DisposableEffect.
    //
    // DisposableEffect is meant for side effects that need to be cleaned up after the keys
    // change or the composable leaves the Composition.
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    // With the keys in DisposableEffect, if either the lifecycle or the mapView change, the
    // observer will be removed and added again to the right lifecycle
    DisposableEffect(key1 = lifecycle, key2 = mapView) {
        // Make MapView follow the current lifecycle
        val lifecycleObserver = getMapLifecycleObserver(mapView)
        lifecycle.addObserver(lifecycleObserver)
        onDispose { lifecycle.removeObserver(lifecycleObserver) }
    }

    /// Check Maps for Compose: https://developers.google.com/maps/documentation/android-sdk/maps-compose

    return mapView
}

// As the MapView is a View and not a composable, you want it to follow the lifecycle of the
// Activity where it's used as well as the lifecycle of the Composition. That means you need
// to create a LifecycleEventObserver to listen for lifecycle events and call the right methods
// on the MapView. Then, you need to add this observer to the current activity's lifecycle.
private fun getMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
            Lifecycle.Event.ON_START -> mapView.onStart()
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
            Lifecycle.Event.ON_STOP -> mapView.onStop()
            Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
            else -> throw IllegalStateException()
        }
    }

fun GoogleMap.setZoom(
    @FloatRange(from = MinZoom.toDouble(), to = MaxZoom.toDouble()) zoom: Float
) {
    resetMinMaxZoomPreference()
    setMinZoomPreference(zoom)
    setMaxZoomPreference(zoom)
}