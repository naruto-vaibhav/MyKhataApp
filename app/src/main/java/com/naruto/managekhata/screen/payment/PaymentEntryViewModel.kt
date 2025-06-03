package com.naruto.managekhata.screen.payment

import com.naruto.managekhata.MainViewModel
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.model.Payment
import com.naruto.managekhata.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentEntryViewModel @Inject constructor(private val storageService: StorageService): MainViewModel() {
    fun createInvoice(customerId:String,invoice: Invoice, onSuccess: ()->Unit){
        launchCatching {
            storageService.createInvoice(customerId,invoice)
            onSuccess.invoke()
        }
    }

    fun createPayment(customerId: String, invoiceId: String, payment: Payment,  onSuccess: ()->Unit){
        launchCatching {
            storageService.createPayment(customerId, invoiceId, payment)
            onSuccess.invoke()
        }
    }

    fun getPayment(customerId: String, invoiceId:String, paymentId: String,  onSuccess: (Payment)->Unit){
        launchCatching {
            storageService.getPayment(customerId, invoiceId, paymentId)?.let(onSuccess)
        }
    }

    fun getInvoice(customerId: String, invoiceId: String, onSuccess: (Invoice)->Unit){
        launchCatching {
            storageService.getInvoiceDetail(customerId, invoiceId)?.let(onSuccess)
        }
    }
}