package com.naruto.managekhata.service

import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.model.Payment

interface StorageService {
    suspend fun createInvoice(invoice: Invoice)
    suspend fun deleteInvoice(invoiceId: String)
    suspend fun createPayment(invoiceId: String, payment: Payment)
    suspend fun getPayment(invoiceId: String, paymentId:String): Payment?
    suspend fun deletePayment(invoiceId:String, paymentId: String)
    suspend fun updateInvoice(invoice: Invoice)
    suspend fun addInvoiceListener(onInvoiceFetch: (List<Invoice>) -> Unit)
    suspend fun addInvoiceDetailListener(invoiceId: String, onInvoiceDetailFetch: (Invoice) -> Unit)
    suspend fun addPaymentListener(invoiceId: String, onPaymentsFetch: (List<Payment>) -> Unit)
    suspend fun getInvoiceDetail(invoiceId: String): Invoice?
    fun removeInvoiceListener()
    fun removeInvoiceDetailListener()
    fun removePaymentListener()
}
