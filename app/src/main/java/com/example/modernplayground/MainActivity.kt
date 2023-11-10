package com.example.modernplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.modernplayground.ui.theme.CupCakeTheme
import com.google.accompanist.appcompattheme.AppCompatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            CupCakeTheme {
                CupcakeApp()
            }
        }
    }
}

/**
 * Next Steps:
 * Nav with compose: https://developer.android.com/jetpack/compose/navigation
 * Principles: https://developer.android.com/guide/navigation/principles
 * codelab: https://developer.android.com/codelabs/jetpack-compose-navigation#0
 * Nav Material Desing: https://m2.material.io/design/navigation/understanding-navigation.html
 */
