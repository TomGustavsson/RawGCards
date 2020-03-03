package com.tomgu.rawgcards.main.gameinfofrag

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tomgu.rawgcards.di.AppComponent
import com.tomgu.rawgcards.login.AccountRepository
import com.tomgu.rawgcards.main.GameRepository
import com.tomgu.rawgcards.main.api.CompleteGame
import com.tomgu.rawgcards.main.api.Game
import com.tomgu.rawgcards.main.api.GameInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class GIDViewModel: ViewModel(), AppComponent.Injectable {

    @Inject
    lateinit var gameRepository: GameRepository

    @Inject
    lateinit var accountRepository: AccountRepository

    private val disposables = CompositeDisposable()

    //Live data for room
    private val gameLiveData : MutableLiveData<CompleteGame> = MutableLiveData()

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    fun saveSharedGame(friendUid: String, game: CompleteGame){
        //Remove game from shared game list
        accountRepository.retrieveSharedGames().document(friendUid).collection("SharedGames").document(game.slug).delete()

        //save in local database
        gameRepository.insert(game)

        }


    }