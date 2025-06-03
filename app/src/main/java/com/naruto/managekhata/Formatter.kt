package com.naruto.managekhata

import java.math.BigDecimal

object Formatter {
    fun Double.toBigString(): String = BigDecimal(this.toString()).toPlainString()
}