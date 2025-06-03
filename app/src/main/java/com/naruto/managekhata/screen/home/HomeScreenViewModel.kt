package com.naruto.managekhata.screen.home

import android.util.Log
import com.naruto.managekhata.MainViewModel
import com.naruto.managekhata.model.Customer
import com.naruto.managekhata.navigation.NavigationGraphComponent
import com.naruto.managekhata.service.AccountService
import com.naruto.managekhata.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService):MainViewModel() {
    val customerFlow = MutableStateFlow<List<Customer>>(emptyList())

    private val _deleteIdsFlow = MutableStateFlow<MutableSet<String>>(mutableSetOf())
    val deleteIdsFlow = _deleteIdsFlow

    fun updateDeleteIdsFlow(id:String){
        _deleteIdsFlow.value = _deleteIdsFlow.value.toMutableSet().apply {
            if (this.contains(id)) remove(id) else add(id)
        }.toMutableSet()
    }

    fun initialize(restartApp: (NavigationGraphComponent) -> Unit) {
        launchCatching {
            accountService.currentUser.collect { user ->
                if (user == null) restartApp(NavigationGraphComponent.NavSplashScreen)
            }
        }
    }

    fun logout() = launchCatching { accountService.signOut() }

    fun createCustomer(customer: Customer){
        launchCatching {
            storageService.createCustomer(customer)
        }
    }

    fun addCustomersListener(){
        launchCatching {
            storageService.addCustomerListListener {
                Log.i(TAG, "addInvoiceListener - $it")
                customerFlow.value = it
            }
        }
    }

    fun deleteCustomer(customersId: List<String>) {
        launchCatching {
            customersId.forEach { launch { storageService.deleteCustomer(it) } }
        }
    }

    fun removeListener(){
        storageService.removeCustomerListListener()
    }
    companion object {
        const val TAG = "HomeScreenViewModel"
    }
}