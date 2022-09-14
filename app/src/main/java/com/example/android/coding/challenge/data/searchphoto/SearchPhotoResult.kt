package com.example.android.coding.challenge.data.searchphoto

import com.example.android.coding.challenge.models.PhotoDetails

data class SearchPhotoResult(
    val success: PhotoDetails,
    val error: Int
)
