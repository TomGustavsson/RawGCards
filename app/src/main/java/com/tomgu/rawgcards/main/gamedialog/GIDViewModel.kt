package com.tomgu.rawgcards.main.gamedialog

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ReportFragment
import androidx.lifecycle.ViewModel
import com.tomgu.rawgcards.di.AppComponent
import com.tomgu.rawgcards.main.GameRepository
import com.tomgu.rawgcards.main.api.Game
import com.tomgu.rawgcards.main.api.GameInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class GIDViewModel: ViewModel(), AppComponent.Injectable {

    @Inject
    lateinit var gameRepository: GameRepository

    private val disposables = CompositeDisposable()

    //Live data for room
    private val gameLiveData : MutableLiveData<Game> = MutableLiveData()

    //Live data for API
    private val gameInfoLiveData : MutableLiveData<GameInfo> = MutableLiveData()

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

            },{
                Log.d("tgiwe", it.toString())
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

}