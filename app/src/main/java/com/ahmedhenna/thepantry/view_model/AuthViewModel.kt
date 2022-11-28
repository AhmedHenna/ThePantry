package com.ahmedhenna.thepantry.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ahmedhenna.thepantry.model.UserItem
import com.ahmedhenna.thepantry.R

class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore


    fun isSignedIn():Boolean{
        return auth.currentUser != null
    }

    fun getCurrentUser(): FirebaseUser{
        return auth.currentUser!!
    }

    fun signInEmailPassword(email: String, password: String, onComplete: () -> Unit, onFail: (msg: String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                onComplete()
            } else {
                onFail(it.exception?.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun registerEmailPassword(email: String,name: String, password: String, onComplete: () -> Unit, onFail: (msg: String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name).build()
                it.result.user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { updateProfileResult ->
                        if (updateProfileResult.isSuccessful) {
                            db.collection("users").document(it.result!!.user!!.uid)
                                .set(UserItem(name, email, listOf(), listOf()))
                                .addOnCompleteListener { databaseResult ->
                                    if (databaseResult.isSuccessful) {
                                        onComplete()
                                    } else {
                                        onFail(
                                            databaseResult.exception?.localizedMessage
                                                ?: "Unknown error"
                                        )
                                    }
                                }
                        } else {
                            onFail(
                                updateProfileResult.exception?.localizedMessage ?: "Unknown error"
                            )
                        }
                    }

            } else {
                onFail(it.exception?.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    fun authWithGoogle(token: String, onComplete: () -> Unit, onFail: (msg: String) -> Unit){
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    onComplete()
                } else {
                    onFail(task.exception?.localizedMessage ?: "Unknown error")
                }
            }


    }

    fun sendResetPasswordEmail(onComplete: () -> Unit, onFail: (msg: String) -> Unit) {
        val currentUser = auth.currentUser
        auth.sendPasswordResetEmail(currentUser!!.email!!).addOnCompleteListener {
            if (it.isSuccessful) {
                onComplete()
            } else {
                onFail(it.exception?.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun signOut(){
        auth.signOut()
    }
}