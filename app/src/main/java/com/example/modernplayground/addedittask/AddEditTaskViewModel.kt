package com.example.modernplayground.addedittask

import android.app.Application
import androidx.lifecycle.AndroidViewModel

/**
 * ViewModel for the Add/Edit screen.
 */
class AddEditTaskViewModel(application: Application) : AndroidViewModel(application) {
    // Note, for testing and architecture purposes, it's bad practice to construct the repository
    // here. We'll show you how to fix this during the codelab
    //private val taskRepository =
}