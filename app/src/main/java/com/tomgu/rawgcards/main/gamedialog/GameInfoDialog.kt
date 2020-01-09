package com.tomgu.rawgcards.main.gamedialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ContentView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.databinding.DialogGameInfoBinding
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.main.gamefragment.GameListViewModel
import com.tomgu.rawgcards.main.ui.MainActivity
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.dialog_game_info.*
import javax.inject.Inject

class GameInfoDialog : DialogFragment(){

    @Inject
    lateinit var vmFactory : AppViewModelFactory
    lateinit var viewModel : GIDViewModel

    lateinit var gameSlug: String


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      var binding: DialogGameInfoBinding = DialogGameInfoBinding.inflate(LayoutInflater.from(context))
      var shareButton = binding.root.findViewById<FloatingActionButton>(R.id.shareButton)

      (activity?.applicationContext as AppApplication).appComponent().inject(this)
      viewModel = ViewModelProviders.of(this, vmFactory)[GIDViewModel::class.java]

      viewModel.getRoomObject(gameSlug)
      viewModel.getApiInfo(gameSlug)

      shareButton.setOnClickListener {
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog()
          bottomSheetDialog.show(fragmentManager!!, "bottomsheetDialog")
      }
      viewModel.getLiveDataRoom().observe(this, Observer {
          binding.game = it
      })

      viewModel.getLiveDataApi().observe(this, Observer {
          binding.gameInf = it
      })
   return binding.root
  }
    fun setGameString(gs: String){
        gameSlug = gs
    }
}