package com.naruto.managekhata.service

import com.naruto.managekhata.model.Customer
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.model.Payment

interface StorageService {
    suspend fun createCustomer(customer: Customer)
    suspend fun updateCustomer(customer: Customer)
    suspend fun deleteCustomer(customerId: String)
    suspend fun addCustomerListListener(onCustomerListFetch: (List<Customer>) -> Unit)
    suspend fun addCustomerDetailListener(customerId: String, onCustomerListFetch: (Customer) -> Unit)
    suspend fun createInvoice(customerId: String, invoice: Invoice)
    suspend fun deleteInvoice(customerId: String, invoiceId: String)
    suspend fun createPayment(customerId: String, invoiceId: String, payment: Payment)
    suspend fun getPayment(customerId: String, invoiceId: String, paymentId:String): Payment?
    suspend fun deletePayment(customerId: String, invoiceId:String, paymentId: String)
    suspend fun updateInvoice(customerId: String, invoice: Invoice)
    suspend fun addInvoiceListener(customerId: String, onInvoiceFetch: (List<Invoice>) -> Unit)
    suspend fun addInvoiceDetailListener(customerId: String, invoiceId: String, onInvoiceDetailFetch: (Invoice) -> Unit)
    suspend fun addPaymentListener(customerId: String, invoiceId: String, onPaymentsFetch: (List<Payment>) -> Unit)
    suspend fun getInvoiceDetail(customerId: String,invoiceId: String): Invoice?
    fun removeCustomerListListener()
    fun removeCustomerDetailListener()
    fun removeInvoiceListener()
    fun removeInvoiceDetailListener()
    fun removePaymentListener()
}
