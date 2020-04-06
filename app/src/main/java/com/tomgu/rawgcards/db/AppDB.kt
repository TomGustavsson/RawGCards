package com.tomgu.rawgcards.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tomgu.rawgcards.api.CompleteGame

@Database(entities = [(CompleteGame::class)], version = 4)
abstract class AppDB: RoomDatabase() {

    abstract fun gameDao() : GameDao

}