package com.tomgu.rawgcards.account.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tomgu.rawgcards.di.AppComponent
import com.tomgu.rawgcards.login.AccountRepository
import com.tomgu.rawgcards.account.Account
import com.tomgu.rawgcards.api.CompleteGame
import javax.inject.Inject

class AccountDialogViewModel: ViewModel(), AppComponent.Injectable {

    @Inject
    lateinit var accountRepository: AccountRepository

    private var friendsLiveData : MutableLiveData<List<Account>> = MutableLiveData()

    private var friendRequestLiveData: MutableLiveData<List<Account>> = MutableLiveData()

    private var sharedGamesLiveData : MutableLiveData<List<CompleteGame>> = MutableLiveData()

    private var allUsersLiveData : MutableLiveData<List<Account>> = MutableLiveData()

    private var currentAccountLiveData: MutableLiveData<Account> = MutableLiveData()

    val isApiFailed: MutableLiveData<Boolean> = MutableLiveData(false)

    private var isUploadingLiveData = MutableLiveData<Boolean>().apply { value = false}
    fun isUploadingLiveData(): LiveData<Boolean> = isUploadingLiveData

    private val isRequest = MutableLiveData<Boolean>().apply {value = false}
    fun isRequest(): LiveData<Boolean> = isRequest

    private var isFriend = MutableLiveData<Boolean>().apply { value = true }
    fun isFriend(): LiveData<Boolean> {
        return isFriend
    }

    private var isUnknown = MutableLiveData<Boolean>().apply { value = false }
    fun isUnknown(): LiveData<Boolean> = isUnknown

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
        } . addOnFailureListener {
            isApiFailed.value = true
        }
    }

    fun friendState(friendUid: String){
        accountRepository.retrieveCurrentAccount().get().addOnSuccessListener {
            val account = it.toObject(Account::class.java)
            if(account!!.friendRequests!!.contains(friendUid)){
                isRequest.value = true
            }
            else if(account.friends!!.contains(friendUid)){
                isFriend.postValue(true)
            } else {
                isUnknown.value = true
            }
        }.addOnFailureListener {
            isApiFailed.value = true
        }
    }

    fun signOut(){
        accountRepository.signOut()
    }

    fun shareGame(game : CompleteGame, uid: String){
        accountRepository.uploadGameToFriend(game, uid)
    }

    fun acceptFriendRequest(friendUid: String){
        isUploadingLiveData.value = true
        accountRepository.acceptFriendRequest(friendUid) {
            isUploadingLiveData.value = it
        }
    }

    fun declineFriendRequest(friendUid: String){
        isUploadingLiveData.value = true
        accountRepository.declineFriendRequest(friendUid) {
            isUploadingLiveData.value = it
        }
    }

    fun addFriend(friendUid: String){
        isUploadingLiveData.value = true
        accountRepository.addFriend(friendUid) {
            isUploadingLiveData.value = it
        }
    }

    fun getSharedGames(friendUid: String){
        accountRepository.retrieveSharedGames().document(friendUid).collection("SharedGames").get()
            .addOnSuccessListener {
                var allGames = mutableListOf<CompleteGame>()
                it.forEach {
                    val game = it.toObject(CompleteGame::class.java)
                    allGames.add(game)
                    sharedGamesLiveData.value = allGames
                }
            }. addOnFailureListener {
                isApiFailed.value = true
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
                .addOnFailureListener {
                    isApiFailed.value = true
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
            .addOnFailureListener {
                isApiFailed.value = true
            }
    }


    fun getGamesLiveData(): LiveData<List<CompleteGame>>{
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
        return isUploadingLiveData
    }

}