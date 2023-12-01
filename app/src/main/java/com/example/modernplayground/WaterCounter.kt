package com.example.modernplayground

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

 /// Memory in composable functions: https://developer.android.com/codelabs/jetpack-compose-state#4
 /// State driven UI: https://developer.android.com/codelabs/jetpack-compose-state#5
@Composable
fun WaterCounter(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        //var count by remember { mutableStateOf(0) }
        // A composable that uses remember to store an object contains internal state, which makes
        // the composable stateful. This is useful in situations where a caller doesn't need to
        // control the state and can use it without having to manage the state themselves. However,
        // composables with internal state tend to be less reusable and harder to test.

        // Composables that don't hold any state are called stateless composables. An easy way to
        // create a stateless composable is by using state hoisting.
        //
        // State hoisting in Compose is a pattern of moving state to a composable's caller to make
        // a composable stateless. The general pattern for state hoisting in Jetpack Compose is to
        // replace the state variable with two parameters:
        //
        //* value: T - the current value to display
        //* onValueChange: (T) -> Unit - an event that requests the value to change with a new
        // value T

        // The pattern where the state goes down, and events go up is called Unidirectional
        // Data Flow (UDF), and state hoisting is how we implement this architecture in Compose.
        var count by rememberSaveable { mutableStateOf(0) }
        if (count > 0) {
            /*var showTask by remember { mutableStateOf(true) }
            if (showTask) {
                WellnessTaskItem(
                    taskName = "Have you taken your 15 minute walk today?",
                    onClose = { showTask = false }
                )
            }*/
            Text(
                text = "You've had $count glasses.",
                modifier = modifier.padding(16.dp)
            )
        }

        /*Row(Modifier.padding(top = 8.dp)){

            Button(
                onClick = { count = 0 },
                Modifier.padding(top = 8.dp, start = 8.dp)) {
                Text("Clear water count")
            }
        }*/
        Button(onClick = { count++ }, Modifier.padding(top = 8.dp), enabled = count < 10) {
            Text("Add one")
        }
    }
}

@Composable
fun StatelessCounter(count: Int, onIncrement: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        if (count > 0) {
            Text("You've had $count glasses.")
        }
        Button(onClick = onIncrement, Modifier.padding(top = 8.dp), enabled = count < 10) {
            Text("Add one")
        }
    }
}

@Composable
fun StatefulCounter(modifier: Modifier = Modifier) {
    var count by rememberSaveable { mutableStateOf(0) }
    StatelessCounter(count = count, onIncrement = { count++ }, modifier)
}