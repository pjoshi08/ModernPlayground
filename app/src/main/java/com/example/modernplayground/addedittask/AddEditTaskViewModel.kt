package com.example.modernplayground.addedittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modernplayground.R
import com.example.modernplayground.TodoDestinationArgs
import com.example.modernplayground.data.DefaultTaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.RuntimeException
import javax.inject.Inject

// UiSate for Add/Edit screen
data class AddEditTaskUiState(
    val title: String = "",
    val description: String = "",
    val isTaskCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isTaskSaved: Boolean = false
)

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskRepository: DefaultTaskRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val taskId: String? = savedStateHandle[TodoDestinationArgs.TASK_ID_ARG]

    // A MutableStateFlow needs to be created in this ViewModel. The source of truth of the current
    // editable Task is the ViewModel, we need to mutate the UI State directly in the methods such as
    // `updateTitle` pr `updateDescription`
    private val _uiState = MutableStateFlow(AddEditTaskUiState())
    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()

    init {
        if (taskId != null) { loadTask(taskId) }
    }

    // Called when clicking on fab
    fun saveTask() {
        if (uiState.value.title.isEmpty() || uiState.value.description.isEmpty()) {
            _uiState.update { it.copy(userMessage = R.string.empty_task_message) }
            return
        }

        if (taskId == null) {
            createNewTask()
        } else {
            updateTask()
        }
    }

    fun snackBarMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun updateTitle(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    private fun createNewTask() = viewModelScope.launch {
        taskRepository.create(uiState.value.title, uiState.value.description)
        _uiState.update { it.copy(isTaskSaved = true) }
    }

    private fun updateTask() {
        if (taskId == null) {
            throw RuntimeException("updateTask() was called but task is new.")
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isTaskSaved = true) }
        }
    }

    private fun loadTask(tasId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {

        }
    }
}