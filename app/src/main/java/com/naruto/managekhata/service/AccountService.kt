package com.naruto.managekhata.service

import android.app.Activity
import com.naruto.managekhata.User
import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentUser: Flow<User?>
    val currentUserId: String
    fun hasUser(): Boolean
    suspend fun sendOtp(
        activity: Activity,
        phoneNo: String,
        onSuccessFul: () -> Unit,
        onFailure: () -> Unit
    )
    fun createCredentialAndSignIn(
        activity: Activity,
        code: String,
        onSuccessFul: () -> Unit,
        onFailure: () -> Unit
    )
    suspend fun signOut()
}