package com.naruto.managekhata

enum class CustomInterestDuration(val value: String, val days: Int) {
    DAILY("Daily",1),
    MONTHLY("Monthly", 30),
    QUARTERLY("Quarterly", 90),
    YEARLY("Yearly", 365)
}