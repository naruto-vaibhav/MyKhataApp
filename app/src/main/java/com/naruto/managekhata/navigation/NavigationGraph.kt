package com.naruto.managekhata.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.naruto.managekhata.AppState
import com.naruto.managekhata.screen.LoginScreen
import com.naruto.managekhata.screen.depth.InvoiceDetailScreen
import com.naruto.managekhata.screen.home.HomeScreen
import com.naruto.managekhata.screen.launch.SplashScreen
import com.naruto.managekhata.screen.payment.PaymentEntry

fun NavGraphBuilder.navGraph(appState: AppState){
    composable<NavigationGraphComponent.NavHomeScreen> {
        HomeScreen(
            navigate = { route -> appState.navigate(route)},
            restartApp = { route -> appState.clearAndNavigate(route) }
        )
    }

    composable<NavigationGraphComponent.NavNewInvoiceScreen> {
        PaymentEntry(
            popUp = { appState.popUp() }
        )
    }

    composable<NavigationGraphComponent.NavInvoiceDetailScreen> {
        val arg = it.toRoute<NavigationGraphComponent.NavInvoiceDetailScreen>()
        InvoiceDetailScreen (
            invoiceId = arg.invoiceId,
            navigate = { route -> appState.navigate(route) }
        )
    }

    composable<NavigationGraphComponent.NavNewPaymentScreen> {
        val arg = it.toRoute<NavigationGraphComponent.NavNewPaymentScreen>()
        PaymentEntry(
            invoiceId = arg.invoiceId,
            interestPercent = arg.interestPercent,
            dueDate = arg.dueDate,
            dueAmount = arg.dueAmount,
            popUp = { appState.popUp() }
        )
    }

    composable<NavigationGraphComponent.NavLoginScreen> {
        LoginScreen(Modifier, navigateAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable<NavigationGraphComponent.NavSplashScreen> {
        SplashScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }
}