package com.example.android.coding.challenge.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "photo_detail")
data class PhotoDetails(
    @PrimaryKey @field:SerializedName("id") val id: Long,
    @field:SerializedName("webformatURL") val largeImage: String?,
    @field:SerializedName("downloads") val numberOfDownloads: Int?,
    @field:SerializedName("likes") val numberOfLikes: Int?,
    @field:SerializedName("comments") val numberOfComments: String?
)