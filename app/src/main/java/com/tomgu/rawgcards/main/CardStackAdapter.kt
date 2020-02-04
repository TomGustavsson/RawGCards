package com.tomgu.rawgcards.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.databinding.CardLayoutBinding
import com.tomgu.rawgcards.main.api.Game


class CardStackAdapter(context: Context, resource: Int) : ArrayAdapter<Game>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

       val binding:CardLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.card_layout, null, false)

        val game : Game = getItem(position)!!

        if(position == 0){
            binding.imageContent.transitionName = "image_trans"
        }
        binding.game = game
        binding.ratingBarCard.rating = game.rating.toFloat()
        binding.progressBarCard.visibility = View.GONE
        return binding.root
    }

}