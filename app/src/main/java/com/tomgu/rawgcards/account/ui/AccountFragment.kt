package com.tomgu.rawgcards.account.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.tomgu.rawgcards.AppViewModelFactory

import com.tomgu.rawgcards.databinding.FragmentAccountBinding
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.login.LoginActivity
import com.tomgu.rawgcards.gameinfofrag.BottomSheetDialog
import javax.inject.Inject

class AccountFragment : Fragment() {

    @Inject
    lateinit var vmFactory: AppViewModelFactory

    lateinit var viewModel: AccountDialogViewModel
    lateinit var bottomSheetDialog: BottomSheetDialog


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentAccountBinding = FragmentAccountBinding.inflate(LayoutInflater.from(context))

        binding.lifecycleOwner = viewLifecycleOwner
        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[AccountDialogViewModel::class.java]

        binding.viewModel = viewModel
        //binding.usersTextView.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        //binding.textView.paintFlags = Paint.UNDERLINE_TEXT_FLAG

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
            //binding.requestCount.setText(it.friendRequests!!.size.toString())
        })
        viewModel.getFriends()
        viewModel.getFriendsLiveData().observe(this, Observer {
            //binding.friendsCount.setText(it.size.toString())
        })
        viewModel.getAllUsers()
        viewModel.getUsersLiveData().observe(this, Observer {
            //binding.usersCount.setText(it.size.toString())
        })


        binding.usersFab.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog.newInstance(null,"USERS", "NOSHARE")
            bottomSheetDialog.show(fragmentManager!!, "bottomsheetDialog")
        }

        binding.friendsFab.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog.newInstance(null,"FRIENDS", "NOSHARE")
            bottomSheetDialog.show(fragmentManager!!, "bottomsheetDialog")
        }

        binding.signOutFab.setOnClickListener {
            viewModel.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.requestsFab.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog.newInstance(null, "REQUESTS", "NOSHARE")
            bottomSheetDialog.show(fragmentManager!!, " bottomsheetDialog")
        }

        return binding.root
    }
}


