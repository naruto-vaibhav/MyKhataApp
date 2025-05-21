package com.naruto.managekhata

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp class ManageKhataApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val LOCALHOST = "10.0.2.2"
        val AUTH_PORT = 9099
        val FIRESTORE_PORT = 8080
        Firebase.firestore.useEmulator(LOCALHOST, FIRESTORE_PORT)
//        FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080)
    }

}