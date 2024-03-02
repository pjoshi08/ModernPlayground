package com.example.modernplayground.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.modernplayground.base.CraneDrawer
import com.example.modernplayground.data.ExploreModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.modernplayground.base.CraneTabBar
import com.example.modernplayground.base.CraneTabs
import com.example.modernplayground.base.ExploreSection
import kotlinx.coroutines.launch

typealias OnExploreItemClicked = (ExploreModel) -> Unit

enum class CraneScreen {
    Fly, Sleep, Eat
}

@Composable
fun CraneHome(
    onExploreItemClicked: OnExploreItemClicked,
    modifier: Modifier = Modifier
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.statusBarsPadding(),
        drawerContent = { CraneDrawer() }
    ) { padding ->
        // Using the rememberCoroutineScope API returns a CoroutineScope bound to the point in
        // the Composition where you call it. The scope will be automatically canceled once it
        // leaves the Composition. With that scope, you can start coroutines when you're not in
        // the Composition.

        // Looking back at the landing screen step that used LaunchedEffect, could you use
        // rememberCoroutineScope and call scope.launch { delay(); onTimeout(); } instead of
        // using LaunchedEffect?

        // You could've done that, and it would've seemed to work, but it wouldn't be correct.
        // As explained in the Thinking in Compose documentation, composables can be called by
        // Compose at any moment. LaunchedEffect guarantees that the side-effect will be executed
        // when the call to that composable makes it into the Composition. If you use
        // rememberCoroutineScope and scope.launch in the body of the LandingScreen, the coroutine
        // will be executed every time LandingScreen is called by Compose regardless of whether
        // that call makes it into the Composition or not. Therefore, you'll waste resources and
        // you won't be executing this side-effect in a controlled environment.
        val scope = rememberCoroutineScope()
        CraneHomeContent(
            modifier = modifier.padding(padding),
            onExploreItemClicked = onExploreItemClicked,
            openDrawer = {
                // COMPLETED: rememberCoroutineScope step - open navigation drawer

                // You cannot use LaunchedEffect as before because we cannot call composables
                // in openDrawer. We're not in the Composition.
                scope.launch {
                    // Suspend functions, in addition to being able to run asynchronous code,
                    // also help represent concepts that happen over time. As opening the drawer
                    // requires some time, movement, and potential animations, that's perfectly
                    // reflected with the suspend function, which will suspend the execution of
                    // the coroutine where it's been called until it finishes and resumes execution.
                    scaffoldState.drawerState.open()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CraneHomeContent(
    modifier: Modifier = Modifier,
    onExploreItemClicked: (ExploreModel) -> Unit,
    openDrawer: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    // COMPLETED: consume stream of data from viewmodel
    /// [collectAsStateWithLifecycle()] collects values from the StateFlow and represents the
    /// latest value via Compose's State API in a lifecycle-aware manner. This will make the
    /// Compose code that reads that state value recompose on new emissions.
    /// Also available: Livedata.observeAsState(), Observable.subscribeAsState()
    val suggestedDestinations by viewModel.suggestedDestinations.collectAsStateWithLifecycle()

    val onPeopleChanged: (Int) -> Unit = { viewModel.updatePeople(it) }
    var tabSelected by remember { mutableStateOf(CraneScreen.Fly) }

    BackdropScaffold(
        modifier = modifier,
        scaffoldState = rememberBackdropScaffoldState(BackdropValue.Revealed),
        frontLayerScrimColor = Color.Unspecified,
        appBar = {
            HomeTabBar(openDrawer, tabSelected, onTabSelected = { tabSelected = it })
        },
        backLayerContent = {
            SearchContent(tabSelected, viewModel, onPeopleChanged)
        },
        frontLayerContent = {
            when (tabSelected) {
                CraneScreen.Fly -> {
                    ExploreSection(
                        title = "Explore Flights by Destination",
                        exploreList = suggestedDestinations,
                        onItemClicked = onExploreItemClicked
                    )
                }
                CraneScreen.Sleep -> {
                    ExploreSection(
                        title = "Explore Properties by Destination",
                        exploreList = viewModel.hotels,
                        onItemClicked = onExploreItemClicked
                    )
                }
                CraneScreen.Eat -> {
                    ExploreSection(
                        title = "Explore Restaurants by Destination",
                        exploreList = viewModel.restaurants,
                        onItemClicked = onExploreItemClicked
                    )
                }
            }
        }
    )
}

@Composable
fun HomeTabBar(
    openDrawer: () -> Unit,
    tabSelected: CraneScreen,
    onTabSelected: (CraneScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    CraneTabBar(
        modifier = modifier,
        onMenuClicked = openDrawer
    ) { tabBarModifier ->
        CraneTabs(
            modifier = tabBarModifier,
            titles = CraneScreen.values().map { it.name },
            tabSelected = tabSelected,
            onTabSelected = { newTab: CraneScreen -> onTabSelected(CraneScreen.values()[newTab.ordinal]) }
        )
    }
}

@Composable
fun SearchContent(
    tabSelected: CraneScreen,
    viewModel: MainViewModel,
    onPeopleChanged: (Int) -> Unit
) {
    when (tabSelected) {
        CraneScreen.Fly -> FlySearchContent(
            onPeopleChanged = onPeopleChanged,
            onToDestinationChanged = { viewModel.toDestinationChanged(it) }
        )
        CraneScreen.Sleep -> SleepSearchContent(
            onPeopleChanged = onPeopleChanged
        )
        CraneScreen.Eat -> EatSearchContent(
            onPeopleChanged = onPeopleChanged
        )
    }
}
