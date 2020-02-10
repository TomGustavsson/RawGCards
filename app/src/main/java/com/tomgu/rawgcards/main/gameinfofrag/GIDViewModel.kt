package com.tomgu.rawgcards.main.gameinfofrag

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tomgu.rawgcards.di.AppComponent
import com.tomgu.rawgcards.login.AccountRepository
import com.tomgu.rawgcards.main.GameRepository
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
    private val gameLiveData : MutableLiveData<Game> = MutableLiveData()

    //Live data for API
    private val gameInfoLiveData : MutableLiveData<GameInfo> = MutableLiveData()

    val isApiError : MutableLiveData<Boolean> = MutableLiveData(false)

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    fun getApiInfo(gameSlug: String) {
        disposables.add(gameRepository.getApi().getGameObject(gameSlug)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                gameInfoLiveData.value = it
                isApiError.value = false

            },{
                Log.d("tgiwe", it.toString())
                isApiError.value = true
            }))
    }

    fun getRoomObject(gameSlug: String){
        disposables.add(gameRepository.getRoom().loadSingle(gameSlug)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{result -> gameLiveData.value = result})
    }

    fun getLiveDataRoom() : MutableLiveData<Game>{
        return gameLiveData
    }

    fun getLiveDataApi() : MutableLiveData<GameInfo>{
        return gameInfoLiveData
    }

    fun saveSharedGame(friendUid: String, game: Game){
        //Remove game from shared game list
        accountRepository.retrieveSharedGames().document(friendUid).collection("SharedGames").document(game.slug).delete()

        //save in local database
        gameRepository.insert(game)

        }


    }