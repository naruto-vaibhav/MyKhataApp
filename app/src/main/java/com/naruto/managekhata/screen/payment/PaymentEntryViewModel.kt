package com.naruto.managekhata.screen.payment

import com.naruto.managekhata.MainViewModel
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentEntryViewModel @Inject constructor(private val storageService: StorageService): MainViewModel() {
    fun createInvoice(invoice: Invoice){
        launchCatching {
            storageService.createInvoice(invoice) {}
        }
    }
}