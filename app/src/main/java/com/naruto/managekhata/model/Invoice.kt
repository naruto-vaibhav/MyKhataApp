package com.naruto.managekhata.model

data class Invoice(
    val id: String,
    val name: String,
    val invoiceAmount: Double,
    val invoiceDate: Long,
    val dueAmount: Double,
    val dueDate: Long,
    val interest: Double
    )