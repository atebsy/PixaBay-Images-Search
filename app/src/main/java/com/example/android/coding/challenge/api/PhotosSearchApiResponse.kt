package com.example.android.coding.challenge.api

import com.example.android.coding.challenge.models.Photo
import com.google.gson.annotations.SerializedName

data class PhotosSearchApiResponse(
    @SerializedName("hits") val items: List<Photo> = emptyList()
)
