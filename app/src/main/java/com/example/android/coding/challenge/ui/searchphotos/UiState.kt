package com.example.android.coding.challenge.ui.searchphotos

import com.example.android.coding.challenge.Constants.DEFAULT_QUERY

data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false
)