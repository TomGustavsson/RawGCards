package com.tomgu.rawgcards.main.account.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Picasso
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.login.LoginActivity
import com.tomgu.rawgcards.main.gamedialog.GIDViewModel
import javax.inject.Inject

class AccountDialog: DialogFragment() {

    @Inject
    lateinit var vmFactory : AppViewModelFactory
    lateinit var viewModel: AccountDialogViewModel

    lateinit var accountImage: de.hdodenhof.circleimageview.CircleImageView
    lateinit var emailText: TextView
    lateinit var signOutText: TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.dialog_account, container, false)

        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[AccountDialogViewModel::class.java]

        accountImage = view.findViewById(R.id.circleImageView)
        emailText = view.findViewById(R.id.emailTextView)
        signOutText = view.findViewById(R.id.signOutTextView)


        viewModel.getCurrentAccount()

        viewModel.getCurrentAccountLiveData().observe(this, Observer {
            Picasso.get().load(it.photo).resize(500,500).into(accountImage)
            emailText.setText(it.email)
        })

        signOutText.setOnClickListener {
            viewModel.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}