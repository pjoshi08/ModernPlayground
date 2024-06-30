package com.example.modernplayground.fake

import com.example.modernplayground.model.MarsPhoto
import com.example.modernplayground.network.MarsApiService

class FakeMarsApiService : MarsApiService {
    override suspend fun getPhotos(): List<MarsPhoto> = FakeDataSource.photosList
}