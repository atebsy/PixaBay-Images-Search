package com.example.android.coding.challenge.data.searchphotos

import com.example.android.coding.challenge.models.Photo


interface PhotosDataSource {
    fun searchPhotos(searchString:String):Any
    suspend fun deleteAllPhotos()
    suspend fun insertPhotos(photos: List<Photo>)
}