package com.example.android.coding.challenge.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.coding.challenge.models.PhotoDetails

@Dao
interface PhotoDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoDetails(photoDetails: PhotoDetails)

    @Query("SELECT * FROM photo_detail WHERE id = :id")
    suspend fun searchPhoto(id: Long): PhotoDetails
}