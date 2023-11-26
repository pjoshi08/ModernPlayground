package com.example.modernplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.modernplayground.ui.accounts.AccountsScreen
import com.example.modernplayground.ui.accounts.SingleAccountScreen
import com.example.modernplayground.ui.bills.BillsScreen
import com.example.modernplayground.ui.components.RallyTabRow
import com.example.modernplayground.ui.overview.OverviewScreen
import com.example.modernplayground.ui.theme.RallyTheme

/**
 * This Activity recreates part of the Rally Material Study from
 * https://material.io/design/material-studies/rally.html
 */
class RallyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RallyApp()
        }
    }
}

@Composable
fun RallyApp() {
    RallyTheme {

        // The [NavController] is the central component when using Navigation in Compose.
        // It keeps track of back stack composable entries, moves the stack forward,
        // enables back stack manipulation, and navigates between destination states.
        // Because NavController is central to navigation, it has to be created as a
        // first step in setting up Compose Navigation.

        // This creates and remembers a NavController which survives configuration changes
        // (using rememberSaveable).
        val navController = rememberNavController()

        val currentBackStack by navController.currentBackStackEntryAsState()
        // Fetch your currentDestination
        val currentDestination = currentBackStack?.destination
        // Change the current screen to this and use Overview as a backup screen if this
        // returns null
        var currentScreen =
            rallyTabRowScreens.find { it.route == currentDestination?.route } ?: Overview

        Scaffold(
            topBar = {
                RallyTabRow(
                    allScreens = rallyTabRowScreens,
                    // To make your code testable and reusable, it is advised not to
                    // pass the entire navController to your composables directly.
                    // Instead, you should always provide callbacks that define the exact
                    // navigation actions you wish to trigger.
                    onTabSelected = { newScreen ->
                        navController.navigateSingleTopTo(newScreen.route)
                    },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->

            RallyNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

