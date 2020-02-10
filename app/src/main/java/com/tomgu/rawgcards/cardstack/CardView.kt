package com.tomgu.rawgcards.cardstack

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.constraintlayout.widget.Constraints
import androidx.databinding.DataBindingUtil
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.databinding.CardLayoutBinding
import com.tomgu.rawgcards.main.api.Game

class CardView(context: Context, layoutParams: Constraints.LayoutParams): FrameLayout(context) {

    var binding : CardLayoutBinding
    var viewGame: Game? = null

    init {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.card_layout, this, true)
        this.layoutParams = layoutParams
    }

    fun setCardGame(game : Game){
        viewGame = game
        binding.game = game
        binding.ratingBarCard.rating = game.rating.toFloat()

    }
}