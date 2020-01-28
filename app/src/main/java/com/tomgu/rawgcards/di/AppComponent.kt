package com.tomgu.rawgcards.di

import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.login.LoginActivity
import com.tomgu.rawgcards.login.LoginFragment
import com.tomgu.rawgcards.login.LoginViewModel
import com.tomgu.rawgcards.main.account.ui.AccountDialogViewModel
import com.tomgu.rawgcards.main.account.ui.AccountFragment
import com.tomgu.rawgcards.main.account.ui.FriendFragment
import com.tomgu.rawgcards.main.gameinfofrag.BottomSheetDialog
import com.tomgu.rawgcards.main.gameinfofrag.GIDViewModel
import com.tomgu.rawgcards.main.gameinfofrag.GameInfoFragment
import com.tomgu.rawgcards.main.gamefragment.GameListFragment
import com.tomgu.rawgcards.main.gamefragment.GameListViewModel
import com.tomgu.rawgcards.main.ui.CardStackFragment
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

    fun inject(gidViewModel: GIDViewModel)

    fun inject(loginViewModel: LoginViewModel)

    fun inject(loginActivity: LoginActivity)

    fun inject(accountDialogViewModel: AccountDialogViewModel)

    fun inject(bottomSheetDialog: BottomSheetDialog)

    fun inject(friendFragment: FriendFragment)

    fun inject(gameInfoFragment: GameInfoFragment)

    fun inject(cardStackFragment: CardStackFragment)

    fun inject(accountFragment: AccountFragment)

    fun inject(loginFragment: LoginFragment)

    fun appViewModelFactory(): AppViewModelFactory

    interface Injectable {

        fun inject(appComponent: AppComponent)
    }

}