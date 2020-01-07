package com.tomgu.rawgcards.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.tomgu.rawgcards.main.account.Account


class AccountRepository {

     var auth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance().collection("Accounts")
    val authenticatedUserMutableLiveData : MutableLiveData<Account> = MutableLiveData()

    val currentAccountMutableLiveData : MutableLiveData<Account> = MutableLiveData()

    fun firebaseSignInWithGoogle(acct: GoogleSignInAccount): MutableLiveData<Account> {
        Log.d("Billyz", "firebaseAuthWithGoogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                val user = auth.currentUser
                if(user != null){
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

                    db.document(uid).get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot?> { task ->
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

    fun getCurrentAccount(){
        db.document(auth.currentUser!!.uid).get().addOnSuccessListener { documentSnapshot ->
            val account = documentSnapshot.toObject(Account::class.java)
            currentAccountMutableLiveData.value = account
        }
    }

    fun signOut(){
        auth.signOut()
    }

}


