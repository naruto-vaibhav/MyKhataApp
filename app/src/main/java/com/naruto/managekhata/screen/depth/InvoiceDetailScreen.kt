package com.naruto.managekhata.screen.depth

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naruto.managekhata.Formatter.toBigString
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.model.Payment
import com.naruto.managekhata.navigation.NavigationGraphComponent
import com.naruto.managekhata.ui.theme.DateFormatter

private const val TAG = "InvoiceDetailScreen"

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun InvoiceDetailScreen(
    customerId: String,
    customerName: String,
    invoiceId: String,
    navigate: (NavigationGraphComponent) -> Unit,
    viewModel: InvoiceDetailViewModel = hiltViewModel()
) {
//
    val invoice = viewModel.invoice
    val payments = viewModel.payment

    DisposableEffect(Unit) {
        Log.i(TAG, "InvoiceDetailScreen")
        viewModel.addInvoiceDetailListener(customerId, invoiceId)
        viewModel.addPaymentListener(customerId, invoiceId)
        onDispose {
            Log.i(TAG, "InvoiceDetailScreen- onDispose")
            viewModel.removeListener()
        }
    }

    if (viewModel.isLoading.value) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = colorScheme.background)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = colorScheme.onBackground)
        }
    } else {
        InvoiceDetail(customerId, customerName, invoiceId, viewModel, invoice.value, payments.value, navigate)
    }
}

@OptIn(ExperimentalMaterial3Api::class )
@Composable
private fun InvoiceDetail(
    customerId: String,
    customerName: String,
    invoiceId: String,
    viewModel: InvoiceDetailViewModel,
    invoice: Invoice,
    payments: List<Payment>,
    navigate: (NavigationGraphComponent) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var isDeleting by rememberSaveable { mutableStateOf(false) }
    var isEditing by rememberSaveable { mutableStateOf(false) }
    val deletePaymentIds = viewModel.deleteIdsFlow.collectAsState()

    HandleBackPressed(isDeleting, isEditing) {
        isDeleting = false
        isEditing = false
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
                    Text(
                        customerName
                    )
                    Spacer(
                        modifier = Modifier
                            .height(12.dp)
                            .padding(12.dp)
                            .background(Color.Black)
                    )
                },
                actions = {
                    if (isEditing){
                        IconButton(onClick = {
                            isEditing = false
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close"
                            )
                        }
                    }
                    else if (isDeleting) {
                        DeleteActionMenu(deletePaymentIds.value.isNotEmpty(), {
                            viewModel.deletePayments(customerId, invoiceId, deletePaymentIds.value.toList())
                        }) {
                            isDeleting = false
                        }
                    } else {
                        DefaultMenu(showMenu, {
                            Log.i(TAG, "DefaultMenu - $showMenu")
                            showMenu = !showMenu
                            Log.i(TAG, "DefaultMenu after - $showMenu")
                        }, { showMenu = false }, {
                            isEditing = false
                            isDeleting = true
                        }) {
                            isDeleting = false
                            isEditing = true
                        }
                    }
                },

                )
        },
        bottomBar = {
            if (!isEditing && !isDeleting){
                FloatingActionButton(
                    onClick = {
                        navigate(
                            NavigationGraphComponent.NavNewPaymentScreen(
                                customerId,
                                customerName,
                                invoice.id ?: invoiceId,
                                invoice.interestPerDay,
                                invoice.dueDate,
                                invoice.dueAmount
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text("Add Payment")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Customer: $customerName")
                    Text("Invoice Date: ${DateFormatter.toFormatDate(invoice.invoiceDate)}")
                    Text("Due Date: ${DateFormatter.toFormatDate(invoice.dueDate)}")
                    Text("Loan Amount: ₹${invoice.invoiceAmount.toBigString()}")
                    Text("Due Amount: ₹${invoice.dueAmount.toBigString()}")
                    Text("Interest: ${invoice.interestPercentage}%/ ${invoice.interestDuration}")
                    Text("Net Interest Amount: ₹${invoice.interestAmount.toBigString()}")
                    Text("Remarks: ${invoice.remarks}")
                }
            }
            PaymentList(payments, isDeleting, isEditing, { deletePaymentIds.value.contains(it) }, {
                viewModel.updateDeleteIdsFlow(it)
            }){ paymentId, amount ->
                navigate(
                    NavigationGraphComponent.EditPaymentScreen(
                        customerId,
                        customerName,
                        invoice.id ?: invoiceId,
                        paymentId,
                        invoice.interestPerDay,
                        invoice.dueDate,
                        invoice.dueAmount + amount
                    )
                )
                isEditing = false
            }

        }
    }
}

@Preview
@Composable
fun PaymentListPreview(){
    PaymentList(emptyList(),
        isDeleting = true,
        isEditing = true,
        isItemChecked = { _ -> true},
        onToggle = {}) { _, _ -> }
}

@Composable
private fun PaymentList(
    payments: List<Payment>,
    isDeleting: Boolean,
    isEditing: Boolean,
    isItemChecked: (String) -> Boolean,
    onToggle: (String) -> Unit,
    onEdit: (String, Double) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .padding(horizontal = 16.dp)
            .background(
                color = Color.LightGray
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            "Payment Details",
            modifier = Modifier.padding(start = 8.dp),
            fontStyle = FontStyle.Italic
        )
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        items(payments.size) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                PaymentCard(
                    payments[index],
                    isDeleting,
                    isEditing,
                    (payments[index].id?.let(isItemChecked) ?: false),
                    onToggle,
                    onEdit = { payments[index].id?.let{ onEdit(it, payments[index].amount) } }
                )
            }
        }
    }
}

@Composable
fun DefaultMenu(
    showMenu: Boolean,
    onIconClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onDeletePayment: () -> Unit,
    onEditPayment: () -> Unit
) {
    IconButton(onClick = {
        Log.i(TAG, "DefaultMenu Inside - $showMenu")
        onIconClick.invoke()
    }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "Menu"
        )
    }

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { onDismissMenu() }
    ) {
        DropdownMenuItem(
            text = { Text("Edit Payment") },
            onClick = {
                onDismissMenu()
                onEditPayment()
            }
        )

        DropdownMenuItem(
            text = { Text("Delete Payment") },
            onClick = {
                onDismissMenu()
                onDeletePayment()
            }
        )
    }
}

@Composable
private fun HandleBackPressed(isDeleting: Boolean, isEditing: Boolean, onBackPressed: () -> Unit) {
    Log.i(TAG, "HandleBackPressed - $isDeleting")
    BackHandler(enabled = isDeleting || isEditing) {
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
            contentDescription = "Close"
        )
    }
}


@Composable
private fun PaymentCard(
    payment: Payment,
    isDeleting: Boolean,
    isEditing: Boolean,
    isChecked: Boolean,
    onCheckToggle: (String) -> Unit,
    onEdit: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp, 16.dp)
        ) {
            if (isDeleting) {
                Column {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            payment.id?.let(onCheckToggle)
                        }
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Row {
                    Text(DateFormatter.toFormatDate(payment.date))
                }
                Row {
                    Text("Amount")
                    Spacer(modifier = Modifier.weight(1f))
                    Text(payment.amount.toBigString())
                }
                Row( horizontalArrangement = Arrangement.End) {
                    Text("Interest")
                    Spacer(modifier = Modifier.weight(1f))
                    Text(payment.interest.toBigString())
                }
            }
            if (isEditing) {
                IconButton(onClick = { onEdit() }) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                }
            }
        }
    }
}

@Preview
@Composable
private fun PaymentCardPreview() {
    val pay = Payment(
        "1234",
        123.0,
        12.0,
        123456L,
        remarks = "remarks"
    )
    PaymentCard(pay, isDeleting = false, isEditing = true, isChecked = false, onCheckToggle = { }, onEdit = {})
}

//@SuppressLint("ViewModelConstructorInComposable")
//@Preview
//@Composable
//fun InvoiceDetailScreenPreview() {
//    val default = Invoice(
//        name = "Name",
//        invoiceAmount = 0.0,
//        invoiceDate = System.currentTimeMillis(),
//        dueAmount = 0.0,
//        dueDate = 0,
//        interestPercentage = 0.0,
//        interestAmount = 0.0
//    )
//    InvoiceDetailScreen("", {}, InvoiceDetailViewModel(StorageServiceImpl(AccountServiceImpl())))
//}
