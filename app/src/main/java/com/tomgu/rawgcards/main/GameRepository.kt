package com.tomgu.rawgcards.main

import android.app.Application
import com.tomgu.rawgcards.db.AppDB
import com.tomgu.rawgcards.db.GameDao
import com.tomgu.rawgcards.main.api.Game
import com.tomgu.rawgcards.main.api.GameAPI
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject


class GameRepository(val gameAPI: GameAPI,
                     val gameDao: GameDao) {


    fun getApi(): GameAPI {
        return gameAPI
    }

    fun insert(game : Game){
        Completable.fromAction{
            gameDao.saveGame(game)
            }.subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun delete(game: Game){
        Completable.fromAction{
            gameDao.delete(game)
        }.subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun getRoom() : GameDao {
        return gameDao
    }
}