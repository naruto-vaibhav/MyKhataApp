package com.naruto.managekhata.service.impl

import android.app.Activity
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import com.naruto.managekhata.User
import com.naruto.managekhata.service.AccountService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AccountServiceImpl @Inject constructor() : AccountService {

    private var verificationId: String? = null

    override val currentUser: Flow<User?>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { User(it.uid) })
                }
            Firebase.auth.addAuthStateListener(listener)
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }

    override val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override suspend fun sendOtp(
        activity: Activity,
        phoneNo: String,
        onSuccessFul: () -> Unit,
        onFailure: () -> Unit
    ) {
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNo)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout duration
            .setActivity(activity)                 // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-retrieval or instant verification succeeded
                    signInWithPhoneAuthCredential(activity, credential, onSuccessFul, onFailure)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // Verification failed
                    Log.w(TAG, "Verification failed", e)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // Code sent to the user's phone
                    this@AccountServiceImpl.verificationId = verificationId
                }
            }
            )           // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun createCredentialAndSignIn(
        activity: Activity,
        code: String,
        onSuccessFul: () -> Unit,
        onFailure: () -> Unit
    ) {
        verificationId?.let {
            val credential = PhoneAuthProvider.getCredential(it, code)
            signInWithPhoneAuthCredential(activity, credential, onSuccessFul, onFailure)
        }
    }

    private fun signInWithPhoneAuthCredential(
        activity: Activity,
        credential: PhoneAuthCredential,
        onSuccessFul: () -> Unit,
        onFailure: () -> Unit
    ) {
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign-in successful
                    onSuccessFul.invoke()
                } else {
                    // Sign-in failed
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        onFailure.invoke()
                    }
                }
            }
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }


    companion object {
        const val TAG = "AccountServiceImpl"
    }
}