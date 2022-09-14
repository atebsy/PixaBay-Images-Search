package com.example.android.coding.challenge.data.searchphoto

import com.example.android.coding.challenge.api.Result
import com.example.android.coding.challenge.models.PhotoDetails
import javax.inject.Inject


class PhotoDetailsRepository @Inject constructor(
    private val remotePhotoDetailsDataSource: RemotePhotoDetailsDataSource,
    private val roomPhotosDetailsDataSource: RoomPhotoDetailDataSource
) {

    suspend fun getPhotoDetails(id: Long): Result<PhotoDetails> {

        val apiResult = remotePhotoDetailsDataSource.searchPhoto(id)

        return if (apiResult is Result.Success) {
            roomPhotosDetailsDataSource.insertPhoto(apiResult.data)
            apiResult
        } else {
            val photoDetails = roomPhotosDetailsDataSource.searchPhotos(id)
            if (photoDetails != null) return Result.Success(photoDetails)

            return apiResult
        }

        return apiResult
    }

}
