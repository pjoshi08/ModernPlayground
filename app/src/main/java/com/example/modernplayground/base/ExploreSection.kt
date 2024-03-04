package com.example.modernplayground.base

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.modernplayground.data.ExploreModel
import com.example.modernplayground.home.OnExploreItemClicked
import com.example.modernplayground.ui.BottomSheetShape
import com.example.modernplayground.ui.crane_caption
import coil.request.ImageRequest.Builder
import com.example.modernplayground.R
import com.example.modernplayground.ui.crane_divider_color
import kotlinx.coroutines.launch

@Composable
fun ExploreSection(
    modifier: Modifier = Modifier,
    title: String,
    exploreList: List<ExploreModel>,
    onItemClicked: OnExploreItemClicked
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White,
        shape = BottomSheetShape
    ) {
        Column(modifier = Modifier.padding(start = 24.dp, top = 20.dp, end = 24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.caption.copy(color = crane_caption)
            )
            Spacer(Modifier.height(8.dp))
            Box(Modifier.weight(1f)) {
                val listState = rememberLazyListState()
                ExploreList(exploreList, onItemClicked, listState = listState)
                // COMPLETED: derivedStateOf step
                // COMPLETED: Show "Scroll on top" button when the first item of the list is not visible

                // DO NOT DO THIS - It's executed on every recomposition
                // val showButton = listState.firstVisibleItemIndex > 0
                // This solution is not as efficient as it could be, because the composable
                // function reading showButton recomposes as often as firstVisibleItemIndex
                // changes - which happens frequently when scrolling. Instead, you want the
                // function to recompose only when the condition changes between true and false.

                // There's an API that allows you to do this: the derivedStateOf API.

                // listState is an observable Compose State. Your calculation, showButton,
                // 'also needs to be a Compose State since you want the UI to recompose when
                // its value changes, and show or hide the button.

                // Use derivedStateOf when you want a Compose State that's derived from
                // another State. The derivedStateOf calculation block is executed every
                // time the internal state changes, but the composable function only
                // recomposes when the result of the calculation is different from the
                // last one. This minimizes the amount of times functions reading showButton
                // recompose.

                // Using the derivedStateOf API in this case is a better and more efficient
                // alternative. You'll also wrap the call with the remember API, so the
                // calculated value survives recomposition.

                // When Should I use derivedStateOf: https://medium.com/androiddevelopers/jetpack-compose-when-should-i-use-derivedstateof-63ce7954c11b

                // Show the button if the first visible item is past the first item.
                // We use a remembered derived state to minimize unnecessary compositions
                val showButton by remember {
                    derivedStateOf { listState.firstVisibleItemIndex > 0 }
                }
                if (showButton) {
                    val coroutineScope = rememberCoroutineScope()
                    FloatingActionButton(
                        backgroundColor = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .navigationBarsPadding()
                            .padding(bottom = 8.dp),
                        onClick = {
                            coroutineScope.launch {
                                listState.scrollToItem(0)
                            }
                        }) {
                        Text("Up!")
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreList(
    exploreList: List<ExploreModel>,
    onItemClicked: OnExploreItemClicked,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = WindowInsets.navigationBars.asPaddingValues(),
        state = listState
    ) {
        items(exploreList) { exploreItem ->
            Column(Modifier.fillParentMaxWidth()) {
                ExploreItem(
                    modifier = Modifier.fillParentMaxWidth(),
                    item = exploreItem,
                    onItemClicked = onItemClicked
                )
                Divider(color = crane_divider_color)
            }
        }
    }
}

@Composable
private fun ExploreItem(
    modifier: Modifier = Modifier,
    item: ExploreModel,
    onItemClicked: OnExploreItemClicked
) {
    Row(
        modifier = modifier
            .clickable { onItemClicked(item) }
            .padding(top = 12.dp, bottom = 12.dp)
    ) {
        ExploreImageContainer {
            Box {
                val painter = rememberAsyncImagePainter(
                    model = Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build()
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (painter.state is AsyncImagePainter.State.Loading) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_crane_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
        Spacer(Modifier.width(24.dp))
        Column {
            Text(
                text = item.city.nameToDisplay,
                style = MaterialTheme.typography.h6
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.caption.copy(color = crane_caption)
            )
        }
    }
}

@Composable
private fun ExploreImageContainer(content: @Composable () -> Unit) {
    Surface(Modifier.size(width = 60.dp, height = 60.dp), RoundedCornerShape(4.dp)) {
        content()
    }
}