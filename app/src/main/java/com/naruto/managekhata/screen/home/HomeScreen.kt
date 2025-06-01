package com.naruto.managekhata.screen.home

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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.navigation.NavigationGraphComponent
import com.naruto.managekhata.ui.theme.DateFormatter

private const val TAG = "HomeScreen"

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ViewModelConstructorInComposable", "CoroutineCreationDuringComposition")
@Composable
fun HomeScreen(
    navigate: (NavigationGraphComponent) -> Unit,
    restartApp: (NavigationGraphComponent) -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
){
    val invoices by viewModel.invoiceFlow.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var isDeleting by rememberSaveable { mutableStateOf(false) }
    val deleteInvoiceIds = viewModel.deleteIdsFlow.collectAsState()

    LaunchedEffect(Unit) { viewModel.initialize(restartApp) }

    DisposableEffect(Unit) {
        Log.i(TAG, "HomeScreen")
        viewModel.addInvoiceListener()
        onDispose {
            Log.i(TAG, "HomeScreen- onDispose")
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
                    if (isDeleting){
                        Text("Select Invoices")
                    }
                    else{
                        Text("Manage Khata")
                    }
                },
                actions = {
                    if (isDeleting) {
                        DeleteActionMenu(
                            deleteInvoiceIds.value.isNotEmpty(),
                            {
                                viewModel.deleteInvoices(deleteInvoiceIds.value.toList())
                                isDeleting = false
                            }) {
                            isDeleting = false
                        }
                    }
                    else{
                        MainActionMenu(viewModel, showMenu,
                            onClick = { showMenu = !showMenu },
                            onDismiss = {showMenu = false},
                            onDeleteRequest = { isDeleting = true }
                            )
                    }
                },

            )
        },
        floatingActionButton = {
            if (!isDeleting){
                FloatingActionButton(onClick = { navigate(NavigationGraphComponent.NavNewInvoiceScreen) }) {
                    Icon(Icons.Default.Add, contentDescription = "Add New Invoice")
                }
            }
        }
    ) { paddingValues ->
        Log.i(TAG, "recomposing invoice list")
        InvoiceListView(
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
private fun HandleBackPressed(isDeleting: Boolean, onBackPressed: ()->Unit){
    Log.i(TAG, "HandleBackPressed - $isDeleting")
    BackHandler(enabled = isDeleting) {
        Log.i(TAG, "HandleBackPressed - executing")
        onBackPressed()
    }
}

@Composable
fun DeleteActionMenu(isDeletePossible: Boolean, onConfirmDelete:()->Unit, stopDelete: ()->Unit){
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
            text = { Text("Delete Invoice") },
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
fun InvoiceListView(
    items: List<Invoice>,
    itemsChecked: Set<String>,
    paddingValues: PaddingValues,
    isDeleting: Boolean,
    onToggleSwitch: (String)->Unit,
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
            InvoiceCard(modifier = Modifier.clickable {
                if (!isDeleting) {
                    items[ind].id?.let{
                        navigate(NavigationGraphComponent.NavInvoiceDetailScreen(it))
                    }
                }
            },items[ind], isDeleting, itemsChecked.contains(items[ind].id),
                onToggleSwitch,
            )
        }
    }
}

@Composable
private fun InvoiceCard(modifier: Modifier = Modifier, invoice: Invoice, isDeleting: Boolean, isChecked: Boolean, onToggle: (String) -> Unit){
    OutlinedCard(modifier = modifier
        .wrapContentHeight()
        .fillMaxWidth()
        .background(if (isDeleting) Color.LightGray else Color.White)
    ) {
        Log.i(TAG, "${invoice.name} - $isChecked")
        Row(modifier = Modifier.padding(16.dp, 16.dp)) {
            if (isDeleting){
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
                Text(invoice.name,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(DateFormatter.toFormatDate(invoice.invoiceDate), fontSize = 12.sp)
            }
            Spacer(modifier=Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
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