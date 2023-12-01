package com.example.modernplayground

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WellnessScreen(
    modifier: Modifier = Modifier,
    wellnessViewModel: WellnessViewModel = viewModel()
) {
    Column(modifier = modifier) {
        StatefulCounter()

        // Warning: You can use the mutableStateListOf API instead to create the list. However,
        // the way you use it might result in unexpected recomposition and suboptimal UI performance.
        //
        // If you just define the list and then add the tasks in a different operation it would
        // result in duplicated items being added for every recomposition.
        //
        // Don't do this!
        //
        // val list = remember { mutableStateListOf<WellnessTask>() }
        //
        // list.addAll(getWellnessTasks())
        //
        // Instead, create the list with its initial value in a single operation and then pass it
        // to the remember function, like this:
        //
        // Do this instead. Don't need to copy
        //
        // val list = remember {
        //     mutableStateListOf<WellnessTask>().apply { addAll(getWellnessTasks()) }
        // }

        // rememberSaveable for the list gives runtime exception here
        /// https://developer.android.com/codelabs/jetpack-compose-state#10
        // val list = remember { getWellnessTasks().toMutableStateList() }
        WellnessTasksList(
            list = wellnessViewModel.tasks,
            onCheckedTask = { task, checked ->
                wellnessViewModel.changeTaskChecked(task, checked)
            },
            onCloseTask = { task -> wellnessViewModel.remove(task) })
    }
}