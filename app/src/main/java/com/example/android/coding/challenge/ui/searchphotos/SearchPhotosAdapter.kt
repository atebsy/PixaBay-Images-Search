package com.example.android.coding.challenge.ui.searchphotos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.coding.challenge.databinding.PhotosListItemBinding
import com.example.android.coding.challenge.models.Photo


class SearchPhotosAdapter(
    private val onItemClicked: (Photo) -> Unit
) : PagingDataAdapter<Photo, SearchPhotosAdapter.PbPhotosViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean =
                    oldItem == newItem
        }
    }

    class PbPhotosViewHolder(private val binding: PhotosListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: Photo){
            binding.photo = photo
        }
    }


    override fun onBindViewHolder(holder: PbPhotosViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PbPhotosViewHolder {
        val viewHolder = PbPhotosViewHolder(
            PhotosListItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.bindingAdapterPosition
            getItem(position)?.let { it1 -> onItemClicked(it1) }
        }
        return viewHolder
    }


}