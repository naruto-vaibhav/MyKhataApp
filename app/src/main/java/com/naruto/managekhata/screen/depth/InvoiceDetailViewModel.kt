package com.naruto.managekhata.screen.depth

import com.naruto.managekhata.MainViewModel
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.model.Payment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InvoiceDetailViewModel @Inject constructor(): MainViewModel() {
    fun getInvoiceDetail(invoiceId: String) = Invoice(
        id = "abcd",
        name = "Vishal",
        invoiceAmount = 1234.0,
        invoiceDate = 1234,
        dueAmount = 2534.0,
        dueDate = 1234,
        interest = 2.0
    )

    fun getAllPayments(invoiceId: String) = List(10) {
        Payment(
            "1234",
            123.0,
            12.0,
            123456L
        )
    }
}