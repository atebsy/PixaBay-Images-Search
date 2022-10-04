package com.example.android.coding.challenge.data.searchphoto

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.coding.challenge.data.searchphotos.RoomPhotosDataSource
import com.example.android.coding.challenge.database.AppDatabase
import com.nhaarman.mockitokotlin2.spy
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RoomPhotoDetailDataSourceTest {

    lateinit var appDatabase: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Test
    fun  search_photo_that_does_not_exits(){
        val photoDao = spy(appDatabase.photoDetailsDao())
        val roomPhotoDataSource = RoomPhotoDetailDataSource(photoDao)

        runTest {
            val actual = roomPhotoDataSource.searchPhotos(-1)

            Assert.assertEquals(null, actual)
        }
    }

    @After
    fun tearDown() {
        appDatabase.close()
    }
}