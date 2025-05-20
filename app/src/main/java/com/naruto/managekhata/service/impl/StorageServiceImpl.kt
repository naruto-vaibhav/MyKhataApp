package com.naruto.managekhata.service.impl

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.service.AccountService
import com.naruto.managekhata.service.StorageService
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(private val auth: AccountService) :StorageService {
    override suspend fun createInvoice(invoice: Invoice, onSuccess: () -> Unit) {
        Firebase.firestore.collection(USERS_COLLECTION).document(auth.currentUserId).collection(
            INVOICES_COLLECTION).add(invoice).await()
    }

    companion object {
        private const val USERS_COLLECTION = "user"
        private const val INVOICES_COLLECTION = "invoices"
    }
}