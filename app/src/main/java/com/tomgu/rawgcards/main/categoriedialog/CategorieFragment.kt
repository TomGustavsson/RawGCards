package com.tomgu.rawgcards.main.categoriedialog

import android.os.Bundle
import android.transition.Explode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.databinding.FragmentCategorieBinding
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.main.api.Game
import com.tomgu.rawgcards.main.ui.CardStackFragment
import kotlinx.android.synthetic.main.card_layout.view.*
import javax.inject.Inject

class CategorieFragment : Fragment() {

    @Inject
    lateinit var vmFactory: AppViewModelFactory

    lateinit var viewModel: CategorieViewModel
    lateinit var gameHashMap: HashMap<String, Int>
    lateinit var cardStackFragment: CardStackFragment
    lateinit var actionGame: Game

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentCategorieBinding = FragmentCategorieBinding.inflate(LayoutInflater.from(context))

        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[CategorieViewModel::class.java]

        exitTransition = Explode()


        gameHashMap = arguments?.getSerializable(MAP_ARGUMENT) as HashMap<String, Int>
        viewModel.getAllApiItems(gameHashMap)

        viewModel.getActionLiveData().observe(this, Observer {
            binding.categorieAction.game = it.games[0]
            actionGame = it.games[0]

        })

        viewModel.getFightingLiveData().observe(this, Observer {
            binding.categorieFighting.game = it.games[0]
        })

        viewModel.getRacingLiveData().observe(this, Observer {
            binding.categorieRacing.game = it.games[0]
        })

        viewModel.getShootingLiveData().observe(this, Observer {
            binding.categorieShooting.game = it.games[0]
        })

        viewModel.getRPGLiveData().observe(this, Observer {
            binding.categorieRPG.game = it.games[0]
        })

        binding.categorieAction.setOnClickListener{
            val image = it.findViewById<ImageView>(R.id.image_content)
            image.transitionName = "image_trans"
            createFragment("Action",image)

        }
        binding.categorieFighting.setOnClickListener {
            val image = it.findViewById<ImageView>(R.id.image_content)
            image.transitionName = "image_trans"
            createFragment("Fighting", image)
        }
        binding.categorieRacing.setOnClickListener {
            val image = it.findViewById<ImageView>(R.id.image_content)
            image.transitionName = "image_trans"
            createFragment("Racing",image)
        }

        binding.categorieShooting.setOnClickListener {
            val image = it.findViewById<ImageView>(R.id.image_content)
            image.transitionName = "image_trans"
            createFragment("Shooter",image)
        }

        binding.categorieRPG.setOnClickListener {
            val image = it.findViewById<ImageView>(R.id.image_content)
            image.transitionName = "image_trans"
            createFragment("RPG",image)
        }

        return binding.root
    }

    companion object {
        private const val MAP_ARGUMENT = "map"

        fun newInstance(map : HashMap<String, Int>) : CategorieFragment{

            val categorieFragment = CategorieFragment()
            val arguments = Bundle()
            arguments.putSerializable(MAP_ARGUMENT, map)

            categorieFragment.arguments = arguments

            return categorieFragment
        }
    }

    fun createFragment(categorie: String, image: ImageView){

        cardStackFragment = CardStackFragment.newInstance(categorie, image.transitionName, actionGame)

        activity!!.supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .addSharedElement(image, image.transitionName)
            .replace(R.id.frame_layout, cardStackFragment)
            .addToBackStack(null)
            .commit()
    }
}
