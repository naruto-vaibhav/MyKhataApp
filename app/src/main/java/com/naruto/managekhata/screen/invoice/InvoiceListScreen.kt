package com.naruto.managekhata.screen.invoice

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naruto.managekhata.Formatter.toBigString
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.navigation.NavigationGraphComponent
import com.naruto.managekhata.ui.theme.DateFormatter

private const val TAG = "InvoiceListScreen"

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ViewModelConstructorInComposable", "CoroutineCreationDuringComposition")
@Composable
fun InvoiceListScreen(
    customerId: String,
    customerName: String,
    navigate: (NavigationGraphComponent) -> Unit,
    popUp: () -> Unit,
    viewModel: InvoiceListScreenViewModel = hiltViewModel()
) {
    val invoices by viewModel.invoiceFlow.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var isDeleting by rememberSaveable { mutableStateOf(false) }
    val deleteInvoiceIds = viewModel.deleteIdsFlow.collectAsState()
    val customer = viewModel.customer

    DisposableEffect(Unit) {
        Log.i(TAG, "DisposableEffect")
        viewModel.addCustomerDetailListener(customerId)
        viewModel.addInvoiceListener(customerId)
        onDispose {
            Log.i(TAG, "DisposableEffect- onDispose")
            viewModel.removeCustomerDetailListener()
            viewModel.removeListener()
        }
    }

    Log.i(TAG, "HomeScreen- Handle Back pressed")
    HandleBackPressed(isDeleting) {
        isDeleting = false
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
                    if (isDeleting) {
                        Text("Select Invoices")
                    } else {
                        Text("Invoices of ${customer.value.customerName}")
                    }
                },
                actions = {
                    if (isDeleting) {
                        DeleteActionMenu(
                            deleteInvoiceIds.value.isNotEmpty(),
                            {
                                viewModel.deleteInvoices(
                                    customerId,
                                    deleteInvoiceIds.value.toList()
                                )
                                isDeleting = false
                            }) {
                            isDeleting = false
                        }
                    } else {
                        MainActionMenu(
                            showMenu,
                            onClick = { showMenu = !showMenu },
                            onDismiss = { showMenu = false },
                            onDeleteCustomerRequest = {
                                viewModel.deleteCustomer(customerId)
                                popUp()
                            },
                            onDeleteInvoiceRequest = { isDeleting = true }
                        )
                    }
                },

                )
        },
        bottomBar = {
            if (!isDeleting) {
                FloatingActionButton(
                    onClick = {
                        navigate(
                            NavigationGraphComponent.NavNewInvoiceScreen(
                                customerId,
                                customer.value.customerName
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text("Add New Invoice")
                }
            }
        }
    ) { paddingValues ->
        Log.i(TAG, "recomposing invoice list")
        InvoiceListView(
            customerId,
            customerName,
            invoices,
            deleteInvoiceIds.value,
            paddingValues,
            isDeleting,
            {
                Log.i(TAG, "checking - $it")
                viewModel.updateDeleteIdsFlow(it)
            },
            navigate
        )
    }
}

@Composable
private fun HandleBackPressed(isDeleting: Boolean, onBackPressed: () -> Unit) {
    Log.i(TAG, "HandleBackPressed - $isDeleting")
    BackHandler(enabled = isDeleting) {
        Log.i(TAG, "HandleBackPressed - executing")
        onBackPressed()
    }
}

@Composable
fun DeleteActionMenu(
    isDeletePossible: Boolean,
    onConfirmDelete: () -> Unit,
    stopDelete: () -> Unit
) {
    IconButton(enabled = isDeletePossible, onClick = { onConfirmDelete() }) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = "Delete"
        )
    }
    IconButton(onClick = { stopDelete() }) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Cross"
        )
    }
}

@Composable
fun MainActionMenu(
    showMenu: Boolean,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    onDeleteCustomerRequest: () -> Unit,
    onDeleteInvoiceRequest: () -> Unit
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
                onDeleteCustomerRequest()
            }
        )
        DropdownMenuItem(
            text = { Text("Delete Invoice") },
            onClick = {
                onDismiss()
                onDeleteInvoiceRequest()
            }
        )
    }
}


@Composable
fun InvoiceListView(
    customerId: String,
    customerName: String,
    items: List<Invoice>,
    itemsChecked: Set<String>,
    paddingValues: PaddingValues,
    isDeleting: Boolean,
    onToggleSwitch: (String) -> Unit,
    navigate: (NavigationGraphComponent) -> Unit
) {
    Log.i(TAG, "InvoiceListView - recomposing")
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = PaddingValues(top = paddingValues.calculateTopPadding()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items.size) { ind ->
            InvoiceCard(
                modifier = Modifier.clickable {
                    if (!isDeleting) {
                        items[ind].id?.let {
                            navigate(
                                NavigationGraphComponent.NavInvoiceDetailScreen(
                                    customerId,
                                    customerName,
                                    it
                                )
                            )
                        }
                    }
                },
                items[ind], isDeleting, itemsChecked.contains(items[ind].id),
                onToggleSwitch,
            )
        }
    }
}

@Composable
private fun InvoiceCard(
    modifier: Modifier = Modifier,
    invoice: Invoice,
    isDeleting: Boolean,
    isChecked: Boolean,
    onToggle: (String) -> Unit
) {
    OutlinedCard(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(if (isDeleting) Color.LightGray else Color.White)
    ) {
        Log.i(TAG, "$invoice - $isChecked")
        Row(modifier = Modifier.padding(16.dp, 16.dp)) {
            if (isDeleting) {
                Column {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            invoice.id?.let(onToggle)
                        }
                    )
                }
            }
            Column {
                Text(DateFormatter.toFormatDate(invoice.invoiceDate), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text("Due (Rs.)", modifier = Modifier.padding(bottom = 4.dp))
                Text(invoice.dueAmount.toBigString())
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


//@Preview
//@Composable
//private fun InvoiceListScreenPreview() {
//    InvoiceListScreen({ } )
//}

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