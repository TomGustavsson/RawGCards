package com.tomgu.rawgcards.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.tomgu.rawgcards.di.AppComponent
import javax.inject.Inject


class LoginViewModel: ViewModel(), AppComponent.Injectable {

    @Inject
    lateinit var accountRepository: AccountRepository

    private val accountLiveData : MutableLiveData<FirebaseUser> = MutableLiveData()

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }


    fun signInWithGoogle(googleAuthCredential: GoogleSignInAccount) {
         accountRepository.firebaseSignInWithGoogle(googleAuthCredential)
    }

    fun getLiveDataRoom() : MutableLiveData<FirebaseUser>{
        return accountLiveData
    }

}