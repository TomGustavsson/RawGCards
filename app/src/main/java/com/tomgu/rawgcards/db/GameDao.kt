package com.tomgu.rawgcards.db

import android.database.Observable
import androidx.lifecycle.LiveData
import androidx.room.*
import com.tomgu.rawgcards.main.api.Game
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveGame(ge: Game)

    @Query("select * from Game")
    fun readGame() : Flowable<MutableList<Game>>

    @Query("DELETE FROM Game")
    fun nuketable()

    @Delete
    fun delete(ge: Game)

    @Query("SELECT * FROM Game WHERE slug=:id ")
    fun loadSingle(id: String): Single<Game>

}