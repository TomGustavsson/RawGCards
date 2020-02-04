package com.tomgu.rawgcards.main.ui

import android.os.Bundle
import android.transition.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.databinding.FragmentCardStackBinding
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.main.CardStackAdapter
import com.tomgu.rawgcards.main.api.Game
import com.tomgu.rawgcards.main.categoriedialog.CategorieFragment
import com.wenchao.cardstack.CardStack
import javax.inject.Inject


class CardStackFragment : Fragment(), CardStack.CardEventListener {

    private var card_stack: CardStack? = null
    private var card_adapter: CardStackAdapter? = null

    @Inject
    lateinit var vmFactory : AppViewModelFactory

    private lateinit var switchCategories : Switch
    lateinit var viewModel: MainViewModel

    lateinit var categorieName: String
    var cardIndex : Int = 0
    var game: Game? = null

    private lateinit var categorieFragment: Fragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentCardStackBinding = FragmentCardStackBinding.inflate(LayoutInflater.from(context))
        //Dagger2 skit
        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[MainViewModel::class.java]

        val reverseFab : FloatingActionButton = binding.reverseFab

        viewModel.getHashMapFromPreferences()
        categorieName = arguments!!.getString("Categorie")!!
        viewModel.setCategorieToApi(categorieName)

        if(arguments!!.getSerializable("Game") != null)
        game = arguments!!.getSerializable("Game") as Game


        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        postponeEnterTransition()

        switchCategories = binding.switchCategories

        //Open categorie fragment
        val com = object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if(p1){
                    categorieFragment = CategorieFragment.newInstance(viewModel.getHashMap())
                    activity!!.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, categorieFragment)
                        .addToBackStack(null)
                        .commit()
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

        card_stack = binding.cardStack
        card_stack!!.setContentResource(R.layout.card_layout)
        card_stack!!.setStackMargin(20)
        card_stack!!.setAdapter(card_adapter!!)

        card_stack!!.setListener(this)

        viewModel.getApiItems()

        viewModel.getLiveData().observe(this, Observer {

            it.games.forEach {
                card_adapter!!.add(it)
            }

            (card_stack as? ViewGroup)?.doOnPreDraw {
                // Parent has been drawn. Start transitioning!
                startPostponedEnterTransition()}

        })

        binding.game = game


        return binding.root
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

    companion object{

        fun newInstance(categorie: String, transName: String, game: Game): CardStackFragment{
            val cardStackFragment = CardStackFragment()

            val arguments = Bundle()
            arguments.putString("Categorie", categorie)
            arguments.putString("Trans", transName)
            arguments.putSerializable("Game", game)

            val transitionSet = TransitionSet()

            transitionSet.addTransition(ChangeBounds())
            transitionSet.addTransition(ChangeTransform())
            transitionSet.addTransition(ChangeImageTransform())

            cardStackFragment.sharedElementEnterTransition = transitionSet

            cardStackFragment.arguments = arguments

            return cardStackFragment
        }
    }

}


