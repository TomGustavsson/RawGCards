package com.tomgu.rawgcards.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tomgu.rawgcards.main.api.CompleteGame
import com.tomgu.rawgcards.main.api.Game

@Database(entities = [(CompleteGame::class)], version = 3)
abstract class AppDB: RoomDatabase() {

    abstract fun gameDao() : GameDao

}