package com.tomgu.rawgcards.di

import android.app.Application
import com.tomgu.rawgcards.di.DaggerAppComponent

class AppApplication : Application(){

 private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this)).build()
    }

    fun appComponent(): AppComponent {
        return appComponent
    }
}