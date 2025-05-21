package com.naruto.managekhata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.firebase.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.naruto.managekhata.navigation.NavigationGraphComponent
import com.naruto.managekhata.navigation.navGraph
import com.naruto.managekhata.ui.theme.ManageKhataTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        configureFirebaseServices()
//        Firebase.firestore.useEmulator(LOCALHOST, FIRESTORE_PORT)
        setContent {
            ManageKhataTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val appState = rememberAppState()
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        NavHost(
                            navController = appState.navController,
                            startDestination = NavigationGraphComponent.NavSplashScreen,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            navGraph(appState)
                        }
                    }
                }

            }
        }
    }
    private fun configureFirebaseServices() {
        if (BuildConfig.DEBUG) {
//            Firebase.auth.useEmulator(LOCALHOST, AUTH_PORT)
//            Firebase.firestore.useEmulator(LOCALHOST, FIRESTORE_PORT)
        }
    }

    companion object {
        const val LOCALHOST = "10.0.2.2"
        const val AUTH_PORT = 9099
        const val FIRESTORE_PORT = 8080
    }
}

@Composable
fun rememberAppState(navController: NavHostController = rememberNavController()) =
    remember(navController) {
        AppState(navController)
    }
