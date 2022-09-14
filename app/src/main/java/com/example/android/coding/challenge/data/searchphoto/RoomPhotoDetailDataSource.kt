package com.example.android.coding.challenge.data.searchphoto

import com.example.android.coding.challenge.database.PhotoDetailsDao
import com.example.android.coding.challenge.models.PhotoDetails

class RoomPhotoDetailDataSource(private val photoDetailsDao: PhotoDetailsDao): PhotoDetailsDataSource {
    override suspend fun searchPhotos(id: Long): PhotoDetails {
        return photoDetailsDao.searchPhoto(id)
    }

    override suspend fun insertPhoto(photoDetails: PhotoDetails) {
        photoDetailsDao.insertPhotoDetails(photoDetails)
    }

}