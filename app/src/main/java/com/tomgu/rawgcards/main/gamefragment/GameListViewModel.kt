package com.tomgu.rawgcards.main.gamefragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tomgu.rawgcards.di.AppComponent
import com.tomgu.rawgcards.main.GameRepository
import com.tomgu.rawgcards.main.api.Game
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class GameListViewModel: ViewModel(), AppComponent.Injectable {

    @Inject
    lateinit var gameRepository: GameRepository

    private val disposables = CompositeDisposable()
    private val gameLiveData : MutableLiveData<MutableList<Game>> = MutableLiveData()

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    fun getRoomItems(){
        disposables.add(gameRepository.getRoom().readGame()
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                gameLiveData.value = it
            })
    }

    fun deleteGame(game: Game){
        gameRepository.delete(game)
    }

    fun getLiveDataRoom() : MutableLiveData<MutableList<Game>>{
        return gameLiveData
    }
}