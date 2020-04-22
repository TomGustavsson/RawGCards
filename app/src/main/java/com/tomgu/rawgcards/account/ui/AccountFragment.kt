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

        viewModel.getCurrentAccount()
        viewModel.getUsers(FriendState.REQUEST)


        viewModel.isApiFailed().observe(viewLifecycleOwner, Observer {
            if(it == true){
                val snackbar = Snackbar.make(binding.accountBackground, "Couldn't load information", Snackbar.LENGTH_LONG)
                snackbar.setAction("CLOSE") {
                    snackbar.dismiss()
                }
                snackbar.show()
            }
        })

        viewModel.getCurrentAccountLiveData().observe(viewLifecycleOwner, Observer {
            binding.account = it
        })

        binding.usersFab.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog.newInstance(null,FriendState.UNKNOWN, "NOSHARE")
            bottomSheetDialog.show(fragmentManager!!, "bottomsheetDialog")
        }

        binding.friendsFab.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog.newInstance(null,FriendState.FRIEND, "NOSHARE")
            bottomSheetDialog.show(fragmentManager!!, "bottomsheetDialog")
        }

        binding.signOutFab.setOnClickListener {
            viewModel.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.requestsFab.setOnClickListener {
            bottomSheetDialog = BottomSheetDialog.newInstance(null, FriendState.REQUEST, "NOSHARE")
            bottomSheetDialog.show(fragmentManager!!, " bottomsheetDialog")
        }

        return binding.root
    }
}


