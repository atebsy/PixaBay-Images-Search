package com.example.android.coding.challenge

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import javax.inject.Inject

class DataStoreUtil @Inject constructor(val dataStore: DataStore<Preferences>) {

    suspend fun setHasSuggestion(hasSuggestions: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_SUGGESTION] = hasSuggestions
        }
    }


}