package com.example.modernplayground.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modernplayground.R
import com.example.modernplayground.data.Task
import com.example.modernplayground.data.TaskRepository
import com.example.modernplayground.util.Async
import com.example.modernplayground.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatisticsUiState(
    val isEmpty: Boolean = false,
    val isLoading: Boolean = false,
    val activeTasksPercent: Float = 0f,
    val completedTasksPercent: Float = 0f
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    val uiState: StateFlow<StatisticsUiState> =
        taskRepository.getTasksStream()
            .map { Async.Success(it) }
            .catch<Async<List<Task>>> { emit(Async.Error(R.string.loading_task_error)) }
            .map { taskAsync -> produceStatisticsUiState(taskAsync) }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = StatisticsUiState(isLoading = true)
            )

    fun refresh() {
        viewModelScope.launch { taskRepository.refresh() }
    }

    private fun produceStatisticsUiState(taskLoad: Async<List<Task>>) =
        when (taskLoad) {
            Async.Loading -> {
                StatisticsUiState(isLoading = true, isEmpty = true)
            }

            is Async.Error -> {
                // TODO: Show error message?
                StatisticsUiState(isLoading = false, isEmpty = true)
            }

            is Async.Success -> {
                val stats = getActiveAndCompletedStats(taskLoad.data)
                StatisticsUiState(
                    isEmpty = taskLoad.data.isEmpty(),
                    activeTasksPercent = stats.activeTasksPercent,
                    completedTasksPercent = stats.completedTasksPercent,
                    isLoading = false
                )
            }
        }
}