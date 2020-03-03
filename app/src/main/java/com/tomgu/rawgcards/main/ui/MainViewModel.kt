package com.tomgu.rawgcards.main.ui

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tomgu.rawgcards.di.AppComponent
import com.tomgu.rawgcards.login.AccountRepository
import com.tomgu.rawgcards.GameRepository
import com.tomgu.rawgcards.api.CompleteGame
import com.tomgu.rawgcards.api.Game
import com.tomgu.rawgcards.api.GameResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class MainViewModel : ViewModel(), AppComponent.Injectable{

    private val disposables = CompositeDisposable()

    @Inject
    lateinit var gameRepository: GameRepository

    @Inject
    lateinit var accountRepository: AccountRepository

    @Inject
    lateinit var pagePreferences: SharedPreferences

    val isApiFailed: MutableLiveData<Boolean> = MutableLiveData(false)

    private val responseLiveData: MutableLiveData<GameResponse> = MutableLiveData<GameResponse>()

    var categorie : String = "1"
        get() = field
        private set(value) {
            field = value
        }

    //Keys are categories and value is page number
    private val myMap : HashMap<String,Int> = hashMapOf("1" to 1, "2" to 1, "4" to 1, "5" to 1, "6" to 1)


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
                isApiFailed.value = false

            },{
                Log.d("tgiwe", it.toString())
                isApiFailed.value = true
            }))

    }

    fun getLiveData() : MutableLiveData<GameResponse>{
        return responseLiveData
    }


    @SuppressLint("CheckResult")
    fun setSaveGameList(game : Game){
        gameRepository.getApi().getGameObject(game.slug)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                gameRepository.insert(CompleteGame(game.slug,game.name,game.rating,game.background_image,it.description,it.background_image_additional))
            }, {
                Log.d("TGIW", it.toString())
            })
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

    fun incrementCurrentPage(){
        myMap.put(categorie, myMap.get(categorie)!! + 1)
        saveHashMap()
    }

    fun resetAllPages(){
        for(entry in myMap.entries){
            myMap.put(entry.key,1)
        }
        saveHashMap()
    }

    private fun saveHashMap(){

        pagePreferences.edit().also {
            myMap.keys.forEach {key ->
                it.putInt(key, myMap[key] ?: 0)
            }
            it.apply()
        }
    }

    fun getHashMapFromPreferences(){

        pagePreferences.also {
            myMap.keys.forEach {key ->
                myMap.put(key, it.getInt(key, 1))
            }
        }
    }

    fun getHashMap(): HashMap<String,Int>{
        return myMap
    }

    fun getActionKey(): Int{
        return myMap.get("4")!!
    }

}