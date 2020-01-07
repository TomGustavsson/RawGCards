package com.tomgu.rawgcards.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.tomgu.rawgcards.main.account.Account
import com.tomgu.rawgcards.di.AppComponent
import javax.inject.Inject


class LoginViewModel: ViewModel(), AppComponent.Injectable {


    @Inject
    lateinit var accountRepository: AccountRepository

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }


    fun signInWithGoogle(googleAuthCredential: GoogleSignInAccount) {
        accountRepository.firebaseSignInWithGoogle(googleAuthCredential)

    }

    fun getLiveData() : LiveData<Account>{
        return accountRepository.authenticatedUserMutableLiveData
    }

}