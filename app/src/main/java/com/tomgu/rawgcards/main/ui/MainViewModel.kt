package com.tomgu.rawgcards.main.ui

import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tomgu.rawgcards.di.AppComponent
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

    @Inject
    lateinit var pagePreferences: SharedPreferences

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
        saveHashMap()
    }

    fun resetAllPages(){
        for(entry in myMap.entries){
            myMap.put(entry.key,1)
        }
    }

    fun saveHashMap(){

        pagePreferences.edit().also {
            myMap.keys.forEach {key ->
                it.putInt(key, myMap[key] ?: 0)
            }
            it.apply()
        }
    }

    fun getHashMap(){

        pagePreferences.also {
            myMap.keys.forEach {key ->
                myMap.put(key, it.getInt(key, 0))
            }
        }
    }
}