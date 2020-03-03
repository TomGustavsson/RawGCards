package com.tomgu.rawgcards.main.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.login.LoginFragment
import com.tomgu.rawgcards.account.ui.AccountFragment
import com.tomgu.rawgcards.gamefragment.GameListFragment
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    lateinit var favouritesFragment : GameListFragment

    @Inject
    lateinit var vmFactory : AppViewModelFactory


    lateinit var viewModel: MainViewModel

    private lateinit var cardStackFragment: CardStackFragment
    private lateinit var accountFragment: Fragment
    private lateinit var loginFragment: LoginFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Dagger2 skit
        (applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[MainViewModel::class.java]

        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottom_nav_bar)

        favouritesFragment = GameListFragment()
        cardStackFragment = CardStackFragment()
        accountFragment = AccountFragment()
        loginFragment = LoginFragment()

        val defaultArgument = Bundle()
        defaultArgument.putString("Categorie", "Racing")
        defaultArgument.putString("TransitionName", "image_content")
        cardStackFragment.arguments = defaultArgument

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, cardStackFragment)
            .addToBackStack("cardStackFragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()


        bottomNavigationView.setOnNavigationItemSelectedListener {

            when(it.itemId){

                R.id.account -> {
                    if(intent.getStringExtra("account") == "guest"){
                       supportFragmentManager
                           .beginTransaction()
                           .replace(R.id.frame_layout, loginFragment)
                           .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                           .addToBackStack(null)
                           .commit()
                    }else {
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.frame_layout, accountFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(null)
                            .commit()
                    }
                }
                R.id.home -> {
                    if (supportFragmentManager.findFragmentById(R.id.frame_layout) != null){
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.frame_layout, cardStackFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                            .addToBackStack("cardStackFragment")
                            .commit()
                    }

                }
                R.id.favourites -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, favouritesFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit()
                }

            }
            true
        }

    }

}
