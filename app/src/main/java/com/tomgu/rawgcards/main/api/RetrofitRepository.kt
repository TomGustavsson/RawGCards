package com.tomgu.rawgcards.main.api

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitRepository {

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://rawg.io/api/")
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    var gameAPI : GameAPI = retrofit!!.create(GameAPI::class.java)

    fun getApi(): GameAPI {
        return gameAPI
    }
}