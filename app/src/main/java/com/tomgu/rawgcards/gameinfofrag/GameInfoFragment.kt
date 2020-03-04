package com.tomgu.rawgcards.gameinfofrag

import android.graphics.Typeface
import android.os.Bundle
import android.transition.*
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import com.tomgu.rawgcards.AppViewModelFactory

import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.databinding.FragmentGameInfoBinding
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.api.CompleteGame
import javax.inject.Inject


class GameInfoFragment : Fragment() {

    @Inject
    lateinit var vmFactory : AppViewModelFactory
    lateinit var viewModel : GIDViewModel

    lateinit var gameSlug: String

    lateinit var gameShare: CompleteGame
    lateinit var state: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentGameInfoBinding = FragmentGameInfoBinding.inflate(LayoutInflater.from(context))
        val shareButton = binding.root.findViewById<FloatingActionButton>(R.id.shareButton)

        binding.gameInfoImage.transitionName = arguments?.getString(TRANS_NAME)
        gameSlug = arguments?.getString(SLUG_ARGUMENT)!!
        gameShare = (arguments?.getSerializable(GAME_ARGUMENT) as CompleteGame?)!!
        state = arguments?.getString(STATE_ARGUMENT)!!

        if(state == "Friend"){
            shareButton.setImageResource(R.drawable.ic_action_save)
        }

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[GIDViewModel::class.java]

        viewModel.getCurrentUser()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val toolbar = binding.toolbar

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        val typeFace: Typeface = ResourcesCompat.getFont(context!!,R.font.wallpoet)!!

        binding.collapsingToolBar.setExpandedTitleTextAppearance(R.style.customFont)
        binding.collapsingToolBar.setCollapsedTitleTypeface(typeFace)
        binding.collapsingToolBar.setExpandedTitleTypeface(typeFace)
        binding.collapsingToolBar.setCollapsedTitleTextColor(resources.getColor(R.color.headerColor))
        binding.collapsingToolBar.setExpandedTitleColor(resources.getColor(R.color.headerColor))

        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, offset ->

            val slideOffset = offset * -1f
            binding.diagonalTriangle.offset = slideOffset / appBarLayout.totalScrollRange

            if(slideOffset > appBarLayout.totalScrollRange - 200) {
                Picasso.get().load(gameShare.backgound_image_additional).into(binding.gameInfoImage)
            }
        })

        shareButton.setOnClickListener {
            if(state != "NoFriend"){
                viewModel.saveSharedGame(state, gameShare)
                Toast.makeText(context, "GAME SAVED", Toast.LENGTH_LONG).show()
            } else {
                val bottomSheetDialog = BottomSheetDialog.newInstance(gameShare,"FRIENDS", "SHARE")
                bottomSheetDialog.show(fragmentManager!!, "bottomsheetDialog")
            }
        }

        binding.completeGame = gameShare

        return binding.root
    }

    companion object{
        private const val SLUG_ARGUMENT = "gameSlug"
        private const val TRANS_NAME = "transitionName"
        private const val GAME_ARGUMENT = "game"
        private const val STATE_ARGUMENT = "state"

        fun newInstance(slug : String, transName: String, game: CompleteGame, state: String): GameInfoFragment{

            val gameInfoFragment = GameInfoFragment()
            val arguments = Bundle()
            arguments.putString(STATE_ARGUMENT, state)
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
