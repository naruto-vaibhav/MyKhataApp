package com.naruto.managekhata.service.impl

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.model.Payment
import com.naruto.managekhata.service.AccountService
import com.naruto.managekhata.service.StorageService
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl @Inject constructor(private val auth: AccountService) : StorageService {
    private var listenerRegistration: ListenerRegistration? = null
    private var invoiceDetailRegistration: ListenerRegistration? = null
    private var paymentListenerRegistration:ListenerRegistration? = null
    override suspend fun createInvoice(invoice: Invoice) {
        Firebase.firestore.collection(USERS_COLLECTION).document(auth.currentUserId).collection(
            INVOICES_COLLECTION
        ).add(invoice).await()
    }

    override suspend fun deleteInvoice(invoiceId: String) {
        Firebase.firestore.collection(USERS_COLLECTION).document(auth.currentUserId).collection(
            INVOICES_COLLECTION
        ).document(invoiceId).delete().await()
    }

    override suspend fun createPayment(invoiceId: String, payment: Payment) {
        if (payment.id == null){
            Firebase.firestore.collection(USERS_COLLECTION).document(auth.currentUserId).collection(
                INVOICES_COLLECTION
            ).document(invoiceId).collection(PAYMENTS_COLLECTION).add(payment)
        }
        else{
            Firebase.firestore.collection(USERS_COLLECTION).document(auth.currentUserId).collection(
                INVOICES_COLLECTION
            ).document(invoiceId).collection(PAYMENTS_COLLECTION).document(payment.id).set(payment).await()
        }
    }

    override suspend fun getPayment(invoiceId: String, paymentId: String): Payment? =
        Firebase.firestore.collection(USERS_COLLECTION).document(auth.currentUserId).collection(
            INVOICES_COLLECTION
        ).document(invoiceId).collection(PAYMENTS_COLLECTION).document(paymentId).get().await().toObject(Payment::class.java)

    override suspend fun deletePayment(invoiceId: String, paymentId: String) {
        Firebase.firestore.collection(USERS_COLLECTION).document(auth.currentUserId).collection(
            INVOICES_COLLECTION
        ).document(invoiceId).collection(PAYMENTS_COLLECTION).document(paymentId).delete().await()
    }

    override suspend fun updateInvoice(invoice: Invoice) {
        invoice.id?.let {
            Firebase.firestore.collection(USERS_COLLECTION).document(auth.currentUserId).collection(
                INVOICES_COLLECTION
            ).document(it).set(invoice).await()
        }
    }

    override suspend fun getInvoiceDetail(invoiceId: String): Invoice? = Firebase.firestore.collection(USERS_COLLECTION).document(auth.currentUserId)
        .collection(INVOICES_COLLECTION).document(invoiceId).get().await().toObject(Invoice::class.java)

    override suspend fun addInvoiceListener(onInvoiceFetch: (List<Invoice>) -> Unit) {
        Log.i(TAG, "addInvoiceListener, user = ${auth.currentUserId}")
        listenerRegistration =
            Firebase.firestore.collection(USERS_COLLECTION).document(auth.currentUserId)
                .collection(INVOICES_COLLECTION).orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    Log.i(TAG, "addInvoiceListener - $value, $error")
                    if (error != null || value == null) return@addSnapshotListener
                    val invoices = value.documents.mapNotNull { doc ->
                        doc.toObject(Invoice::class.java)?.copy(id = doc.id)
                    }
                    onInvoiceFetch(invoices)
                }
    }

    override suspend fun addInvoiceDetailListener(invoiceId: String, onInvoiceDetailFetch: (Invoice) -> Unit) {
        Log.i(TAG, "addInvoiceListener, user = ${auth.currentUserId}")
        invoiceDetailRegistration =
            Firebase.firestore.collection(USERS_COLLECTION).document(auth.currentUserId)
                .collection(INVOICES_COLLECTION).document(invoiceId)
                .addSnapshotListener { value, error ->
                    Log.i(TAG, "addInvoiceDetailListener - $value, $error")
                    if (error != null || value == null) return@addSnapshotListener
                    value.toObject(Invoice::class.java)?.let(onInvoiceDetailFetch)
                }
    }

    override suspend fun addPaymentListener(invoiceId: String, onPaymentsFetch: (List<Payment>) -> Unit) {
        paymentListenerRegistration =
            Firebase.firestore.collection(USERS_COLLECTION).document(auth.currentUserId)
                .collection(INVOICES_COLLECTION).document(invoiceId).collection(PAYMENTS_COLLECTION).orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    Log.i(TAG, "addInvoiceListener - $value, $error")
                    if (error != null || value == null) return@addSnapshotListener
                    val payments = value.documents.mapNotNull { doc ->
                        doc.toObject(Payment::class.java)?.copy(id = doc.id)
                    }
                    onPaymentsFetch(payments)
                }
    }

    override fun removeInvoiceListener() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }

    override fun removeInvoiceDetailListener() {
        invoiceDetailRegistration?.remove()
        invoiceDetailRegistration = null
    }

    override fun removePaymentListener() {
        paymentListenerRegistration?.remove()
        paymentListenerRegistration = null
    }

    companion object {
        private const val TAG = "StorageServiceImpl"
        private const val USERS_COLLECTION = "user"
        private const val INVOICES_COLLECTION = "invoices"
        private const val PAYMENTS_COLLECTION = "payments"
    }
}