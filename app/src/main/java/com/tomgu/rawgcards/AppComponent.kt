package com.tomgu.rawgcards

import com.tomgu.rawgcards.main.ui.GameListFragment
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

    fun viewModelFactory(): MainViewModel.MainViewModelFactory

    interface Injectable {

        fun inject(appComponent: AppComponent)
    }

}