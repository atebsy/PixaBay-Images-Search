package com.example.android.coding.challenge.api

import retrofit2.http.GET
import retrofit2.http.Query

interface PbApiService {

    @GET("/api")
    suspend fun searchPhotos(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int,
        @Query("key") apiKey: String,
        @Query("image_type") imageType: String,
        @Query("orientation") photoOrientation: String
    ): PhotosSearchApiResponse

    @GET("/api")
    suspend fun searchPhoto(
        @Query("id") photoId: Long,
        @Query("key") apiKey: String,
        @Query("orientation") photoOrientation: String
    ): PhotoDatailsApiResponse

}
