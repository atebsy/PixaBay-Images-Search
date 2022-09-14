package com.example.android.coding.challenge.di

import android.content.Context
import com.example.android.coding.challenge.data.searchphoto.RoomPhotoDetailDataSource
import com.example.android.coding.challenge.data.searchphotos.RoomPhotosDataSource
import com.example.android.coding.challenge.database.AppDatabase
import com.example.android.coding.challenge.database.PhotoDao
import com.example.android.coding.challenge.database.PhotoDetailsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun providesAppDatabase(@ApplicationContext context: Context) = AppDatabase.getDatabase(context)

    @Provides
    fun providesPhotosDao(appDatabase: AppDatabase) = appDatabase.photosDao()

    @Provides
    fun provideRemoteKeysDao(appDatabase: AppDatabase) = appDatabase.remoteKeysDao()

    @Provides
    fun providesPhotoDetailsDao(appDatabase: AppDatabase) = appDatabase.photoDetailsDao()

    @Singleton
    @Provides
    fun providesRoomPhotosDataSource(photoDao: PhotoDao) = RoomPhotosDataSource(photoDao)

    @Singleton
    @Provides
    fun providesRoomPhotoDetailsDataSource(photoDetailsDao: PhotoDetailsDao) = RoomPhotoDetailDataSource(photoDetailsDao)
}