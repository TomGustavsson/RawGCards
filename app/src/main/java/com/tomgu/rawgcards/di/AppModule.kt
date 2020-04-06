package com.tomgu.rawgcards.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.preference.PreferenceManager
import android.util.Log
import android.widget.MediaController
import androidx.room.Room
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.db.AppDB
import com.tomgu.rawgcards.db.GameDao
import com.tomgu.rawgcards.login.AccountRepository
import com.tomgu.rawgcards.GameRepository
import com.tomgu.rawgcards.api.GameAPI
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
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
    fun httpClient(context: Context): OkHttpClient{
        val cacheSize = (5 * 1024 * 1024).toLong()
        val myCache = Cache(context.cacheDir, cacheSize)
        return OkHttpClient.Builder()
            .cache(myCache)
            .addNetworkInterceptor {

                Log.d("TGIW", "network interceptor called")
                var response : Response = it.proceed(it.request())

                var cacheControl = CacheControl.Builder()
                    .maxAge(5, TimeUnit.HOURS)
                    .build()

                response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", cacheControl.toString())
                    .build()
            }
            .build()
    }

    @Singleton
    @Provides
    fun retrofit(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://rawg.io/api/")
            .client(httpClient)
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
        return retrofit.create(GameAPI::class.java)
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
    fun gameRepository(gameAPI: GameAPI, gameDao: GameDao) : GameRepository {
        return GameRepository(gameAPI, gameDao)
    }

    @Singleton
    @Provides
    fun accountRepository(): AccountRepository{
        return AccountRepository()
    }



}