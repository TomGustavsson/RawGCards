package com.tomgu.rawgcards.main.ui

import android.accounts.Account
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import androidx.fragment.app.FragmentTransaction
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.main.CardStackAdapter
import com.wenchao.cardstack.CardStack
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.main.account.ui.AccountDialog
import com.tomgu.rawgcards.main.categoriedialog.Categorie
import com.tomgu.rawgcards.main.categoriedialog.DialogCategories
import com.tomgu.rawgcards.main.gamefragment.GameListFragment
import javax.inject.Inject

class MainActivity : AppCompatActivity(), CardStack.CardEventListener {

    private var card_stack: CardStack? = null
    private var card_adapter: CardStackAdapter? = null

    lateinit var favouritesFragment : GameListFragment

    @Inject
    lateinit var vmFactory : AppViewModelFactory

    lateinit var switchCategories : Switch
    lateinit var viewModel: MainViewModel

    lateinit var dialogCategories: DialogCategories
    lateinit var accountDialog: AccountDialog

    var cardIndex : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Dagger2 skit
        (applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[MainViewModel::class.java]

        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottom_nav_bar)
        val reverseFab : FloatingActionButton = findViewById(R.id.reverseFab)
        val accountImage: ImageView = findViewById(R.id.accountImage)

        viewModel.getHashMapFromPreferences()

        switchCategories = findViewById(R.id.switchCategories)
        favouritesFragment = GameListFragment()
        dialogCategories = DialogCategories()
        accountDialog = AccountDialog()

        //Open dialog with switch object
        val com = object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if(p1){
                    dialogCategories.show(supportFragmentManager,"gameInfoDialog")
                } else{
                    Log.d("switcher", "Switch is disabled")
                }
            }
        }

        viewModel.getCurrentAccount()

       viewModel.getCurrentAccountLiveData().observe(this, Observer {
            Picasso.get().load(it.photo).resize(500,500).into(accountImage)
        })

        switchCategories.setOnCheckedChangeListener(com)

        //Reverse Button
        reverseFab.setOnClickListener {
            reverseFab.animate().rotation(reverseFab.getRotation()-360).start()
            viewModel.resetAllPages()
            card_adapter!!.clear()
            viewModel.getApiItems()
            card_stack!!.reset(true)
            cardIndex = 0
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {

            when(it.itemId){

                R.id.account -> {
                    accountDialog.show(supportFragmentManager, "accountDialog")
                }
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
        })


    }


    override fun swipeEnd(i: Int, v: Float): Boolean {

        if (i == 1 || i == 3){
            viewModel.setSaveGameList(card_adapter!!.getItem(card_stack!!.currIndex))
            cardIndex ++
        } else {
            cardIndex ++
        }
        if(cardIndex >= 18){
            viewModel.incrementCurrentPage()
            viewModel.getApiItems()
            cardIndex = 0
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

    fun changeSwitch(categorie: Categorie){
        switchToggle()
        card_adapter!!.clear()
        viewModel.setCategorieToApi(categorie.name)
        viewModel.getApiItems()
        card_stack!!.reset(true)
        cardIndex = 0
    }

    fun switchToggle(){
        switchCategories.toggle()
    }
}
