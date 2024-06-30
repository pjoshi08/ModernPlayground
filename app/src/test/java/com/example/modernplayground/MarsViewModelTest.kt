package com.example.modernplayground

import com.example.modernplayground.fake.FakeDataSource
import com.example.modernplayground.fake.FakeNetworkMarsPhotosRepository
import com.example.modernplayground.rules.TestDispatcherRule
import com.example.modernplayground.ui.screens.MarsUiState
import com.example.modernplayground.ui.screens.MarsViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MarsViewModelTest {

    @get:Rule
    val testDispatcher = TestDispatcherRule()

    /**
     * Recall that the MarsViewModel calls the repository using viewModelScope.launch().
     * This instruction launches a new coroutine under the default coroutine dispatcher,
     * which is called the Main dispatcher. The Main dispatcher wraps the Android UI thread.
     * The reason for the preceding error is the Android UI thread is not available in a unit
     * test. Unit tests are executed on your workstation, not an Android device or Emulator.
     * If code under a local unit test references the Main dispatcher, an exception
     * (Module with the Main dispatcher had failed to initialize. For tests Dispatchers.setMain
     * from kotlinx-coroutines-test module can be used) is thrown when the unit tests are run.
     * To overcome this issue, you must explicitly define the default dispatcher when running
     * unit tests.
     */
    @Test
    fun marsViewModel_getMarsPhotos_verifyMarsUiStateSuccess() = runTest {
        val marsViewModel = MarsViewModel(
            marsPhotosRepository = FakeNetworkMarsPhotosRepository()
        )
        assertEquals(
            MarsUiState.Success("Success : ${FakeDataSource.photosList.size} Mars "
            + "photos retrieved"),
            marsViewModel.marsUiState
        )
    }
}