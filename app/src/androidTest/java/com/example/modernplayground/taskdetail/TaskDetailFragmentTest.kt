package com.example.modernplayground.taskdetail

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.modernplayground.R
import com.example.modernplayground.ServiceLocator
import com.example.modernplayground.data.Task
import com.example.modernplayground.data.source.FakeAndroidTestRepository
import com.example.modernplayground.data.source.TasksRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// @MediumTest—Marks the test as a "medium run-time" integration test (versus @SmallTest unit
// tests and @LargeTest end-to-end tests). This helps you group and choose which size of test to run.
@MediumTest
@RunWith(AndroidJUnit4::class)
class TaskDetailFragmentTest {

    private lateinit var repository: TasksRepository

    @Before
    fun initRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun cleanupDb() = runBlocking { ServiceLocator.resetRepository() }

    // FragmentScenario is a class from AndroidX Test that wraps around a fragment and gives
    // you direct control over the fragment's lifecycle for testing. To write tests for fragments,
    // you create a FragmentScenario for the fragment you're testing
    @Test
    fun activeTaskDetails_DisplayedInUi() = runTest { // runBlocking gives error
        // GIVEN - Add active (incomplete) task to the DB
        val activeTask = Task("Active Task", "AndroidX Rocks", false)
        repository.saveTask(activeTask)

        // WHEN - Details fragment launched to display task
        // Creates a Bundle, which represents the fragment arguments for the task that get passed
        // into the fragment.
        val bundle = TaskDetailFragmentArgs(activeTask.id).toBundle()
        // The launchFragmentInContainer function creates a FragmentScenario, with this bundle and a theme.
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)
    }
}