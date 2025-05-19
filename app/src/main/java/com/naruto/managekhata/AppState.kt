package com.naruto.managekhata

import androidx.compose.runtime.Stable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import com.naruto.managekhata.navigation.NavigationGraphComponent

@Stable
class AppState(val navController: NavHostController) {
    fun popUp() {
        navController.popBackStack()
    }

    fun navigate(route: NavigationGraphComponent) {
        navController.navigate(route) { launchSingleTop = true }
    }

    fun navigateAndPopUp(route: NavigationGraphComponent, popUp: NavigationGraphComponent) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(popUp) { inclusive = true }
        }
    }

    fun clearAndNavigate(route: NavigationGraphComponent) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(0) { inclusive = true }
        }
    }
}