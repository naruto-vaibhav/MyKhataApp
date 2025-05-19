package com.naruto.managekhata.model

data class Payment(
    val id: String,
    val amount: Double,
    val interest: Double,
    val date: Long
)
