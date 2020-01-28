package com.tomgu.rawgcards.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.tomgu.rawgcards.main.account.Account
import com.tomgu.rawgcards.di.AppComponent
import javax.inject.Inject


class LoginViewModel: ViewModel(), AppComponent.Injectable {


    @Inject
    lateinit var accountRepository: AccountRepository
    private val authenticatedUserMutableLiveData: MutableLiveData<Account> = MutableLiveData()

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }


    fun signInWithGoogle(googleAuthCredential: GoogleSignInAccount) {
        accountRepository.firebaseSignInWithGoogle(googleAuthCredential){
            authenticatedUserMutableLiveData.value = it
        }

    }

    fun getLiveData() : LiveData<Account>{
        return authenticatedUserMutableLiveData
    }


}