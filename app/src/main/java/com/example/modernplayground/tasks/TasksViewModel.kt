package com.example.modernplayground.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modernplayground.ADD_EDIT_RESULT_OK
import com.example.modernplayground.DELETE_RESULT_OK
import com.example.modernplayground.EDIT_RESULT_OK
import com.example.modernplayground.R
import com.example.modernplayground.data.Task
import com.example.modernplayground.util.Async
import com.example.modernplayground.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.modernplayground.tasks.TasksFilterType.ACTIVE_TASKS
import com.example.modernplayground.tasks.TasksFilterType.COMPLETED_TASKS
import com.example.modernplayground.tasks.TasksFilterType.ALL_TASKS
import kotlinx.coroutines.flow.stateIn


data class TasksUiState(
    val items: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val filteringUiInfo: FilteringUiInfo = FilteringUiInfo(),
    val userMessage: Int? = null
)

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    // TODO: private val taskRepository: DefaultTaskRepository
) : ViewModel() {

    private val tasksStream = flow<List<Task>> { emptyList<Task>() }

    private val _savedFilterType = savedStateHandle
        .getStateFlow(TASKS_FILTER_SAVED_STATE_KEY, ALL_TASKS)

    private val _filterUiInfo = _savedFilterType.map { getFilterUiInfo(it) }.distinctUntilChanged()
    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)
    private val _filteredTasksAsync =
        combine(tasksStream, _savedFilterType) { tasks, type ->
            filterTasks(tasks, type)
        }
            .map { Async.Success(it) }
            .catch<Async<List<Task>>> { emit(Async.Error(R.string.loading_task_error)) }

    val uiState: StateFlow<TasksUiState> = combine(
        _filterUiInfo, _isLoading, _userMessage, _filteredTasksAsync
    ) { filterUiInfo, isLoading, userMessage, tasksAsync ->
        when (tasksAsync) {
            Async.Loading -> {
                TasksUiState(isLoading = true)
            }

            is Async.Error -> {
                TasksUiState(userMessage = tasksAsync.errorMessage)
            }

            is Async.Success -> {
                TasksUiState(
                    items = tasksAsync.data,
                    filteringUiInfo = filterUiInfo,
                    isLoading = isLoading,
                    userMessage = userMessage
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = TasksUiState(isLoading = true)
        )

    fun setFiltering(requestType: TasksFilterType) {
        savedStateHandle[TASKS_FILTER_SAVED_STATE_KEY] = requestType
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            showSnackBarMessage(R.string.completed_tasks_cleared)
            refresh()
        }
    }

    fun completeTask(task: Task, completed: Boolean) {
        if (completed) {
            // TODO: taskRepository.completeTask(task.id)
            showSnackBarMessage(R.string.task_marked_complete)
        } else {
            showSnackBarMessage(R.string.task_marked_active)
        }
    }

    fun showEditResultMessage(result: Int) {
        when (result) {
            EDIT_RESULT_OK -> showSnackBarMessage(R.string.successfully_saved_task_message)
            ADD_EDIT_RESULT_OK -> showSnackBarMessage(R.string.successfully_added_task_message)
            DELETE_RESULT_OK -> showSnackBarMessage(R.string.successfully_deleted_task_message)
        }
    }

    fun snackBarMessageShown() {
        _userMessage.value = null
    }

    private fun showSnackBarMessage(message: Int) {
        _userMessage.value = message
    }

    fun refresh() {
        _isLoading.value = true
        viewModelScope.launch {
            // TODO: taskRepository.refresh()
            _isLoading.value = false
        }
    }

    private fun filterTasks(tasks: List<Task>, filterType: TasksFilterType): List<Task> {
        val tasksToShow = ArrayList<Task>()
        // We filter the tasks based on the requestType
        for (task in tasks) {
            when (filterType) {
                ALL_TASKS -> tasksToShow.add(task)
                ACTIVE_TASKS -> if (task.isActive) {
                    tasksToShow.add(task)
                }

                COMPLETED_TASKS -> if (task.isCompleted) {
                    tasksToShow.add(task)
                }
            }
        }

        return tasksToShow
    }

    private fun getFilterUiInfo(requestType: TasksFilterType): FilteringUiInfo =
        when (requestType) {
            ALL_TASKS -> {
                FilteringUiInfo(
                    R.string.label_all, R.string.no_tasks_all,
                    R.drawable.logo_no_fill
                )
            }

            ACTIVE_TASKS -> {
                FilteringUiInfo(
                    R.string.label_active, R.string.no_tasks_active,
                    R.drawable.ic_check_circle_96dp
                )
            }

            COMPLETED_TASKS -> {
                FilteringUiInfo(
                    R.string.label_completed, R.string.no_tasks_completed,
                    R.drawable.ic_verified_user_96dp
                )
            }
        }
}

// Used to save the current filtering in SavedStateHandle
const val TASKS_FILTER_SAVED_STATE_KEY = "TASKS_FILTER_SAVED_STATE_KEY"

data class FilteringUiInfo(
    val currentFilteringLabel: Int = R.string.label_all,
    val noTasksLabel: Int = R.string.no_tasks_all,
    val noTaskIconRes: Int = R.drawable.logo_no_fill
)