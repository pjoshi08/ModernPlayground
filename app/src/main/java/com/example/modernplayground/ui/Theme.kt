package com.example.modernplayground.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AnimationCodelabTheme(content: @Composable () -> Unit) {
    val colors = lightColorScheme(
        primary = Melon,
        primaryContainer = PaleDogwood,
        onPrimary = Color.Black,
        secondary = Peach,
        onSecondary = Color.Black
    )
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}