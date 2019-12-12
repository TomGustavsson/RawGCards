package com.tomgu.rawgcards.di

import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.main.gamedialog.GIDViewModel
import com.tomgu.rawgcards.main.gamedialog.GameInfoDialog
import com.tomgu.rawgcards.main.gamefragment.GameListFragment
import com.tomgu.rawgcards.main.gamefragment.GameListViewModel
import com.tomgu.rawgcards.main.ui.MainActivity
import com.tomgu.rawgcards.main.ui.MainViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component (modules = [AppModule::class])
interface AppComponent {

    fun inject(mainViewModel: MainViewModel)

    fun inject(appApplication: AppApplication)

    fun inject(gameListFragment: GameListFragment)

    fun inject(mainActivity: MainActivity)

    fun inject(gameListViewModel: GameListViewModel)

    fun inject(gameInfoDialog: GameInfoDialog)

    fun inject(gidViewModel: GIDViewModel)

    fun appViewModelFactory(): AppViewModelFactory

    interface Injectable {

        fun inject(appComponent: AppComponent)
    }

}