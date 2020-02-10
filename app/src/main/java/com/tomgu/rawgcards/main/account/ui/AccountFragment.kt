package com.tomgu.rawgcards.main.account.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.MyBaseDiffUtil

import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.databinding.FragmentAccountBinding
import com.tomgu.rawgcards.databinding.FriendListItemBinding
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.login.LoginActivity
import com.tomgu.rawgcards.main.MyBaseAdapter
import com.tomgu.rawgcards.main.account.Account
import com.tomgu.rawgcards.main.gameinfofrag.BottomSheetDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AccountFragment : Fragment() {

    @Inject
    lateinit var vmFactory: AppViewModelFactory

    lateinit var viewModel: AccountDialogViewModel
    lateinit var bottomSheetDialog: BottomSheetDialog


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentAccountBinding = FragmentAccountBinding.inflate(LayoutInflater.from(context))

        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[AccountDialogViewModel::class.java]

        binding.emailTextView.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.usersTextView.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.textView.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        viewModel.getCurrentAccount()

        viewModel.isApiFailed.observe(this, Observer {
            if(it == true){
                val snackbar = Snackbar.make(binding.accountBackground, "Couldn't load information", Snackbar.LENGTH_INDEFINITE)
                snackbar.setAction("CLOSE", {
                    snackbar.dismiss()
                })
                snackbar.show()
            }
        })

        viewModel.getCurrentAccountLiveData().observe(this, Observer {
            binding.account = it
            binding.requestCount.setText(it.friendRequests!!.size.toString())
        })
        viewModel.getFriends()
        viewModel.getFriendsLiveData().observe(this, Observer {
            binding.friendsCount.setText(it.size.toString())
        })
        viewModel.getAllUsers()
        viewModel.getUsersLiveData().observe(this, Observer {
            binding.usersCount.setText(it.size.toString())
        })


        binding.usersTextView.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog.newInstance(null,"USERS", "NOSHARE")
            bottomSheetDialog.show(fragmentManager!!, "bottomsheetDialog")
        }

        binding.friendsTextView.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog.newInstance(null,"FRIENDS", "NOSHARE")
            bottomSheetDialog.show(fragmentManager!!, "bottomsheetDialog")
        }

        binding.signOutButton.setOnClickListener {
            viewModel.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.requestText.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog.newInstance(null, "REQUESTS", "NOSHARE")
            bottomSheetDialog.show(fragmentManager!!, " bottomsheetDialog")
        }

        ObjectAnimator.ofInt(binding.progressBarGames, "progress", 50)
            .setDuration(3000)
            .start()

        return binding.root
    }
}


