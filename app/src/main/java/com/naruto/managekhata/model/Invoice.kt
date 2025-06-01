package com.naruto.managekhata.model

data class Invoice(
    val id: String? = null,
    val name: String = "",
    val invoiceAmount: Double = 0.0,
    val invoiceDate: Long = 0L,
    val dueAmount: Double = 0.0,
    val dueDate: Long = 0L,
    val interestPercentage: Double = 0.0,
    val interestDuration: String = "Daily",
    val interestPerDay: Double = 0.0,
    val interestAmount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val remarks: String = ""
    )