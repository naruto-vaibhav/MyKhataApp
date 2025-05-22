package com.naruto.managekhata.navigation

import kotlinx.serialization.Serializable

sealed class NavigationGraphComponent {

    @Serializable
    data object NavSplashScreen : NavigationGraphComponent()

    @Serializable
    data object NavLoginScreen : NavigationGraphComponent()

    @Serializable
    data object NavHomeScreen : NavigationGraphComponent()

    @Serializable
    data object NavNewInvoiceScreen : NavigationGraphComponent()

    @Serializable
    data class NavNewPaymentScreen(val invoiceId: String, val interestPercent: Double, val dueDate: Long) : NavigationGraphComponent()

    @Serializable
    data class NavInvoiceDetailScreen(val invoiceId: String) : NavigationGraphComponent()
}