package com.tomgu.rawgcards.db

import androidx.room.*
import com.tomgu.rawgcards.main.api.CompleteGame
import com.tomgu.rawgcards.main.api.Game
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveGame(ge: CompleteGame)

    @Query("select * from CompleteGame")
    fun readGame() : Flowable<MutableList<CompleteGame>>

    @Query("DELETE FROM CompleteGame")
    fun nuketable()

    @Delete
    fun delete(ge: CompleteGame)

    @Query("SELECT * FROM CompleteGame WHERE slug=:id ")
    fun loadSingle(id: String): Single<CompleteGame>

}