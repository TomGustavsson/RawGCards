package com.tomgu.rawgcards.main.gameinfofrag

import android.os.Bundle
import android.transition.*
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tomgu.rawgcards.AppViewModelFactory

import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.databinding.FragmentGameInfoBinding
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.main.api.Game
import java.io.Serializable
import javax.inject.Inject


class GameInfoFragment : Fragment() {


    @Inject
    lateinit var vmFactory : AppViewModelFactory
    lateinit var viewModel : GIDViewModel

    lateinit var gameSlug: String

    lateinit var gameShare: Game

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var binding: FragmentGameInfoBinding = FragmentGameInfoBinding.inflate(LayoutInflater.from(context))
        var shareButton = binding.root.findViewById<FloatingActionButton>(R.id.shareButton)

        binding.gameInfoImage.transitionName = arguments?.getString(TRANS_NAME)
        gameSlug = arguments?.getString(SLUG_ARGUMENT)!!
        gameShare = (arguments?.getSerializable(GAME_ARGUMENT) as Game?)!!

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[GIDViewModel::class.java]

        val toolbar = binding.toolbar

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        if((activity as AppCompatActivity).supportActionBar != null){
            (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        }

        //viewModel.getRoomObject(gameSlug)
        viewModel.getApiInfo(gameSlug)

        shareButton.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(gameShare)
            bottomSheetDialog.show(fragmentManager!!, "bottomsheetDialog")
        }

        binding.game = gameShare
        /*viewModel.getLiveDataRoom().observe(this, Observer {
            gameShare = it
            binding.game = it
        })*/

        viewModel.getLiveDataApi().observe(this, Observer {
            binding.gameInf = it
        })
        return binding.root
    }

    companion object{
        private const val SLUG_ARGUMENT = "gameSlug"
        private const val TRANS_NAME = "transitionName"
        private const val GAME_ARGUMENT = "game"

        fun newInstance(slug : String, transName: String, game: Game): GameInfoFragment{

            val gameInfoFragment = GameInfoFragment()
            val arguments = Bundle()
            arguments.putString(SLUG_ARGUMENT, slug)
            arguments.putString(TRANS_NAME, transName)
            arguments.putSerializable(GAME_ARGUMENT, game)

            val transitionSet = TransitionSet()

            transitionSet.addTransition(ChangeBounds())
            transitionSet.addTransition(ChangeTransform())
            transitionSet.addTransition(ChangeImageTransform())

            gameInfoFragment.sharedElementEnterTransition = transitionSet
            gameInfoFragment.enterTransition = Fade()

            gameInfoFragment.arguments = arguments

            return gameInfoFragment
        }
    }

}
