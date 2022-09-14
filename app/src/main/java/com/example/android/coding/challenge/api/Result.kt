package com.example.android.coding.challenge.api

sealed class Result <out T : Any>{
    object Loading : Result<Nothing>()
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}
