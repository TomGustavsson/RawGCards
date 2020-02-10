package com.tomgu.rawgcards.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.databinding.FragmentLoginBinding
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.main.ui.MainActivity
import javax.inject.Inject

class LoginFragment : Fragment() {

    lateinit var gso : GoogleSignInOptions

    lateinit var googleSignInClient: GoogleSignInClient
    private val GOOGLE_SIGN = 123

    private lateinit var userText : TextView
    private lateinit var button : SignInButton

    @Inject
    lateinit var vmFactory : AppViewModelFactory
    lateinit var viewModel: LoginViewModel

    lateinit var binding: FragmentLoginBinding
    lateinit var progressBar: ProgressBar
    lateinit var signInGuest : Button


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentLoginBinding = FragmentLoginBinding.inflate(LayoutInflater.from(context))

        //Dagger2 Skit
        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[LoginViewModel::class.java]

        button = binding.signInButton
        signInGuest = binding.signInGuest
        userText = binding.userTextView
        progressBar = binding.loginProgresBar

        Log.d("TGIW", viewModel.getCurrentAccount().toString())

        if(viewModel.getCurrentAccount() != null) {

            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra("account", "verified")
            startActivity(intent)
        }

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity!!,gso)

        button.setSize(SignInButton.SIZE_WIDE)
        button.setColorScheme(SignInButton.COLOR_DARK)

        button.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            button.visibility = View.GONE
            signIn()
        }

        signInGuest.setOnClickListener{

            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra("account", "guest")
            startActivity(intent)
        }

        viewModel.getLiveData().observe(this, Observer {
            binding.userTextView.setText("Welcome " + it.name)
            if (it != null){
                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("account", "verified")
                startActivity(intent)
            }
        })

        return binding.root
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN)
    }

    fun snackBar(){

        val snackbar = Snackbar.make(binding.loginBackground, "Google sign in failed", Snackbar.LENGTH_INDEFINITE)
        snackbar.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
               // val snackbar = Snackbar.make(binding.loginBackground, "Google sign in failed", Snackbar.LENGTH_INDEFINITE)
              //  snackbar.show()
                progressBar.visibility = View.GONE
                button.visibility = View.VISIBLE
                // ...
            }
        }
    }
    }
