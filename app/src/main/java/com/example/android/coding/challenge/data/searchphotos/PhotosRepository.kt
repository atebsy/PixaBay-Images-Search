package com.example.android.coding.challenge.data.searchphotos

import androidx.paging.*
import com.example.android.coding.challenge.Constants.NETWORK_PAGE_SIZE
import com.example.android.coding.challenge.api.PbApiService
import com.example.android.coding.challenge.database.AppDatabase
import com.example.android.coding.challenge.database.RemoteKeysDao
import com.example.android.coding.challenge.models.Photo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository class that works with local and remote data sources.
 */
class PhotosRepository @Inject constructor(
    private val pbApiService: PbApiService,
    private val appDatabase: AppDatabase,
    private val remoteKeysDao: RemoteKeysDao,
    private val roomPhotosDataSource: RoomPhotosDataSource
) {

    fun getSearchResultStream(query: String): Flow<PagingData<Photo>> {

        val pagingSourceFactory = { roomPhotosDataSource.searchPhotos(query) }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = PbPhotosRemoteMediator(
                query,
                pbApiService,
                appDatabase,
                roomPhotosDataSource,
                remoteKeysDao
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

}
