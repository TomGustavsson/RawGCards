package com.tomgu.rawgcards.main.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tomgu.rawgcards.AppApplication
import com.tomgu.rawgcards.AppComponent
import com.tomgu.rawgcards.main.GameRepository
import com.tomgu.rawgcards.main.api.Game
import com.tomgu.rawgcards.main.api.GameResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel : ViewModel(), AppComponent.Injectable{

    private val disposables = CompositeDisposable()

    @Inject
    lateinit var gameRepository: GameRepository

    private val responseLiveData: MutableLiveData<GameResponse> = MutableLiveData<GameResponse>()
    private val gameLiveData : MutableLiveData<List<Game>> = MutableLiveData()


    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    fun getApiItems() {

        disposables.add(gameRepository.getApi().getStoreObject()
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                responseLiveData.value = it

            },{
                Log.d("tgiwe", it.toString())
            }))
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
    fun getLiveData() : MutableLiveData<GameResponse>{
        return responseLiveData
    }
    fun getLiveDataRoom() : MutableLiveData<List<Game>>{
        return gameLiveData
    }

    fun setSaveGameList(game : Game){
            gameRepository.insert(game)
    }

    class MainViewModelFactory (private val application: AppApplication) : ViewModelProvider.AndroidViewModelFactory(application) {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val t = super.create(modelClass)
            if (t is AppComponent.Injectable) {
                (t as AppComponent.Injectable).inject(application.appComponent())
            }
            return t
        }
    }


}