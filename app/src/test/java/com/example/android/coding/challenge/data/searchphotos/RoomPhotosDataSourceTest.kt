package com.example.android.coding.challenge.data.searchphotos

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.coding.challenge.database.AppDatabase
import com.example.android.coding.challenge.models.Photo
import com.nhaarman.mockitokotlin2.spy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
class RoomPhotosDataSourceTest {

    lateinit var appDatabase: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        appDatabase.close()
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    @Config(manifest=Config.NONE)
    fun test_insert_and_search_photos_when_loading_first_page_for_the_first_time() {
        val photosDao = spy(appDatabase.photosDao())
        val roomPhotoDataSource = RoomPhotosDataSource(photosDao)
        val photo1 = Photo(
            id = 1,
            searchString = "fruits",
            userName = "test_user",
            photoTags = "tag1,tag2",
            photoThumbnail = "https://thumb1.com"
        )
        val photo2 = Photo(
            id = 2,
            searchString = "fruits",
            userName = "test_user_2",
            photoTags = "tag3,tag4",
            photoThumbnail = "https://thumb2.com"
        )
        val photos = listOf(photo1, photo2)

        val expected = PagingSource.LoadResult.Page(
            data = photos,
            prevKey = null,
            nextKey = null,
            itemsBefore = 0,
            itemsAfter = 0
        )
        runTest {
            roomPhotoDataSource.insertPhotos(photos)
            val dbResult = roomPhotoDataSource.searchPhotos("fruits")

            val actual = dbResult.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )

            Assert.assertEquals(expected, actual)
        }
    }

}