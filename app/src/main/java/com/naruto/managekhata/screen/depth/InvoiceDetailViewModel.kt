package com.naruto.managekhata.screen.depth

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.naruto.managekhata.MainViewModel
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.model.Payment
import com.naruto.managekhata.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InvoiceDetailViewModel @Inject constructor(private val storageService: StorageService): MainViewModel() {

    private val _invoice = mutableStateOf(DEFAULT_INVOICE)
    val invoice: State<Invoice> = _invoice

    private val _payments = mutableStateOf<List<Payment>>(emptyList())
    val payment:State<List<Payment>> = _payments

    fun addInvoiceDetailListener(invoiceId: String) {
        launchCatching {
            storageService.addInvoiceDetailListener(invoiceId) {
                Log.i(TAG, "getInvoiceDetail - $it")
                _invoice.value = it
            }
        }
    }

    fun addPaymentListener(invoiceId: String) {
        launchCatching {
            storageService.addPaymentListener(invoiceId) {
                _payments.value = it
                val invoice = _invoice.value.copy(
                    dueAmount = _invoice.value.invoiceAmount - it.sumOf { payment-> payment.amount },
                    interestAmount = it.sumOf { payment-> payment.interest }
                )
                updateInvoice(invoice)
            }
        }
    }

    private fun updateInvoice(invoice: Invoice){
        launchCatching {
            storageService.updateInvoice(invoice)
            Log.i(TAG, "getInvoiceDetail - $invoice")
            _invoice.value = invoice
        }
    }

    fun removeListener(){
        storageService.removePaymentListener()
    }

    companion object {
        private const val TAG ="InvoiceDetailViewModel"
        private val DEFAULT_INVOICE = Invoice(
            name = "Name",
            invoiceAmount = 0.0,
            invoiceDate = System.currentTimeMillis(),
            dueAmount = 0.0,
            dueDate = 0,
            interestPercentage = 0.0,
            interestAmount = 0.0
        )
    }
}