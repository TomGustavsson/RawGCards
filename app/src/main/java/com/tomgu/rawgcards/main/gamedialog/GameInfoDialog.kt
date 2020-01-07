package com.tomgu.rawgcards.main.gamedialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.main.gamefragment.GameListViewModel
import com.tomgu.rawgcards.main.ui.MainActivity
import kotlinx.android.synthetic.*
import javax.inject.Inject

class GameInfoDialog : DialogFragment(){

    @Inject
    lateinit var vmFactory : AppViewModelFactory
    lateinit var viewModel : GIDViewModel

    lateinit var gameSlug: String


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    var view: View = inflater.inflate(R.layout.dialog_game_info, container, false)

   var gameInfoImage = view.findViewById<ImageView>(R.id.gameInfoImage)
   var infoTitleText = view.findViewById<TextView>(R.id.infoTitleText)
   var infoRatingText = view.findViewById<TextView>(R.id.infoRatingText)
   var descriptionScrollView = view.findViewById<TextView>(R.id.descriptionScrollView)
   var extraInfoImage = view.findViewById<ImageView>(R.id.extraInfoImage)
    var shareButton = view.findViewById<FloatingActionButton>(R.id.shareButton)

      (activity?.applicationContext as AppApplication).appComponent().inject(this)
      viewModel = ViewModelProviders.of(this, vmFactory)[GIDViewModel::class.java]

      viewModel.getRoomObject(gameSlug)
      viewModel.getApiInfo(gameSlug)

      shareButton.setOnClickListener {
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog()
          bottomSheetDialog.show(fragmentManager!!, "bottomsheetDialog")
      }
      viewModel.getLiveDataRoom().observe(this, Observer {
          Picasso.get().load(it.background_image).into(gameInfoImage)
          infoTitleText.setText(it.name)
          infoRatingText.setText(it.rating)
      })

      viewModel.getLiveDataApi().observe(this, Observer {
          Picasso.get().load(it.background_image_additional).into(extraInfoImage)
          descriptionScrollView.setText(it.description)
      })

   return view
  }

    fun setGameString(gs: String){
        gameSlug = gs
    }
}