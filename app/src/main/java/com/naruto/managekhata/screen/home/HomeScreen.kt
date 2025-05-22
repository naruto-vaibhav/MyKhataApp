package com.naruto.managekhata.screen.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.navigation.NavigationGraphComponent
import com.naruto.managekhata.ui.theme.DateFormatter

const val TAG = "HomeScreen"

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun HomeScreen(
    navigate: (NavigationGraphComponent) -> Unit,
    restartApp: (NavigationGraphComponent) -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
){
    val invoices by viewModel.invoiceFlow.collectAsState()

    LaunchedEffect(Unit) { viewModel.initialize(restartApp) }

    DisposableEffect(Unit) {
        Log.i(TAG, "HomeScreen")
        viewModel.addInvoiceListener()
        onDispose {
            Log.i(TAG, "HomeScreen- onDispose")
            viewModel.removeListener()
        }
    }

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = { Text("ManageKhata") },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Profile"
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                showMenu = false
                                viewModel.logout()
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navigate(NavigationGraphComponent.NavNewInvoiceScreen) }) {
                Icon(Icons.Default.Add, contentDescription = "Add New Invoice")
            }
        }
    ) { paddingValues ->
//        val dummyList = List(20) {
//            Invoice(
//                id = "abcd",
//                name = "Vishal",
//                invoiceAmount = 1234.0,
//                invoiceDate = 1234,
//                dueAmount = 2534.0,
//                dueDate = 1234,
//                interest = 2.0
//            )
//        }

        InvoiceListView(invoices, paddingValues, navigate)
    }
}

@Composable
fun InvoiceListView(items: List<Invoice>, paddingValues: PaddingValues, navigate: (NavigationGraphComponent) -> Unit) {
    LazyColumn (
        modifier = Modifier.fillMaxSize().padding(8.dp),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items.size) { ind ->
            InvoiceCard(modifier = Modifier.clickable {
                items[ind].id?.let{
                    navigate(NavigationGraphComponent.NavInvoiceDetailScreen(it))
                }
            },items[ind])
        }
    }
}

@Composable
private fun InvoiceCard(modifier: Modifier = Modifier, invoice: Invoice){
    OutlinedCard(modifier = modifier.wrapContentHeight().fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp, 16.dp)) {
            Column(modifier = Modifier.weight(0.75f)) {
                Text(invoice.name,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(DateFormatter.toFormatDate(invoice.invoiceDate), fontSize = 12.sp)
            }
            Column(modifier = Modifier.weight(0.25f),
                horizontalAlignment = Alignment.End) {
                Text("Due (Rs.)", modifier = Modifier.padding(bottom = 4.dp))
                Text(invoice.dueAmount.toString())
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
//    InvoiceCard(invoice = DEFAULT_INVOICE)
//}


@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen({ _ -> Unit}, { _ -> Unit})
}