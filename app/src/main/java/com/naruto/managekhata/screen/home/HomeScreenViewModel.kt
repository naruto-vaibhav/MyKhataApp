package com.naruto.managekhata.screen.home

import android.util.Log
import com.naruto.managekhata.MainViewModel
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.navigation.NavigationGraphComponent
import com.naruto.managekhata.service.AccountService
import com.naruto.managekhata.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService):MainViewModel() {
    val invoiceFlow = MutableStateFlow<List<Invoice>>(emptyList())

    fun initialize(restartApp: (NavigationGraphComponent) -> Unit) {
        launchCatching {
            accountService.currentUser.collect { user ->
                if (user == null) restartApp(NavigationGraphComponent.NavSplashScreen)
            }
        }
    }

    fun logout() = launchCatching { accountService.signOut() }

    fun addInvoiceListener(){
        launchCatching {
            storageService.addInvoiceListener {
                Log.i(TAG, "addInvoiceListener - $it")
                invoiceFlow.value = it
            }
        }
    }

    fun removeListener(){
        storageService.removeInvoiceListener()
    }
    companion object {
        const val TAG = "HomeScreenViewModel"
    }
}