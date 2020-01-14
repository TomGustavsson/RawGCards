package com.tomgu.rawgcards.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.tomgu.rawgcards.main.account.Account
import com.tomgu.rawgcards.main.api.Game
import io.reactivex.Completable


class AccountRepository {

    var auth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance().collection("Accounts")
    val authenticatedUserMutableLiveData: MutableLiveData<Account> = MutableLiveData()

    fun firebaseSignInWithGoogle(acct: GoogleSignInAccount): MutableLiveData<Account> {

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    val email = user.email
                    val photo = user.photoUrl
                    val name = user.displayName
                    val uid = user.uid
                    val account = Account(
                        email!!,
                        photo.toString(),
                        name.toString(),
                        uid
                    )
                    authenticatedUserMutableLiveData.value = account
                    Log.d("billyz", account.email)

                    db.document(uid).get()
                        .addOnCompleteListener(OnCompleteListener<DocumentSnapshot?> { task ->
                            if (task.isSuccessful) {
                                val document = task.result
                                if (document!!.exists()) {
                                    Log.d("tgiw", "Document exists!")
                                } else {
                                    db.document(user.uid).set(account)
                                    Log.d("tgiw", "Document does not exist!")
                                }
                            } else {
                                Log.d("tgiw", "Failed with: ", task.exception)
                            }
                        })
                }
            } else {
                Log.d("Billyz", "signInWithCredential:failure", authTask.exception)
            }

        }
        return authenticatedUserMutableLiveData
    }

    fun retrieveCurrentAccount(): DocumentReference{
        return db.document(auth.currentUser!!.uid)
    }


    fun signOut() {
        auth.signOut()
    }

    fun acceptFriendRequest(friendUid: String) {
        db.document(auth.currentUser!!.uid).get().addOnSuccessListener {
            val account = it.toObject(Account::class.java)
            account?.friends!!.add(friendUid)
            account.friendRequests!!.remove(friendUid)
            db.document(auth.currentUser!!.uid).set(account)
        }
    }

    fun declineFriendRequest(friendUid: String){
        db.document(auth.currentUser!!.uid).get().addOnSuccessListener {
            val account = it.toObject(Account::class.java)
            account?.friendRequests!!.remove(friendUid)
            db.document(auth.currentUser!!.uid).set(account)
        }
    }

    fun addFriend(friendUid: String) {

        db.document(friendUid).get().addOnSuccessListener {
            val account = it.toObject(Account::class.java)
            account?.friendRequests!!.add(auth.currentUser!!.uid)
            db.document(friendUid).set(account)
        }
        db.document(auth.currentUser!!.uid).get().addOnSuccessListener {
            val account = it.toObject(Account::class.java)
            account?.friends!!.add(friendUid)
            db.document(auth.currentUser!!.uid).set(account)
        }
    }

    fun retrieveFriends(): DocumentReference {
        return db.document(auth.currentUser!!.uid)
    }

    fun retrieveSharedGames(): CollectionReference{
        return db.document(auth.currentUser!!.uid).collection("Friends")
    }

    fun database(): CollectionReference{
        return db
    }

    fun uploadGameToFriend(game: Game, friendUid: String) {
        db.document(friendUid).collection("Friends").document(auth.currentUser!!.uid)
            .collection("SharedGames").document().set(game)
    }

}


