package com.example.modernplayground

import com.example.modernplayground.data.NetworkMarsPhotosRepository
import com.example.modernplayground.fake.FakeDataSource
import com.example.modernplayground.fake.FakeMarsApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkMarsRepositoryTest {

    /**
     * The coroutine test library provides the runTest() function. The function takes the
     * method that you passed in the lambda and runs it from [TestScope], which inherits from
     * [CoroutineScope].
     */
    @Test
    fun networkMarsPhotosRepository_getMarsPhotos_verifyPhotoList() = runTest {
        val repository = NetworkMarsPhotosRepository(
            marsApiService = FakeMarsApiService()
        )
        assertEquals(FakeDataSource.photosList, repository.getMarsPhotos())
    }
}