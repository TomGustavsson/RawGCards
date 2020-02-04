package com.tomgu.rawgcards

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.tomgu.rawgcards.databinding.CardLayoutBinding
import com.tomgu.rawgcards.main.api.Game


class CategorieShape(context: Context, attributeSet: AttributeSet): FrameLayout(context, attributeSet) {

    var binding : CardLayoutBinding
    var game : Game? = null
        set(value) {
            field = value
            setCard()
        }

    init {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.card_layout, this, true)
    }


    fun setCard() {
        binding.game = game
        binding.ratingBarCard.rating = game!!.rating.toFloat()
        binding.ratingBarCard.scaleY = 0.3f
        binding.ratingBarCard.scaleX = 0.3f
    }
}