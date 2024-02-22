package com.example.modernplayground.details

import android.os.Bundle
import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.modernplayground.R
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.MapView

/**
 * Remembers a MapView and gives it the lifecycle of the current LifecycleOwner
 */
@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    // TODO: DisposableEffect step. Make MapView follow the lifecycle
    return remember {
        MapView(context).apply {
            id = R.id.map
            onCreate(Bundle())
        }
    }
}

fun GoogleMap.setZoom(
    @FloatRange(from = MinZoom.toDouble(), to = MaxZoom.toDouble()) zoom: Float
) {
    resetMinMaxZoomPreference()
    setMinZoomPreference(zoom)
    setMaxZoomPreference(zoom)
}