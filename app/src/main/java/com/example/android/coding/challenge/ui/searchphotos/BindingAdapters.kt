package com.example.android.coding.challenge.ui.searchphotos

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import coil.load
import com.example.android.coding.challenge.R

@BindingAdapter("imgSrc")
fun ImageView.bindImageSrc(imageUrl:String){
    imageUrl?.let {
        val imgUri = imageUrl.toUri().buildUpon().scheme("https").build()
        this@bindImageSrc.load(imgUri) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }
}