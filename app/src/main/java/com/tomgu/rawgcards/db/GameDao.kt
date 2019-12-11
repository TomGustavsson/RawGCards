package com.tomgu.rawgcards.db

import android.database.Observable
import androidx.lifecycle.LiveData
import androidx.room.*
import com.tomgu.rawgcards.main.api.Game
import io.reactivex.Flowable

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveGame(ge: Game)

    @Query("select * from Game")
    fun readGame() : Flowable<List<Game>>

    @Query("DELETE FROM Game")
    fun nuketable()

    @Delete
    fun delete(ge: Game)

}