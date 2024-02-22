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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.modernplayground.base.CraneDrawer
import com.example.modernplayground.data.ExploreModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.modernplayground.base.CraneTabBar
import com.example.modernplayground.base.CraneTabs
import com.example.modernplayground.base.ExploreSection

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
        CraneHomeContent(
            modifier = modifier.padding(padding),
            onExploreItemClicked = onExploreItemClicked,
            openDrawer = {
                // TODO: rememberCoroutineScope step - open navigation drawer
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
    // TODO: consume stream of data from viewmodel
    val suggestedDestinations: List<ExploreModel> = remember { emptyList() }

    val onPeopleChanged: (Int) -> Unit = { viewModel.updatePeople(it) }
    var tabSelected by remember { mutableStateOf(CraneScreen.Fly) }

    BackdropScaffold(
        modifier = modifier,
        scaffoldState = rememberBackdropScaffoldState(BackdropValue.Revealed),
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
        onMenuClicked = openDrawer) { tabBarModifier ->
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
