package com.naruto.managekhata.model

data class Customer(
    val customerId: String? = null,
    val customerName: String = "",
    val contactInfo: String = "",
    var totalAmount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
