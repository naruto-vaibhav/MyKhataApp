package com.naruto.managekhata

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp class ManageKhataApp : Application() {
    override fun onCreate() {
        super.onCreate()
        configureFirebaseServices(false)
    }
    private fun configureFirebaseServices(isTest: Boolean) {
        if (isTest) {
            Firebase.auth.useEmulator(LOCALHOST, AUTH_PORT)
            Firebase.firestore.useEmulator(LOCALHOST, FIRESTORE_PORT)
        }
    }
    companion object {
        private const val LOCALHOST = "10.0.2.2"
        private const val AUTH_PORT = 9099
        private const val FIRESTORE_PORT = 8080
    }
}