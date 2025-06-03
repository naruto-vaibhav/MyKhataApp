package com.naruto.managekhata.screen.depth

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.naruto.managekhata.MainViewModel
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.model.Payment
import com.naruto.managekhata.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceDetailViewModel @Inject constructor(private val storageService: StorageService): MainViewModel() {

    private val _invoice = mutableStateOf(DEFAULT_INVOICE)
    val invoice: State<Invoice> = _invoice

    private val _payments = mutableStateOf<List<Payment>>(emptyList())
    val payment:State<List<Payment>> = _payments

    private val _isLoading = mutableStateOf(true)
    val isLoading = _isLoading


    private val _deleteIdsFlow = MutableStateFlow<MutableSet<String>>(mutableSetOf())
    val deleteIdsFlow = _deleteIdsFlow

    fun updateDeleteIdsFlow(id: String) {
        _deleteIdsFlow.value = _deleteIdsFlow.value.toMutableSet().apply {
            if (this.contains(id)) remove(id) else add(id)
        }.toMutableSet()
    }

    fun addInvoiceDetailListener(customerId: String,invoiceId: String) {
        launchCatching {
            storageService.addInvoiceDetailListener(customerId, invoiceId) {
                Log.i(TAG, "addInvoiceDetailListener - $it")
                _invoice.value = it
                _isLoading.value = false
            }
        }
    }

    fun deletePayments(customerId: String,invoiceId: String, paymentsId: List<String>) {
        launchCatching {
            paymentsId.forEach { launch { storageService.deletePayment(customerId, invoiceId, it) } }
        }
    }

    fun addPaymentListener(customerId:String, invoiceId: String) {
        launchCatching {
            storageService.addPaymentListener(customerId, invoiceId) {
                _payments.value = it
                val invoice = _invoice.value.copy(
                    dueAmount = _invoice.value.invoiceAmount - it.sumOf { payment-> payment.amount },
                    interestAmount = it.sumOf { payment-> payment.interest }
                )
                updateInvoice(customerId, invoice)
            }
        }
    }

    private fun updateInvoice(customerId: String, invoice: Invoice){
        launchCatching {
            storageService.updateInvoice(customerId, invoice)
            Log.i(TAG, "updateInvoice - $invoice")
            _invoice.value = invoice
        }
    }

    fun removeListener(){
        storageService.removePaymentListener()
    }

    companion object {
        private const val TAG ="InvoiceDetailViewModel"
        private val DEFAULT_INVOICE = Invoice(
            invoiceAmount = 0.0,
            invoiceDate = System.currentTimeMillis(),
            dueAmount = 0.0,
            dueDate = 0,
            interestPercentage = 0.0,
            interestAmount = 0.0,
            remarks = "Remarks"
        )
    }
}