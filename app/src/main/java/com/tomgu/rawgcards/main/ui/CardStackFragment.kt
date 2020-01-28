package com.tomgu.rawgcards.main.ui

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.main.CardStackAdapter
import com.tomgu.rawgcards.main.categoriedialog.Categorie
import com.tomgu.rawgcards.main.categoriedialog.DialogCategories
import com.wenchao.cardstack.CardStack
import javax.inject.Inject


class CardStackFragment : Fragment(), CardStack.CardEventListener {

    private var card_stack: CardStack? = null
    private var card_adapter: CardStackAdapter? = null

    @Inject
    lateinit var vmFactory : AppViewModelFactory

    private lateinit var switchCategories : Switch
    lateinit var viewModel: MainViewModel

    private lateinit var dialogCategories: DialogCategories
    val DATEPICKER_FRAGMENT = 1

    var cardIndex : Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_card_stack, container, false)
        //Dagger2 skit
        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[MainViewModel::class.java]

        val reverseFab : FloatingActionButton = view.findViewById(R.id.reverseFab)

        viewModel.getHashMapFromPreferences()

        switchCategories = view.findViewById(R.id.switchCategories)

        //Open dialog with switch object
        val com = object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if(p1){
                    dialogCategories = DialogCategories()
                    dialogCategories.setTargetFragment(this@CardStackFragment, DATEPICKER_FRAGMENT)
                    dialogCategories.show(fragmentManager!!,"gameInfoDialog")
                } else{
                    Log.d("switcher", "Switch is disabled")
                }
            }
        }

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

        card_adapter = CardStackAdapter(context!!, 0)

        card_stack = view.findViewById(R.id.card_stack)
        card_stack!!.setContentResource(R.layout.card_layout)
        card_stack!!.setStackMargin(20)
        card_stack!!.setAdapter(card_adapter!!)

        card_stack!!.setListener(this)

        viewModel.getApiItems()

        viewModel.getLiveData().observe(this, Observer {

            it.games.forEach {
                card_adapter!!.add(it)
            }
        })


        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            DATEPICKER_FRAGMENT -> if(resultCode == Activity.RESULT_OK){

                val bundle: Bundle = data!!.extras!!
                val categorieName = bundle.getString("categorie")
                changeSwitch(categorieName!!)
            }
        }

    }

    override fun swipeEnd(i: Int, v: Float): Boolean {

        if (i == 1 || i == 3){
            viewModel.setSaveGameList(card_adapter!!.getItem(card_stack!!.currIndex)!!)
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

    fun changeSwitch(categorieName : String){
        switchToggle()
        card_adapter!!.clear()
        viewModel.setCategorieToApi(categorieName)
        viewModel.getApiItems()
        card_stack!!.reset(true)
        cardIndex = 0
    }

    fun switchToggle(){
        switchCategories.toggle()
    }
}

