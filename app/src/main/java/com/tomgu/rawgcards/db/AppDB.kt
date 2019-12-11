package com.tomgu.rawgcards.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tomgu.rawgcards.main.api.Game

@Database(entities = [(Game::class)], version = 2)
abstract class AppDB: RoomDatabase() {

    abstract fun gameDao() : GameDao

}