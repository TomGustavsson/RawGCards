package com.tomgu.rawgcards.main.ui

import android.renderscript.Sampler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.di.AppComponent
import com.tomgu.rawgcards.main.GameRepository
import com.tomgu.rawgcards.main.api.Game
import com.tomgu.rawgcards.main.api.GameResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.security.Key
import javax.inject.Inject
import kotlin.math.log

class MainViewModel : ViewModel(), AppComponent.Injectable{

    private val disposables = CompositeDisposable()

    @Inject
    lateinit var gameRepository: GameRepository

    private val responseLiveData: MutableLiveData<GameResponse> = MutableLiveData<GameResponse>()
    var categorie : String = "1"

    //Keys are categories and value is page number
    var myMap : HashMap<String,Int> = hashMapOf("1" to 1, "2" to 1, "4" to 1, "5" to 1, "6" to 1)


    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    fun getApiItems() {

        disposables.add(gameRepository.getApi().getStoreObject(myMap.get(categorie)!!,categorie,"1995-01-01,2019-12-31")
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                responseLiveData.value = it

            },{
                Log.d("tgiwe", it.toString())
            }))
    }

    fun getLiveData() : MutableLiveData<GameResponse>{
        return responseLiveData
    }


    fun setSaveGameList(game : Game){
            gameRepository.insert(game)
    }

    fun setCategorieToApi(ce: String){
        when(ce){
            "Racing" -> categorie = "1"
            "Shooter" -> categorie = "2"
            "Action" -> categorie = "4"
            "RPG" -> categorie = "5"
            "Fighting" -> categorie = "6"
        }
    }

    fun setPageNumber(){
        myMap.put(categorie, myMap.get(categorie)!! + 1)
    }

    fun resetAllPages(){
        for(entry in myMap.entries){
            myMap.put(entry.key,1)
        }
        Log.d("billyz", myMap.toString())
    }

}