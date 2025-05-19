package com.naruto.managekhata.screen

import android.app.Activity
import com.naruto.managekhata.MainViewModel
import com.naruto.managekhata.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val accountService: AccountService
): MainViewModel() {
    fun onVerification(activity: Activity, phoneNo: String, onSuccess:()->Unit, onFailure: ()-> Unit){
        launchCatching {
            accountService.sendOtp(activity, phoneNo, onSuccess, onFailure)
        }
    }

    fun onSignInClick(activity: Activity, otp: String, onSuccess:()->Unit, onFailure: ()-> Unit){
        launchCatching {
            accountService.createCredentialAndSignIn(activity, otp, onSuccess, onFailure)
        }
    }

}