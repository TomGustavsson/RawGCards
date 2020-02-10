package com.tomgu.rawgcards.main.ui

import android.os.Bundle
import android.transition.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.Switch
import androidx.constraintlayout.widget.Constraints
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.cardstack.CardStackListener
import com.tomgu.rawgcards.cardstack.CardView
import com.tomgu.rawgcards.cardstack.MyCardStack
import com.tomgu.rawgcards.databinding.FragmentCardStackBinding
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.main.CardStackAdapter
import com.tomgu.rawgcards.main.api.Game
import com.tomgu.rawgcards.main.categoriedialog.CategorieFragment
import javax.inject.Inject


class CardStackFragment : Fragment(), CardStackListener {

    @Inject
    lateinit var vmFactory : AppViewModelFactory

    private lateinit var switchCategories : Switch
    lateinit var viewModel: MainViewModel

    lateinit var categorieName: String
    var game: Game? = null

    lateinit var myCardStack: MyCardStack

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
            viewModel.getApiItems()
            myCardStack.resetCardStack()
        }

        viewModel.getApiItems()
        viewModel.isApiFailed.observe(this, Observer {
            if(it == true){
                val snackbar = Snackbar.make(binding.cardStackBackground, "Couldn't load games", Snackbar.LENGTH_INDEFINITE)
                snackbar.setAction("Refresh", {
                    snackbar.dismiss()
                    viewModel.getApiItems()
                })
                snackbar.show()
        }
        })


        myCardStack = binding.myCardStack

        myCardStack.cardStackListener = this

        viewModel.getLiveData().observe(this, Observer {

            myCardStack.setList(it.games)

            (myCardStack as? ViewGroup)?.doOnPreDraw {
                // Parent has been drawn. Start transitioning!
                startPostponedEnterTransition()}

        })
        binding.game = game


        return binding.root
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

    override fun onRejected(game: Game) {
    }

    override fun onApproved(game: Game) {
        viewModel.setSaveGameList(game)
    }

    override fun viewCountRemain(count: Int) {
        if(count <= 2){
            viewModel.incrementCurrentPage()
            viewModel.getApiItems()
        }
    }


}


