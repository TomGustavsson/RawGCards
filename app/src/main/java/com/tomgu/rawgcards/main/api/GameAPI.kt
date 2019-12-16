package com.tomgu.rawgcards.main.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GameAPI {

    @GET("games")
    fun getStoreObject(
        @Query("page") page: Int,
        @Query("genres") genres: String,
        @Query("dates") dates: String
    ): Observable<GameResponse>

    @GET("games/{slug}")
    fun getGameObject(@Path("slug") slug: String): Observable<GameInfo>

}