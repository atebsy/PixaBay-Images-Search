package com.example.android.coding.challenge.data.searchphoto


import com.example.android.coding.challenge.models.PhotoDetails


interface PhotoDetailsDataSource {
    suspend fun searchPhotos(id:Long):PhotoDetails
    suspend fun insertPhoto(photoDetails: PhotoDetails)
}