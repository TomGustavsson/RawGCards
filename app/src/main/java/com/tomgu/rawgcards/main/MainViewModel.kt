package com.tomgu.rawgcards.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tomgu.rawgcards.main.api.GameResponse
import com.tomgu.rawgcards.main.api.RetrofitRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel(){

    private val disposables = CompositeDisposable()
    var repositoryRetroFit: RetrofitRepository = RetrofitRepository
    private val responseLiveData: MutableLiveData<GameResponse> = MutableLiveData<GameResponse>()

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



}