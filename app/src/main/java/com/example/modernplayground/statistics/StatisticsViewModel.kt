package com.example.modernplayground.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.modernplayground.TodoApplication
import com.example.modernplayground.data.Task
import com.example.modernplayground.data.source.DefaultTasksRepository
import kotlinx.coroutines.launch
import com.example.modernplayground.data.Result
import com.example.modernplayground.data.Result.Success
import com.example.modernplayground.data.Result.Error


/**
 * ViewModel for the statistics screen.
 */
class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val tasksRepository = (application as TodoApplication).tasksRepository

    private val tasks: LiveData<Result<List<Task>>> = tasksRepository.observeTasks()
    private val _dataLoading = MutableLiveData<Boolean>(false)
    private val stats: LiveData<StatsResult?> = tasks.map {
        if (it is Success) {
            getActiveAndCompletedStats(it.data)
        } else {
            null
        }
    }

    val activeTasksPercent = stats.map {
        it?.activeTasksPercent ?: 0f }
    val completedTasksPercent: LiveData<Float> = stats.map { it?.completedTasksPercent ?: 0f }
    val dataLoading: LiveData<Boolean> = _dataLoading
    val error: LiveData<Boolean> = tasks.map { it is Error }
    val empty: LiveData<Boolean> = tasks.map { (it as? Success)?.data.isNullOrEmpty() }

    fun refresh() {
        _dataLoading.value = true
        viewModelScope.launch {
            tasksRepository.refreshTasks()
            _dataLoading.value = false
        }
    }
}