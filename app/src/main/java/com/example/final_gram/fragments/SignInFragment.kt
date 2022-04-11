package com.example.final_gram.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.final_gram.HolderActivity
import com.example.final_gram.R
import com.example.final_gram.databinding.FragmentSignInBinding
import com.example.final_gram.utils.NetworkHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SignInFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var binding: FragmentSignInBinding
    lateinit var googleSignInClient: GoogleSignInClient

    var RC_SIGN_IN = 1
    private val TAG = "SignInFragment"
    lateinit var auth: FirebaseAuth
    lateinit var networkHelper: NetworkHelper
    var token: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(layoutInflater, container, false)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(binding.root.context, gso)
        auth = FirebaseAuth.getInstance()
        networkHelper = NetworkHelper(binding.root.context)

        token = (activity as HolderActivity).intent.extras!!.getString("key")
        (activity as HolderActivity)
        Log.d(TAG, "onCreateView: $token")
        binding.signIn.setOnClickListener {

            if (networkHelper.isNetworkConnected()) {

                signIn()
            } else {
                Toast.makeText(
                    binding.root.context,
                    "Make sure your internet is on",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }


        binding.logOut.setOnClickListener {
            if (networkHelper.isNetworkConnected()) {
                googleSignInClient.signOut()

                Toast.makeText(
                    binding.root.context,
                    "you have logged out",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    binding.root.context,
                    "Make sure your internet is on",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }



        return binding.root
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    var bundle = Bundle()
                    bundle.putString("key", token)
                    findNavController().navigate(R.id.homeFragment, bundle)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        binding.root.context,
                        task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignInFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}