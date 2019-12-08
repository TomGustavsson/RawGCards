package com.tomgu.rawgcards.db

import android.app.Application
import androidx.lifecycle.LiveData
import com.tomgu.rawgcards.main.api.Game

class RoomRepository(application: Application) {

    var appDB: AppDB = AppDB.getInstance(application)
    private var gameDao : GameDao = appDB.gameDao()
    private var allGames : LiveData<List<Game>> = gameDao.readGame()

    fun insert(game : Game){
        gameDao.saveGame(game)
    }

    fun getAllGames() : LiveData<List<Game>> {
        return allGames
    }

}
