package com.tomgu.rawgcards.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.SignInButton.COLOR_DARK
import com.google.android.gms.common.api.ApiException
import com.tomgu.rawgcards.main.account.Account
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.databinding.ActivityLoginBinding
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.main.ui.MainActivity
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {


    lateinit var gso : GoogleSignInOptions

    lateinit var googleSignInClient: GoogleSignInClient
    private val GOOGLE_SIGN = 123

    private lateinit var userText : TextView
    private lateinit var button : SignInButton

    @Inject
    lateinit var vmFactory : AppViewModelFactory
    lateinit var viewModel: LoginViewModel

    lateinit var binding: ActivityLoginBinding
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        //Dagger2 skit
        (applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[LoginViewModel::class.java]

        button = findViewById(R.id.signInButton)
        userText = findViewById(R.id.userTextView)
        progressBar = findViewById(R.id.loginProgresBar)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

       googleSignInClient = GoogleSignIn.getClient(this,gso)

        button.setSize(SignInButton.SIZE_WIDE)
        button.setColorScheme(COLOR_DARK)

        button.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            button.visibility = View.GONE
            signIn()
        }

        viewModel.getLiveData().observe(this, Observer {

            binding.userTextView.setText("Welcome" + it.name)
           if (it != null){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        })


    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                viewModel.signInWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Billyz", "Google sign in failed", e)
                // ...
            }
        }
    }


    private fun updateUI(user: Account?) {
        userText.setText(user?.email)
    }
}
