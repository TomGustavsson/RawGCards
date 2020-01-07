package com.tomgu.rawgcards.main.account.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tomgu.rawgcards.di.AppComponent
import com.tomgu.rawgcards.login.AccountRepository
import com.tomgu.rawgcards.main.account.Account
import javax.inject.Inject

class AccountDialogViewModel: ViewModel(), AppComponent.Injectable {

    @Inject
    lateinit var accountRepository: AccountRepository

     override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    fun getCurrentAccount(){
        accountRepository.getCurrentAccount()
    }

    fun getCurrentAccountLiveData() : LiveData<Account> {
        return accountRepository.currentAccountMutableLiveData
    }

    fun retrieveFriends(): LiveData<MutableList<Account>> {
        accountRepository.retrieveFriends()
        return accountRepository.allFriendsMutableLiveData
    }


    fun signOut(){
        accountRepository.signOut()
    }
}