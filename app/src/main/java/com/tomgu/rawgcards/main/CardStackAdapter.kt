package com.tomgu.rawgcards.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.squareup.picasso.Picasso
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.main.api.Game
import io.opencensus.stats.ViewData


class CardStackAdapter(context: Context, resource: Int) : ArrayAdapter<Game>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val imageView = convertView!!.findViewById<ImageView>(R.id.image_content)
        val titleTextView = convertView!!.findViewById<TextView>(R.id.titleTextView)
        val ratingTextView = convertView!!.findViewById<TextView>(R.id.ratingTextView)
        val game : Game = getItem(position)

        titleTextView.setText(game.name)
        ratingTextView.setText(game.rating)
        Picasso.get().load(game.background_image).resize(500,500).into(imageView)

        return convertView
    }

}