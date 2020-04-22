package com.tomgu.rawgcards.account.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tomgu.rawgcards.di.AppComponent
import com.tomgu.rawgcards.login.AccountRepository
import com.tomgu.rawgcards.account.models.Account
import com.tomgu.rawgcards.account.models.State
import com.tomgu.rawgcards.api.CompleteGame
import javax.inject.Inject

class AccountDialogViewModel: ViewModel(), AppComponent.Injectable {

    @Inject
    lateinit var accountRepository: AccountRepository

    private var usersLiveData: MutableLiveData<List<Account>> = MutableLiveData()
    fun getUserLiveData(): LiveData<List<Account>> = usersLiveData

    private var friendRequestLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    fun getFriendRequestLiveData(): LiveData<Boolean> = friendRequestLiveData

    private var sharedGamesLiveData : MutableLiveData<List<CompleteGame>> = MutableLiveData()
    fun getGamesLiveData(): LiveData<List<CompleteGame>> = sharedGamesLiveData

    private var currentAccountLiveData: MutableLiveData<Account> = MutableLiveData()
    fun getCurrentAccountLiveData(): LiveData<Account> = currentAccountLiveData

    private val isApiFailed: MutableLiveData<Boolean> = MutableLiveData(false)
    fun isApiFailed(): LiveData<Boolean> = isApiFailed

    private var isUploadingLiveData = MutableLiveData<Boolean>()
    fun isUploadingLiveData(): LiveData<Boolean> = isUploadingLiveData

    private var friendStateLiveData = MutableLiveData<FriendState>()
    fun getFriendStateLiveData(): LiveData<FriendState> = friendStateLiveData

    private var requestStateLiveData = MutableLiveData<State>()
    fun getRequestStateLiveData(): LiveData<State> = requestStateLiveData

    var toastMessage: MutableLiveData<String> = MutableLiveData()

     override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    fun getCurrentAccount(){
        accountRepository.retrieveCurrentAccount().get().addOnSuccessListener { documentSnapshot ->
            val account = documentSnapshot.toObject(Account::class.java)
            currentAccountLiveData.value = account
        }
    }

    fun friendState(friendUid: String){
        accountRepository.retrieveCurrentAccount().get().addOnSuccessListener {
            val account = it.toObject(Account::class.java)

            account!!.friendRequests!!.forEach {
                if(it.id == friendUid){
                    friendStateLiveData.value = FriendState.REQUEST
                    requestStateLiveData.value = it.state
                    return@addOnSuccessListener
                }
            }
            if(account.friends!!.contains(friendUid)){
                friendStateLiveData.value = FriendState.FRIEND
                return@addOnSuccessListener
            } else {
                friendStateLiveData.value = FriendState.UNKNOWN
                return@addOnSuccessListener
            }
        }.addOnFailureListener {
            isApiFailed.value = true
        }
    }

    fun signOut(){
        accountRepository.signOut()
    }

    fun shareGame(game : CompleteGame, uid: String){
        accountRepository.uploadGameToFriend(game, uid){
            toastMessage.value = it
        }
    }

    fun acceptFriendRequest(friendUid: String){
        isUploadingLiveData.value = true
        accountRepository.acceptFriendRequest(friendUid) {
            isUploadingLiveData.value = false
            if(it){
                friendStateLiveData.value = FriendState.FRIEND
            } else {
                //Show that something went wrong
            }
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
            isUploadingLiveData.value = false
            if(it){
                friendState(friendUid)
            } else {
                //Something went wrong with the friend request
                friendStateLiveData.value = FriendState.UNKNOWN
            }
        }
    }

    fun getSharedGames(friendUid: String){
        accountRepository.retrieveSharedGames().document(friendUid).collection("SharedGames").get()
            .addOnSuccessListener {
                val allGames = mutableListOf<CompleteGame>()
                it.forEach {
                    val game = it.toObject(CompleteGame::class.java)
                    allGames.add(game)
                    sharedGamesLiveData.value = allGames
                }
            }. addOnFailureListener {
                isApiFailed.value = true
            }
    }

    fun getUsers(state : FriendState){
        when(state){
            FriendState.FRIEND -> {

                accountRepository.retrieveCurrentAccount().get().addOnSuccessListener { documentSnapshot ->
                    val account = documentSnapshot.toObject(Account::class.java)
                    val friends = account!!.friends
                    val allFriends = mutableListOf<Account>()

                    friends!!.forEach {
                        accountRepository.database().document(it).get().addOnSuccessListener {
                            val friendAccount = it.toObject(Account::class.java)
                            allFriends.add(friendAccount!!)
                            usersLiveData.value = allFriends
                        }
                    }
                } . addOnFailureListener {
                    isApiFailed.value = true
                }
            }
            FriendState.UNKNOWN -> {
                accountRepository.retrieveCurrentAccount().get().addOnSuccessListener {
                    val currentUser = it.toObject(Account::class.java)!!

                    accountRepository.database().get()
                        .addOnSuccessListener { documentSnapshot ->
                            val allUsers = mutableListOf<Account>()
                            documentSnapshot.forEach {
                                val account = it.toObject(Account::class.java)
                                if (account != currentUser)
                                    allUsers.add(account)
                                usersLiveData.value = allUsers
                            }
                        }
                        .addOnFailureListener {
                            isApiFailed.value = true
                        }
                }
            }
            FriendState.REQUEST -> {

                accountRepository.retrieveCurrentAccount().get().addOnSuccessListener { documentSnapshot ->
                    val account = documentSnapshot.toObject(Account::class.java)
                    val friendRequests = account!!.friendRequests?.filter { it.state == State.Asked }
                    val allFriendRequests = mutableListOf<Account>()

                    friendRequests?.forEach {
                        accountRepository.database().document(it.id!!).get().addOnSuccessListener {
                            val friendRequestAccount = it.toObject(Account::class.java)
                            allFriendRequests.add(friendRequestAccount!!)
                            usersLiveData.value = allFriendRequests
                            friendRequestLiveData.value = true

                        }
                    }
                }
                    .addOnFailureListener {
                        isApiFailed.value = true
                    }
            }
        }
    }

}

enum class FriendState{
    FRIEND,
    REQUEST,
    UNKNOWN
}