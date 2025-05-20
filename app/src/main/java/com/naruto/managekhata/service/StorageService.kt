package com.naruto.managekhata.service

import com.naruto.managekhata.model.Invoice

interface StorageService {
    suspend fun createInvoice(invoice: Invoice, onSuccess: () -> Unit)
}