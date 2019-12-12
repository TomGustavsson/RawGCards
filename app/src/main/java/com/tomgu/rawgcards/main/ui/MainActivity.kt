package com.tomgu.rawgcards.main.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentTransaction
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.main.CardStackAdapter
import com.wenchao.cardstack.CardStack
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.main.gamefragment.GameListFragment
import javax.inject.Inject

class MainActivity : AppCompatActivity(), CardStack.CardEventListener {

    private var card_stack: CardStack? = null
    private var card_adapter: CardStackAdapter? = null

    lateinit var favouritesFragment : GameListFragment

    @Inject
    lateinit var vmFactory : AppViewModelFactory

    lateinit var viewModel: MainViewModel

    var cardIndex : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        //Dagger2 skit
        (applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[MainViewModel::class.java]

        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottom_nav_bar)
        favouritesFragment =
            GameListFragment()

        bottomNavigationView.setOnNavigationItemSelectedListener {

            when(it.itemId){
                R.id.home -> {
                    supportFragmentManager
                        .beginTransaction()
                        .remove(favouritesFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .commit()
                }
                R.id.favourites -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, favouritesFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

            }
            true
        }

        card_adapter = CardStackAdapter(applicationContext, 0)

        card_stack = findViewById(R.id.card_stack)
        card_stack!!.setContentResource(R.layout.card_layout)
        card_stack!!.setStackMargin(20)
        card_stack!!.setAdapter(card_adapter!!)

        card_stack!!.setListener(this)

        viewModel.getApiItems()

        viewModel.getLiveData().observe(this, Observer {
            for(game in it.games)
            card_adapter!!.add(game)
            Log.d("tgiw", it.toString())
        })

    }

    override fun swipeEnd(i: Int, v: Float): Boolean {

        if (i == 1 || i == 3){
            viewModel.setSaveGameList(card_adapter!!.getItem(cardIndex))
            cardIndex ++
        } else {
            cardIndex ++
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
