package com.ahmedhenna.thepantry.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ahmedhenna.thepantry.R
import com.ahmedhenna.thepantry.model.UserItem
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore


    fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUser(onComplete: (UserItem)->Unit) {
        val authenticatedUser = auth.currentUser!!
        db.collection("users").document(authenticatedUser.uid).get().addOnCompleteListener {
            if(it.isSuccessful && it.result != null){
                onComplete(it.result.toObject(UserItem::class.java)!!)
            }
        }
    }

    fun signInEmailPassword(
        email: String,
        password: String,
        onComplete: () -> Unit,
        onFail: (msg: String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                onComplete()
            } else {
                onFail(it.exception?.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun registerEmailPassword(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        onComplete: () -> Unit,
        onFail: (msg: String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName("$firstName $lastName").build()
                it.result.user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { updateProfileResult ->
                        if (updateProfileResult.isSuccessful) {
                            db.collection("users").document(it.result!!.user!!.uid)
                                .set(UserItem(firstName, lastName, email, listOf(), listOf()))
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

    fun authWithGoogle(token: String, onComplete: () -> Unit, onFail: (msg: String) -> Unit) {
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

    fun sendResetPasswordEmail(email: String, onComplete: () -> Unit, onFail: (msg: String) -> Unit) {
        val currentUser = auth.currentUser
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onComplete()
            } else {
                onFail(it.exception?.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        onComplete: () -> Unit,
        onFail: (msg: String) -> Unit
    ) {
        val currentUser = auth.currentUser!!
        currentUser.reauthenticate(
            EmailAuthProvider.getCredential(
                currentUser.email!!,
                currentPassword
            )
        ).addOnCompleteListener { result ->
            if (result.isSuccessful) {
                currentUser.updatePassword(newPassword).addOnCompleteListener {
                    if (it.isSuccessful) {
                        onComplete()
                    } else {
                        onFail(it.exception?.localizedMessage ?: "Unknown error")
                    }
                }
            } else {
                onFail(result.exception?.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }
}