package com.naruto.managekhata.screen.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naruto.managekhata.Formatter.toBigString
import com.naruto.managekhata.model.Customer
import com.naruto.managekhata.navigation.NavigationGraphComponent
import com.naruto.managekhata.ui.elements.TwoInputTextDialog

private const val TAG = "HomeScreen"

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ViewModelConstructorInComposable", "CoroutineCreationDuringComposition")
@Composable
fun HomeScreen(
    navigate: (NavigationGraphComponent) -> Unit,
    restartApp: (NavigationGraphComponent) -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
){
    val customers by viewModel.customerFlow.collectAsState()
    var showAddCustomerDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.initialize(restartApp)
    }

    DisposableEffect(Unit) {
        Log.i(TAG, "DisposableEffect")
        viewModel.addCustomersListener()
        onDispose {
            Log.i(TAG, "DisposableEffect- onDispose")
            viewModel.removeListener()
        }
    }

    Log.i(TAG, "DisposableEffect- Handle Back pressed")
    HandleBackPressed(showAddCustomerDialog) {
        showAddCustomerDialog = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorScheme.primary,
                titleContentColor = colorScheme.onPrimary,
                actionIconContentColor = colorScheme.onPrimary
                ),
                title = {
                    Text("Manage Khata")
                },
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Log Out")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddCustomerDialog=true }) {
                Icon(Icons.Default.Add, contentDescription = "Add New Customer")
            }
        }
    ) { paddingValues ->
        Log.i(TAG, "recomposing invoice list")
        if (showAddCustomerDialog){
            TwoInputTextDialog(
                title = "Add Customer",
                onConfirm = { name, contactInfo ->
                    showAddCustomerDialog = false
                    viewModel.createCustomer(Customer(
                        customerName = name,
                        contactInfo = contactInfo
                    ))
                }
            ) {
                showAddCustomerDialog = false
            }
        }
        CustomerListView(
            customers,
            paddingValues,
            navigate
        )
    }
}

@Composable
private fun HandleBackPressed(isDeleting: Boolean, onBackPressed: ()->Unit){
    Log.i(TAG, "HandleBackPressed - $isDeleting")
    BackHandler(enabled = isDeleting) {
        Log.i(TAG, "HandleBackPressed - executing")
        onBackPressed()
    }
}

//@Composable
//fun DeleteActionMenu(isDeletePossible: Boolean, onConfirmDelete:()->Unit, stopDelete: ()->Unit){
//    IconButton(enabled = isDeletePossible, onClick = { onConfirmDelete() }) {
//        Icon(
//            imageVector = Icons.Filled.Delete,
//            contentDescription = "Delete"
//        )
//    }
//    IconButton(onClick = { stopDelete() }) {
//        Icon(
//            imageVector = Icons.Filled.Close,
//            contentDescription = "Cross"
//        )
//    }
//}

@Composable
fun MainActionMenu(
    viewModel: HomeScreenViewModel,
    showMenu: Boolean,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    onDeleteRequest: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "Profile"
        )
    }

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { Text("Delete Customer") },
            onClick = {
                onDismiss()
                onDeleteRequest()
            }
        )
        DropdownMenuItem(
            text = { Text("Logout") },
            onClick = {
                onDismiss()
                viewModel.logout()
            }
        )
    }
}


@Composable
fun CustomerListView(
    items: List<Customer>,
    paddingValues: PaddingValues,
    navigate: (NavigationGraphComponent) -> Unit
) {
    Log.i(TAG, "InvoiceListView - recomposing")
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = PaddingValues(top = paddingValues.calculateTopPadding()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items.size) { ind ->
            CustomerCard(modifier = Modifier.clickable {
                items[ind].customerId?.let{
                    navigate(NavigationGraphComponent.NavInvoiceListScreen(it, customerName = items[ind].customerName))
                }
            },items[ind])
        }
    }
}

@Composable
private fun CustomerCard(
    modifier: Modifier = Modifier,
    customer: Customer,
) {
    OutlinedCard(modifier = modifier
        .wrapContentHeight()
        .fillMaxWidth()
    ) {
        Log.i(TAG, "customer - $customer")
        Row(modifier = Modifier.padding(16.dp, 16.dp)) {
            Column {
                Text(customer.customerName,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Spacer(modifier=Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text("Total Due (Rs.)", modifier = Modifier.padding(bottom = 4.dp))
                Text(customer.totalAmount.toBigString())
            }
        }
    }
}

//@Preview
//@Composable
//private fun KhataEntryPreview(){
//    val DEFAULT_INVOICE = Invoice(
//        name = "Name",
//        invoiceAmount = 0.0,
//        invoiceDate = System.currentTimeMillis(),
//        dueAmount = 0.0,
//        dueDate = 0,
//        interestPercentage = 0.0,
//        interestAmount = 0.0
//    )
//    InvoiceCard(Modifier, invoice = DEFAULT_INVOICE, true, true, {})
//}


@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen({ }, {} )
}

//@OptIn(DelicateCoroutinesApi::class)
//suspend fun main(){
//    val set = MutableStateFlow<MutableSet<String>>(mutableSetOf())
//    set.collectLatest {
//        println(it)
//    }
//    delay(2000)
//    set.value.add("Hello")
//    delay(5000)
//}