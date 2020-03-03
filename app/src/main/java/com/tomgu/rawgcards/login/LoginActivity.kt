package com.tomgu.rawgcards.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.di.AppApplication
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var vmFactory : AppViewModelFactory
    lateinit var viewModel: LoginViewModel

    lateinit var loginFragment: LoginFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginFragment = LoginFragment()

        //Dagger2 skit
        (applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[LoginViewModel::class.java]

        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, loginFragment)
            .addToBackStack("loginFragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

    }
}
