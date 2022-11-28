package com.ahmedhenna.thepantry.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.ahmedhenna.thepantry.view_model.AuthViewModel

open class AuthFragment : LoadableFragment() {
    protected val authViewModel: AuthViewModel by viewModels()

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var onComplete: ()->Unit = {}
    private var onFail: (msg: String)->Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)!!
                authViewModel.authWithGoogle(account.idToken!!,onComplete, onFail)
            }
        }
    }

    protected fun initiateGoogleAuth(onComplete: ()->Unit, onFail: (msg: String)->Unit) {
        this.onComplete = onComplete
        this.onFail = onFail
        val signInIntent = authViewModel.getGoogleSignInClient(requireActivity()).signInIntent
        resultLauncher.launch(signInIntent)
    }


}