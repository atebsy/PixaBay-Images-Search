package com.example.android.coding.challenge.data.searchphotos

import androidx.paging.PagingSource
import com.example.android.coding.challenge.database.PhotoDao
import com.example.android.coding.challenge.models.Photo

class RoomPhotosDataSource(private val photoDao: PhotoDao): PhotosDataSource {

    override fun searchPhotos(searchString: String): PagingSource<Int, Photo> {
       return photoDao.searchPhotos(searchString)
    }

    override suspend fun deleteAllPhotos() {
        photoDao.deleteAllPhotos()
    }

    override suspend fun insertPhotos(photos: List<Photo>) {
        photoDao.insertAll(photos)
    }
}