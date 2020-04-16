package com.tomgu.rawgcards.login

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.tomgu.rawgcards.account.models.Account
import com.tomgu.rawgcards.account.models.FriendRequest
import com.tomgu.rawgcards.account.models.State
import com.tomgu.rawgcards.api.CompleteGame

class AccountRepository {

    var auth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance().collection("Accounts")

    fun firebaseSignInWithGoogle(acct: GoogleSignInAccount, callback: (Account) -> Unit) {

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
                    Log.d("billyz", account.email)
                    callback.invoke(account)
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
    }

    fun getCurrentUser(): FirebaseUser?{
        return auth.currentUser
    }

    fun retrieveCurrentAccount(): DocumentReference{
        return db.document(auth.currentUser!!.uid)
    }

    fun signOut() {
        auth.signOut()
    }

    fun acceptFriendRequest(friendUid: String, callback : (Boolean) -> Unit) {

        db.document(auth.currentUser!!.uid).get().addOnSuccessListener {
            val account = it.toObject(Account::class.java)
            account?.friends!!.add(friendUid)

            account.friendRequests!!.forEach {
                if(it.id == friendUid){
                    account.friendRequests!!.remove(it)
                }
            }
            db.document(auth.currentUser!!.uid).set(account).addOnCompleteListener {

                db.document(friendUid).get().addOnSuccessListener {
                    val friendAccount = it.toObject(Account::class.java)
                    friendAccount?.friends!!.add(auth.currentUser!!.uid)

                    friendAccount.friendRequests!!.forEach {
                        if(it.id == auth.currentUser!!.uid){
                            friendAccount.friendRequests!!.remove(it)
                        }
                    }

                    db.document(friendUid).set(friendAccount).addOnCompleteListener {
                        callback.invoke(it.isSuccessful)
                    }.addOnFailureListener {
                        callback.invoke(false)
                    }
                }
            }.addOnFailureListener {
                callback.invoke(false)
            }
        }
    }

    fun declineFriendRequest(friendUid: String, callback: (Boolean) -> Unit){
        db.document(auth.currentUser!!.uid).get().addOnSuccessListener {
            val account = it.toObject(Account::class.java)
           // account?.friendRequests!!.remove(friendUid)

            account?.friendRequests!!.forEach {
                if(it.id == friendUid){
                    account.friendRequests!!.remove(it)
                }
            }
            db.document(auth.currentUser!!.uid).set(account)
        }
        db.document(friendUid).get().addOnSuccessListener {
            val account = it.toObject(Account::class.java)
            account?.friends!!.remove(auth.currentUser!!.uid)
            db.document(friendUid).set(account).addOnCompleteListener {
                callback.invoke(it.isSuccessful)
            }.addOnFailureListener {
                callback.invoke(false)
            }

        }
    }

    fun addFriend(friendUid: String, callback : (Boolean) -> Unit) {

        db.document(friendUid).get().addOnSuccessListener {
            val account = it.toObject(Account::class.java)
            account?.friendRequests!!.add(FriendRequest(auth.currentUser!!.uid, State.Asked))
            db.document(friendUid).set(account)
        }
        db.document(auth.currentUser!!.uid).get().addOnSuccessListener {
            val account = it.toObject(Account::class.java)
            account?.friendRequests!!.add(FriendRequest(friendUid, State.Pending))
            db.document(auth.currentUser!!.uid).set(account).addOnCompleteListener {
                callback.invoke(it.isSuccessful)
            }.addOnFailureListener {
                callback.invoke(false)
            }
        }
    }

    fun retrieveSharedGames(): CollectionReference{
        return db.document(auth.currentUser!!.uid).collection("Friends")
    }

    fun database(): CollectionReference{
        return db
    }

    fun uploadGameToFriend(game: CompleteGame, friendUid: String, callback: (String) -> Unit) {
        db.document(friendUid).collection("Friends").document(auth.currentUser!!.uid)
            .collection("SharedGames").get().addOnSuccessListener {
                val gameDocuments = mutableListOf<String>()
                it.forEach {
                    gameDocuments.add(it.id)
                }
                if(gameDocuments.contains(game.slug)){
                    callback.invoke("User already shared this game")
                } else {
                    db.document(friendUid).collection("Friends").document(auth.currentUser!!.uid)
                        .collection("SharedGames").document(game.slug).set(game)
                        .addOnSuccessListener {
                            callback.invoke("Your game was uploaded")
                        }
                        .addOnFailureListener{
                            callback.invoke("Something went wrong..")
                        }
                }

            }
    }


}


