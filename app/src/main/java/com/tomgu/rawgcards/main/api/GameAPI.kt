package com.tomgu.rawgcards.main.api

import io.reactivex.Observable
import retrofit2.http.GET

interface GameAPI {

    @GET("games?page=1&genres=1&dates=1995-01-01,2019-12-31")
    fun getStoreObject(): Observable<GameResponse>
}