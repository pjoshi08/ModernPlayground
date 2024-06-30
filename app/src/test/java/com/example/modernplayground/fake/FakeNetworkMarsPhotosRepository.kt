package com.example.modernplayground.fake

import com.example.modernplayground.data.MarsPhotosRepository
import com.example.modernplayground.model.MarsPhoto

class FakeNetworkMarsPhotosRepository : MarsPhotosRepository {
    override suspend fun getMarsPhotos(): List<MarsPhoto> = FakeDataSource.photosList
}