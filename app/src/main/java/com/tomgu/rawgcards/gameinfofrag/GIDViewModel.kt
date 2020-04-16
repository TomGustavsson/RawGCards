package com.tomgu.rawgcards.gameinfofrag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tomgu.rawgcards.di.AppComponent
import com.tomgu.rawgcards.login.AccountRepository
import com.tomgu.rawgcards.GameRepository
import com.tomgu.rawgcards.api.CompleteGame
import javax.inject.Inject

class GIDViewModel: ViewModel(), AppComponent.Injectable {

    @Inject
    lateinit var gameRepository: GameRepository

    @Inject
    lateinit var accountRepository: AccountRepository

    private var currentAccountLiveData: MutableLiveData<Boolean> = MutableLiveData()
    fun getCurrentAccountLiveData(): LiveData<Boolean> = currentAccountLiveData

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    fun saveSharedGame(friendUid: String, game: CompleteGame) {
        //Remove game from shared game list
        accountRepository.retrieveSharedGames().document(friendUid).collection("SharedGames")
            .document(game.slug).delete()

        //save in local database
        gameRepository.insert(game)

    }

    fun getCurrentUser(){
        if(accountRepository.getCurrentUser() == null){
            currentAccountLiveData.value = false
        }
        if(accountRepository.getCurrentUser() != null){
            currentAccountLiveData.value = true
        }
    }
}

