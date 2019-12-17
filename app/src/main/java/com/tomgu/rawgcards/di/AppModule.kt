package com.tomgu.rawgcards.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.room.Room
import com.google.gson.Gson
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.db.AppDB
import com.tomgu.rawgcards.db.GameDao
import com.tomgu.rawgcards.main.GameRepository
import com.tomgu.rawgcards.main.api.GameAPI
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
class AppModule(private val applicationContext: Context){

    @Singleton
    @Provides
    fun appApplication(): AppApplication {
        return applicationContext as AppApplication
    }

    @Singleton
    @Provides
    fun appViewModelCreator(appApplication: AppApplication): AppViewModelFactory {
        return AppViewModelFactory(appApplication)
    }

    @Singleton
    @Provides
    fun sharedPreferences(context: Context): SharedPreferences {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs
    }

    @Singleton
    @Provides
    fun retrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://rawg.io/api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun applicationContext(): Context {
        return this.applicationContext
    }

    @Singleton
    @Provides
    fun gameAPI(retrofit: Retrofit): GameAPI{
        return retrofit().create(GameAPI::class.java)
    }

    @Singleton
    @Provides
    fun appDb(): AppDB{
        return Room.databaseBuilder(applicationContext, AppDB::class.java, "roomDb")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun provideGameDao(appDb: AppDB): GameDao {
        return appDb.gameDao()
    }
    @Singleton
    @Provides
    fun gameRepository(gameAPI: GameAPI, gameDao: GameDao) : GameRepository{
        return GameRepository(gameAPI, gameDao)
    }

}