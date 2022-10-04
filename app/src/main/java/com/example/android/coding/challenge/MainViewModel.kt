package com.example.android.coding.challenge

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.android.coding.challenge.Constants.DEFAULT_QUERY
import com.example.android.coding.challenge.Constants.LAST_QUERY_SCROLLED
import com.example.android.coding.challenge.Constants.LAST_SEARCH_QUERY
import com.example.android.coding.challenge.data.searchphotos.PhotosRepository
import com.example.android.coding.challenge.models.Photo
import com.example.android.coding.challenge.models.SuggestionPreference
import com.example.android.coding.challenge.ui.searchphotos.UiAction
import com.example.android.coding.challenge.ui.searchphotos.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PhotosRepository,
    private val savedStateHandle: SavedStateHandle,
    private val dataStoreUtil: DataStoreUtil,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private var _searchString = MutableStateFlow("")
    val searchString: StateFlow<String> = _searchString

    private var _hasSuggestion =
        MutableStateFlow<Boolean>(false)
    val hasSuggestion: StateFlow<Boolean> = _hasSuggestion

    /**
     * Stream of immutable states representative of the UI.
     */
    val state: StateFlow<UiState>

    val pagingDataFlow: Flow<PagingData<Photo>>

    /**
     * Processor of side effects from the UI which in turn feedback into [state]
     */
    val accept: (UiAction) -> Unit

    init {
        val initialQuery: String = savedStateHandle.get(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        val lastQueryScrolled: String = savedStateHandle.get(LAST_QUERY_SCROLLED) ?: DEFAULT_QUERY
        _searchString.value = initialQuery
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Search(query = initialQuery)) }
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            // This is shared to keep the flow "hot" while caching the last query scrolled,
            // otherwise each flatMapLatest invocation would lose the last query scrolled,
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }

        pagingDataFlow = searches
            .flatMapLatest { searchPhotos(queryString = it.query) }
            .cachedIn(viewModelScope)

        state = combine(
            searches,
            queriesScrolled,
            ::Pair
        ).map { (search, scroll) ->
            UiState(
                query = search.query,
                lastQueryScrolled = scroll.currentQuery,
                // If the search query matches the scroll query, the user has scrolled
                hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }

        viewModelScope.launch {

            val preferences: Preferences = dataStore.data.first().toPreferences()
            _hasSuggestion.value =preferences[PreferencesKeys.HAS_SUGGESTION] ?: false
        }
    }


    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        super.onCleared()
    }

    private fun searchPhotos(queryString: String): Flow<PagingData<Photo>> =
        repository.getSearchResultStream(queryString)

    fun updateSearchString(searchString: String) {
        _searchString.value = searchString
        setHasSuggestions(true)
    }

    fun setHasSuggestions(hasSuggestions: Boolean = false) {
        viewModelScope.launch {
            dataStoreUtil.setHasSuggestion(hasSuggestions)
            _hasSuggestion.value = hasSuggestions
        }
    }

}


