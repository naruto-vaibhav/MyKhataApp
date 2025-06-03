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
    data class NavInvoiceListScreen(val customerId: String, val customerName: String) : NavigationGraphComponent()

    @Serializable
    data class NavNewInvoiceScreen(val customerId: String, val customerName: String) : NavigationGraphComponent()

    @Serializable
    data class NavInvoiceDetailScreen(val customerId: String, val customerName: String, val invoiceId: String) : NavigationGraphComponent()

    @Serializable
    data class NavNewPaymentScreen(val customerId: String, val customerName: String, val invoiceId: String, val interestPercent: Double, val dueDate: Long, val dueAmount: Double) : NavigationGraphComponent()

    @Serializable
    data class EditPaymentScreen(val customerId: String, val customerName: String, val invoiceId: String, val paymentId:String, val interestPercent: Double, val dueDate: Long, val dueAmount: Double) : NavigationGraphComponent()
}