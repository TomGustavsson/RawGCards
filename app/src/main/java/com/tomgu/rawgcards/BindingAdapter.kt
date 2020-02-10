package com.tomgu.rawgcards

import android.widget.ImageView
import android.widget.RatingBar
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

object BindingAdapter {

    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadImageUrl(view: ImageView, url: String?){
        Picasso.get().load(url).resize(500, 500).into(view)
    }

    @JvmStatic
    @BindingAdapter("loadImageCircle")
    fun loadImageUrlCircle(view: de.hdodenhof.circleimageview.CircleImageView, url: String?){
            Picasso.get().load(url).resize(500, 500).into(view)
    }

    @JvmStatic
    @BindingAdapter("loadImageResource")
    fun loadImageResource(view: ImageView, resource: Int){
        Picasso.get().load(resource).into(view)
    }

    @JvmStatic
    @BindingAdapter("getRatingFloat")
    fun getRatingFloat(bar : RatingBar, string : String){
        bar.rating = string.toFloat()
    }


}