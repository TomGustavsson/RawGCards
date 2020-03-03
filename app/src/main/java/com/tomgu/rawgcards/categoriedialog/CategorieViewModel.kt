package com.tomgu.rawgcards.categoriedialog

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tomgu.rawgcards.di.AppComponent
import com.tomgu.rawgcards.GameRepository
import com.tomgu.rawgcards.api.GameResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CategorieViewModel: ViewModel(), AppComponent.Injectable {

    @Inject
    lateinit var gameRepository: GameRepository

    private val disposables = CompositeDisposable()
    private val responseLiveDataAction: MutableLiveData<GameResponse> = MutableLiveData<GameResponse>()
    private val responseLiveDataFighting: MutableLiveData<GameResponse> = MutableLiveData<GameResponse>()
    private val responseLiveDataRacing: MutableLiveData<GameResponse> = MutableLiveData<GameResponse>()
    private val responseLiveDataShooting: MutableLiveData<GameResponse> = MutableLiveData<GameResponse>()
    private val responseLiveDataRPG: MutableLiveData<GameResponse> = MutableLiveData<GameResponse>()
    private val isApiCallFailed: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isLoading: MutableLiveData<Boolean> = MutableLiveData(true)

    fun getAllApiItems(gameMap: HashMap<String, Int>) {
        gameMap.forEach { categorie ->
            disposables.add(gameRepository.getApi().getStoreObject(categorie.value,categorie.key,"1995-01-01,2019-12-31")
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    isApiCallFailed.value = false
                    isLoading.value = false
                    when(categorie.key){
                        "1" -> responseLiveDataRacing.value = it
                        "2" -> responseLiveDataShooting.value = it
                        "4" -> responseLiveDataAction.value = it
                        "5" -> responseLiveDataRPG.value = it
                        "6" -> responseLiveDataFighting.value = it
                    }
                },{
                    Log.d("tgiwe", it.toString())
                    isApiCallFailed.value = true
                    isLoading.value = false
                }))
        }

    }
    fun getIsLoading(): MutableLiveData<Boolean>{
        return isLoading
    }
    fun getIsApiCallFailed(): MutableLiveData<Boolean>{
        return isApiCallFailed
    }
    fun getActionLiveData(): MutableLiveData<GameResponse>{
        return responseLiveDataAction
    }
    fun getFightingLiveData(): MutableLiveData<GameResponse>{
        return responseLiveDataFighting
    }
    fun getRacingLiveData(): MutableLiveData<GameResponse>{
        return responseLiveDataRacing
    }
    fun getShootingLiveData(): MutableLiveData<GameResponse>{
        return responseLiveDataShooting
    }
    fun getRPGLiveData(): MutableLiveData<GameResponse>{
        return responseLiveDataRPG
    }

    override fun inject(appComponent: AppComponent){
        appComponent.inject(this)
    }

}