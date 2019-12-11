package com.tomgu.rawgcards

import android.app.Application
import android.content.Context

class AppApplication : Application(){

 private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }

    fun appComponent(): AppComponent{
        return appComponent
    }
}