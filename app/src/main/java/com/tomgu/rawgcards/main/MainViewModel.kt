package com.tomgu.rawgcards.main

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tomgu.rawgcards.db.RoomRepository
import com.tomgu.rawgcards.main.api.Game
import com.tomgu.rawgcards.main.api.GameResponse
import com.tomgu.rawgcards.main.api.RetrofitRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(application: Application) : ViewModel(){

    private val disposables = CompositeDisposable()
    var repositoryRetroFit: RetrofitRepository = RetrofitRepository
    private val responseLiveData: MutableLiveData<GameResponse> = MutableLiveData<GameResponse>()
    var roomRepository : RoomRepository = RoomRepository(application)

    var gameList = mutableListOf<Game>()

    fun getApiItems() {

        disposables.add(repositoryRetroFit.getApi().getStoreObject()
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                responseLiveData.value = it

            },{
                Log.d("tgiw", it.toString())
            }))

    }
    fun getLiveData() : MutableLiveData<GameResponse>{
        return responseLiveData
    }

    fun setSaveGameList(game : Game){
        roomRepository.insert(game)
    }

}