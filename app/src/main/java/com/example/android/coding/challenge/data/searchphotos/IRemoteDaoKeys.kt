package com.example.android.coding.challenge.data.searchphotos

import com.example.android.coding.challenge.database.RemoteKeys

interface IRemoteDaoKeys {

    suspend fun insertAll(remoteKey: List<RemoteKeys>)
    suspend fun remoteKeysRepoId(repoId: Long): RemoteKeys?
    suspend fun clearRemoteKeys()
}