package com.naruto.managekhata.screen.depth

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naruto.managekhata.model.Payment
import com.naruto.managekhata.navigation.NavigationGraphComponent
import com.naruto.managekhata.screen.home.TAG
import com.naruto.managekhata.ui.theme.DateFormatter

@SuppressLint("ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailScreen(
    invoiceId: String,
    navigate: (NavigationGraphComponent) -> Unit,
    viewModel: InvoiceDetailViewModel = hiltViewModel()
) {
//
    val invoice = viewModel.invoice
    val payments = viewModel.payment

    DisposableEffect(Unit) {
        Log.i(TAG, "InvoiceDetailScreen")
        viewModel.addInvoiceDetailListener(invoiceId)
        viewModel.addPaymentListener(invoiceId)
        onDispose {
            Log.i(TAG, "InvoiceDetailScreen- onDispose")
            viewModel.removeListener()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navigate(NavigationGraphComponent.NavNewPaymentScreen(invoiceId, invoice.value.interestPercentage, invoice.value.dueDate)) }, modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp)) {
//                Icon(Icons.Default.Add, contentDescription = "Add Payment")
                Text("Add Payment")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Top Card with invoice info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Customer: ${invoice.value.name}")
                    Text("Invoice Date: ${DateFormatter.toFormatDate(invoice.value.invoiceDate)}")
                    Text("Due Date: ${DateFormatter.toFormatDate(invoice.value.dueDate)}")
                    Text("Loan Amount: ₹${invoice.value.invoiceAmount}")
                    Text("Due Amount: ₹${invoice.value.dueAmount}")
                    Text("Net Interest Amount: ₹${invoice.value.interestAmount}")
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(payments.value.size) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        PaymentCard(payments.value[index])
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentCard(payment: Payment){
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(DateFormatter.toFormatDate(payment.date))
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Amount")
                Spacer(modifier = Modifier.weight(1f))
                Text("${payment.amount}")
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text("Interest")
                Spacer(modifier = Modifier.weight(1f))
                Text("${payment.interest}")
            }
        }
    }

//    Row(modifier = Modifier.padding(16.dp, 16.dp)) {
//        Column(modifier = Modifier.weight(0.75f)) {
//            Text(invoice.name,
//                fontSize = 18.sp,
//                modifier = Modifier.padding(bottom = 4.dp)
//            )
//            Text(invoice.date.toString(), fontSize = 12.sp)
//        }
//        Column(modifier = Modifier.weight(0.25f),
//            horizontalAlignment = Alignment.End) {
//            Text("Due (Rs.)", modifier = Modifier.padding(bottom = 4.dp))
//            Text(invoice.dueAmount.toString())
//        }
//    }
}

//@Preview
//@Composable
//private fun PaymentCardPreview(){
//    val pay = Payment(
//        "1234",
//        123.0,
//        12.0,
//        123456L
//    )
//    PaymentCard(pay)
//}

@Preview
@Composable
fun InvoiceDetailScreenPreview() {
//    val sampleInvoice = Invoice(
//        id = "inv123",
//        name = "Ravi",
//        invoiceAmount = 1000.0,
//        dueAmount = 100.0,
//        date = System.currentTimeMillis()
//    )
//    InvoiceDetailScreen(sampleInvoice.id) {}
}
