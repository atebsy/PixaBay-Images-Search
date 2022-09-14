package com.example.android.coding.challenge.ui.photodetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.coding.challenge.api.Result
import com.example.android.coding.challenge.data.searchphoto.PhotoDetailsRepository
import com.example.android.coding.challenge.models.PhotoDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDetailsViewModel @Inject constructor(
    private val photoDetailsRepository: PhotoDetailsRepository
) : ViewModel() {

    private var _getPhotoDetailsResult = MutableStateFlow<Result<PhotoDetails>>(Result.Loading)
    val getPhotoDetailsResult: StateFlow<Result<PhotoDetails>> = _getPhotoDetailsResult

    fun getPhotoDetails(photoId: Long) {
        viewModelScope.launch {
            _getPhotoDetailsResult.value = photoDetailsRepository.getPhotoDetails(photoId)
        }
    }

}