package com.example.modernplayground.statistics

import com.example.modernplayground.data.Task
import org.hamcrest.Matchers.`is`
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat

class StatisticsUtilsTest {

    // Naming convention: subjectUnderTest_actionOrInput_resultState
    @Test
    fun getActiveAndCompletedStats_noCompleted_returnsHundredZero() {
        // Create an active task
        val tasks = listOf<Task>(
            Task("title", "desc", isCompleted = false)
        )

        // Call your function
        val result = getActiveAndCompletedStats(tasks)

        // Check the result
        //assertEquals(result.activeTasksPercent, 100f)
        assertThat(result.activeTasksPercent, `is`(100f))
        //assertEquals(result.completedTasksPercent, 0f)
        assertThat(result.completedTasksPercent, `is`(0f))
    }
}