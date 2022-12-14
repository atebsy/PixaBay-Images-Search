package com.example.android.coding.challenge.ui.searchphotos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.android.coding.challenge.R
import com.example.android.coding.challenge.databinding.PhotosLoadStateFooterViewItemBinding

class PhotosLoadStateViewHolder(
    private val binding: PhotosLoadStateFooterViewItemBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.errorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): PhotosLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.photos_load_state_footer_view_item, parent, false)
            val binding = PhotosLoadStateFooterViewItemBinding.bind(view)
            return PhotosLoadStateViewHolder(binding, retry)
        }
    }
}
