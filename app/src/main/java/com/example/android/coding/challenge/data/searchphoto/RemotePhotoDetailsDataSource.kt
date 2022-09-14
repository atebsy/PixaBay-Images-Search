package com.example.android.coding.challenge.data.searchphoto

import com.example.android.coding.challenge.BuildConfig
import com.example.android.coding.challenge.Constants
import com.example.android.coding.challenge.api.PbApiService
import com.example.android.coding.challenge.api.Result
import com.example.android.coding.challenge.models.PhotoDetails
import java.io.IOException
import javax.inject.Inject

class RemotePhotoDetailsDataSource @Inject constructor(private val pbApiService: PbApiService) {
    suspend fun searchPhoto(id: Long): Result<PhotoDetails> {
        return try {
            val apiResponse =
                pbApiService.searchPhoto(id, BuildConfig.PB_API_KEY, Constants.PHOTO_ORIENTATION)
            if (apiResponse.photoDetails.isNotEmpty()) Result.Success(apiResponse.photoDetails[0]) else Result.Error(IOException())
        } catch (e: Throwable) {
            Result.Error(IOException(e))
        }
    }
}