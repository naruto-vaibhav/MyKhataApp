package com.naruto.managekhata.screen.payment

import com.naruto.managekhata.MainViewModel
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.model.Payment
import com.naruto.managekhata.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentEntryViewModel @Inject constructor(private val storageService: StorageService): MainViewModel() {
    fun createInvoice(invoice: Invoice, onSuccess: ()->Unit){
        launchCatching {
            storageService.createInvoice(invoice)
            onSuccess.invoke()
        }
    }
    fun createPayment(invoiceId: String, payment: Payment,  onSuccess: ()->Unit){
        launchCatching {
            storageService.createPayment(invoiceId, payment)
            onSuccess.invoke()
        }
    }

    fun getPayment(invoiceId:String, paymentId: String,  onSuccess: (Payment)->Unit){
        launchCatching {
            storageService.getPayment(invoiceId, paymentId)?.let(onSuccess)
        }
    }

    fun getInvoice(invoiceId: String, onSuccess: (Invoice)->Unit){
        launchCatching {
            storageService.getInvoiceDetail(invoiceId)?.let(onSuccess)
        }
    }
}