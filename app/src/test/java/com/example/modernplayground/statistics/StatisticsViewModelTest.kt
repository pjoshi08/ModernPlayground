package com.example.modernplayground.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.modernplayground.MainCoroutineRule
import com.example.modernplayground.MainDispatcherRule
import com.example.modernplayground.data.source.FakeTestRepository
import com.example.modernplayground.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class StatisticsViewModelTest {

    // Executes each task synchronously using Architecture components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    //@get:Rule
    //var mainCoroutineRule = MainCoroutineRule() // Deprecated

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private lateinit var statisticsViewModel: StatisticsViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var tasksRepository: FakeTestRepository

    @Before
    fun setupStatisticsViewModel() {
        // Initialize the repository with no tasks
        tasksRepository = FakeTestRepository()

        statisticsViewModel = StatisticsViewModel(tasksRepository)
    }

    @Test
    fun loadTasks_loading() = runTest {
        // Main dispatcher will not run coroutines eagerly for this test
        Dispatchers.setMain(StandardTestDispatcher())

        // Pause the dispatcher so you can verify initial values
        //mainDispatcherRule.pauseDispatcher()

        // Load the task in the view model
        statisticsViewModel.refresh()

        // Then progress indicator is shown
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(true))

        // Execute pending coroutines actions
        //mainCoroutineRule.resumeDispatcher()
        // Wait until coroutine in statisticsViewModel.refresh() completes
        advanceUntilIdle()

        // The progress indicator is hidden
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadStatisticsWhenTasksAreUnavailable_callErrorToDisplay() {
        // Make the repository return errors
        tasksRepository.setReturnError(true)
        statisticsViewModel.refresh()

        // Then empty and error are true (which triggers an error message to be shown)
        assertThat(statisticsViewModel.empty.getOrAwaitValue(), `is`(true))
        assertThat(statisticsViewModel.error.getOrAwaitValue(), `is`(true))
    }
}