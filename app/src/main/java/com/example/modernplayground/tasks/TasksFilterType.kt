package com.example.modernplayground.tasks

/**
 * Used with the filter spinner in the tasks list.
 */
enum class TasksFilterType {
    /**
     * Do no filter tasks.
     */
    ALL_TASKS,

    /**
     * Filters only the active (not completed yet) tasks.
     */
    ACTIVE_TASKS,

    /**
     * Filters only the completed tasks.
     */
    COMPLETED_TASKS
}