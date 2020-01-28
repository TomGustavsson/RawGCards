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

    private var friendRequestLiveData: MutableLiveData<List<Account>> = MutableLiveData()

    private var sharedGamesLiveData : MutableLiveData<List<Game>> = MutableLiveData()

    private var allUsersLiveData : MutableLiveData<List<Account>> = MutableLiveData()

    private var currentAccountLiveData: MutableLiveData<Account> = MutableLiveData()

    private var isUploadedLiveData: MutableLiveData<Boolean> = MutableLiveData()

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

        accountRepository.retrieveCurrentAccount().get().addOnSuccessListener { documentSnapshot ->
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
        accountRepository.acceptFriendRequest(friendUid) {
            isUploadedLiveData.value = it
        }
    }

    fun declineFriendRequest(friendUid: String){
        accountRepository.declineFriendRequest(friendUid) {
            isUploadedLiveData.value = it
        }
    }

    fun addFriend(friendUid: String){
        accountRepository.addFriend(friendUid) {
            isUploadedLiveData.value = it
        }
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

        accountRepository.retrieveCurrentAccount().get().addOnSuccessListener {
           var currentUser = it.toObject(Account::class.java)!!

            accountRepository.database().get()
                .addOnSuccessListener { documentSnapshot ->
                    val allUsers = mutableListOf<Account>()
                    documentSnapshot.forEach {
                        val account = it.toObject(Account::class.java)
                        if (account != currentUser)
                            allUsers.add(account)
                        allUsersLiveData.value = allUsers
                    }
                }
        }

    }

    fun getAllFriendRequests(){
        accountRepository.retrieveCurrentAccount().get().addOnSuccessListener { documentSnapshot ->
            val account = documentSnapshot.toObject(Account::class.java)
            val friendRequests = account!!.friendRequests
            val allFriendRequests = mutableListOf<Account>()

            friendRequests!!.forEach {
                accountRepository.database().document(it).get().addOnSuccessListener {
                    val friendRequestAccount = it.toObject(Account::class.java)
                    allFriendRequests.add(friendRequestAccount!!)
                    friendRequestLiveData.value = allFriendRequests
                }
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

    fun getFriendRequestLiveData(): LiveData<List<Account>>{
        return friendRequestLiveData
    }

    fun getIsUploadedLiveData(): LiveData<Boolean>{
        return isUploadedLiveData
    }

}