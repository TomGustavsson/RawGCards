package com.tomgu.rawgcards.gamefragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tomgu.rawgcards.di.AppComponent
import com.tomgu.rawgcards.GameRepository
import com.tomgu.rawgcards.api.CompleteGame
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class GameListViewModel: ViewModel(), AppComponent.Injectable {

    @Inject
    lateinit var gameRepository: GameRepository

    private val gameLiveData : MutableLiveData<MutableList<CompleteGame>> = MutableLiveData()
    private var roomDisposable: Disposable? = null

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    fun getRoomItems(){
        roomDisposable?.dispose()
        roomDisposable = gameRepository.getRoom().readGame()
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                if(gameLiveData.value != it)
                gameLiveData.value = it
            }
    }

    fun deleteGame(game: CompleteGame){
        gameRepository.delete(game)
    }

    fun getLiveDataRoom() : MutableLiveData<MutableList<CompleteGame>>{
        return gameLiveData
    }
}