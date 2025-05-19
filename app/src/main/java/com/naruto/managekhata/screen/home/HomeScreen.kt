package com.naruto.managekhata.screen.home

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.navigation.NavigationGraphComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigate: (NavigationGraphComponent) -> Unit,
){
    var khataList = remember {
        mutableStateListOf<Invoice>()
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
                                // Handle profile click
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navigate(NavigationGraphComponent.NavNewInvoiceScreen) }, modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Add New Invoice")
            }
        }
    ) { paddingValues ->
        val dummyList = List(20) {
            Invoice(
                id = "abcd",
                name = "Vishal",
                invoiceAmount = 1234.0,
                dueAmount = 2534.0,
                date = 1234
            )
        }

        InvoiceListView(dummyList, paddingValues, navigate)
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
                navigate(NavigationGraphComponent.NavInvoiceDetailScreen(items[ind].id))
            },items[ind])
        }
    }
}

@Composable
private fun InvoiceCard(modifier: Modifier, invoice: Invoice){
    OutlinedCard(modifier = modifier.wrapContentHeight().fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp, 16.dp)) {
            Column(modifier = Modifier.weight(0.75f)) {
                Text(invoice.name,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(invoice.date.toString(), fontSize = 12.sp)
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
//    val dummy = Invoice(
//        id = "abcd",
//        name = "Vishal",
//        totalAmount = 1234,
//        dueAmount = 2534,
//        date = "25th Apr"
//    )
//    InvoiceCard(dummy)
//}


@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen { _ -> Unit}
}