package com.tomgu.rawgcards.main.account.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.tomgu.rawgcards.di.AppComponent
import com.tomgu.rawgcards.login.AccountRepository
import com.tomgu.rawgcards.main.account.Account
import com.tomgu.rawgcards.main.api.Game
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AccountDialogViewModel: ViewModel(), AppComponent.Injectable {

    @Inject
    lateinit var accountRepository: AccountRepository

    private var friendsLiveData : MutableLiveData<List<Account>> = MutableLiveData()

    private var sharedGamesLiveData : MutableLiveData<List<Game>> = MutableLiveData()

    private var allUsersLiveData : MutableLiveData<List<Account>> = MutableLiveData()

    private var currentAccountLiveData: MutableLiveData<Account> = MutableLiveData()

     override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    fun getCurrentAccount(){
        accountRepository.retrieveCurrentAccount().get().addOnSuccessListener { documentSnapshot ->
            val account = documentSnapshot.toObject(Account::class.java)
            currentAccountLiveData.value = account
        }
    }

    fun getFriends() {

        accountRepository.retrieveFriends().get().addOnSuccessListener { documentSnapshot ->
            val account = documentSnapshot.toObject(Account::class.java)
            val friends = account!!.friends
            val allFriends = mutableListOf<Account>()

            friends!!.forEach {
                accountRepository.database().document(it).get().addOnSuccessListener {
                    val friendAccount = it.toObject(Account::class.java)
                    allFriends.add(friendAccount!!)
                    friendsLiveData.value = allFriends
                }
            }
        }
    }

    fun signOut(){
        accountRepository.signOut()
    }

    fun shareGame(game : Game, uid: String){
        accountRepository.uploadGameToFriend(game, uid)
    }

    fun acceptFriendRequest(friendUid: String){
        accountRepository.acceptFriendRequest(friendUid)
    }

    fun declineFriendRequest(friendUid: String){
        accountRepository.declineFriendRequest(friendUid)
    }

    fun addFriend(friendUid: String){
        accountRepository.addFriend(friendUid)
    }

    fun getSharedGames(friendUid: String){
        accountRepository.retrieveSharedGames().document(friendUid).collection("SharedGames").get()
            .addOnSuccessListener {
                var allGames = mutableListOf<Game>()
                it.forEach {
                    val game = it.toObject(Game::class.java)
                    allGames.add(game)
                    sharedGamesLiveData.value = allGames
                }
            }
    }

    fun getAllUsers(){
        accountRepository.database().get()
            .addOnSuccessListener { documentSnapshot ->
                val allUsers = mutableListOf<Account>()
                documentSnapshot.forEach {
                    val account = it.toObject(Account::class.java)
                    allUsers.add(account)
                    allUsersLiveData.value = allUsers
                }
            }
    }

    fun getGamesLiveData(): LiveData<List<Game>>{
        return sharedGamesLiveData
    }

    fun getFriendsLiveData(): LiveData<List<Account>>{
        return friendsLiveData
    }

    fun getUsersLiveData(): LiveData<List<Account>>{
        return allUsersLiveData
    }

    fun getCurrentAccountLiveData() : LiveData<Account> {
        return currentAccountLiveData
    }

}