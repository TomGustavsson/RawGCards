package com.tomgu.rawgcards

import com.tomgu.rawgcards.db.GameDao
import com.tomgu.rawgcards.api.CompleteGame
import com.tomgu.rawgcards.api.GameAPI
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class GameRepository(val gameAPI: GameAPI,
                     val gameDao: GameDao) {


    fun getApi(): GameAPI {
        return gameAPI
    }

    fun insert(game : CompleteGame){

        Completable.fromAction{
            gameDao.saveGame(game)
            }.subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun delete(game: CompleteGame){
        Completable.fromAction{
            gameDao.delete(game)
        }.subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun getRoom() : GameDao {
        return gameDao
    }
}