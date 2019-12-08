package com.tomgu.rawgcards.main.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.main.CardStackAdapter
import com.tomgu.rawgcards.main.MainViewModel
import com.wenchao.cardstack.CardStack
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), CardStack.CardEventListener {

    private var card_stack: CardStack? = null
    private var card_adapter: CardStackAdapter? = null

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottom)

        card_adapter = CardStackAdapter(applicationContext, 0)

        card_stack = findViewById(R.id.card_stack)
        card_stack!!.setContentResource(R.layout.card_layout)
        card_stack!!.setStackMargin(20)
        card_stack!!.setAdapter(card_adapter!!)

        card_stack!!.setListener(this)

        viewModel = MainViewModel()
        viewModel.getApiItems()

        viewModel.getLiveData().observe(this, Observer {
            for(game in it.games)
            card_adapter!!.add(game)
            Log.d("tgiw", it.toString())
        })

    }

    override fun swipeEnd(i: Int, v: Float): Boolean {

        if (i == 1 || i == 3){
            //Save game in room database
        } else {
            //remove from room database
        }

        return v > 300
    }

    override fun swipeStart(i: Int, v: Float): Boolean {
        return false
    }

    override fun swipeContinue(i: Int, v: Float, v1: Float): Boolean {
        return false
    }

    override fun discarded(i: Int, i1: Int) {

    }

    override fun topCardTapped() {

    }
}
