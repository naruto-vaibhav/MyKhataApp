package com.naruto.managekhata.screen.invoice

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.naruto.managekhata.MainViewModel
import com.naruto.managekhata.model.Customer
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.screen.depth.InvoiceDetailViewModel
import com.naruto.managekhata.screen.depth.InvoiceDetailViewModel.Companion
import com.naruto.managekhata.service.AccountService
import com.naruto.managekhata.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceListScreenViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService):MainViewModel() {


    private val _customer = mutableStateOf(DEFAULT_CUSTOMER)
    val customer: State<Customer> = _customer

    val invoiceFlow = MutableStateFlow<List<Invoice>>(emptyList())

    private val _deleteIdsFlow = MutableStateFlow<MutableSet<String>>(mutableSetOf())
    val deleteIdsFlow = _deleteIdsFlow



    fun deleteCustomer(customerId: String){
        TODO("Implement Delete Customer")
    }

    fun updateDeleteIdsFlow(id:String){
        _deleteIdsFlow.value = _deleteIdsFlow.value.toMutableSet().apply {
            if (this.contains(id)) remove(id) else add(id)
        }.toMutableSet()
    }

    fun addCustomerDetailListener(customerId: String) {
        launchCatching {
            storageService.addCustomerDetailListener(customerId) {
                Log.i(TAG, "addCustomerDetailListener, customer - $it")
                _customer.value = it
            }
        }
    }


    fun addInvoiceListener(customerId: String){
        launchCatching {
            storageService.addInvoiceListener(customerId) {
                Log.i(TAG, "addInvoiceListener, invoice - $it")
                invoiceFlow.value = it
                updateCustomer(it)
            }
        }
    }

    private fun updateCustomer(invoiceList: List<Invoice>){
        Log.i(TAG, "updateCustomer - $invoiceList")
        val customer = _customer.value.copy(
            totalAmount = invoiceList.sumOf { it.dueAmount }
        )
        launchCatching {
            storageService.createCustomer(customer)
        }
    }

    fun deleteInvoices(customerId: String, invoicesId: List<String>) {
        launchCatching {
            invoicesId.forEach { launch { storageService.deleteInvoice(customerId, it) } }
        }
    }

    fun removeCustomerDetailListener(){
        storageService.removeCustomerDetailListener()
    }

    fun removeListener(){
        storageService.removeInvoiceListener()
    }
    companion object {
        const val TAG = "InvoiceListScreenViewModel"
        private val DEFAULT_CUSTOMER = Customer()
    }
}