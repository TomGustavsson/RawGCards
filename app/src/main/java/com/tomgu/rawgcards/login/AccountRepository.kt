package com.tomgu.rawgcards.login

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AccountRepository {

     var auth = FirebaseAuth.getInstance()

    fun firebaseSignInWithGoogle(acct: GoogleSignInAccount) {
        Log.d("Billyz", "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                val user = auth.currentUser
            } else {
                Log.d("Billyz", "signInWithCredential:failure", authTask.exception)
            }
        }
    }

}