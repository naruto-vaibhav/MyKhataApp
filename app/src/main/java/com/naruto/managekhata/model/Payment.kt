package com.naruto.managekhata.model

data class Payment(
    val id: String?= null,
    val amount: Double = 0.0,
    val interest: Double = 0.0,
    val date: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val remarks: String = ""
)
