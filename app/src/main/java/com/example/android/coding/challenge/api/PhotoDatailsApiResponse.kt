package com.example.android.coding.challenge.api

import com.example.android.coding.challenge.models.PhotoDetails
import com.google.gson.annotations.SerializedName

data class PhotoDatailsApiResponse(
    @SerializedName("hits") val photoDetails: List<PhotoDetails> = emptyList()
)