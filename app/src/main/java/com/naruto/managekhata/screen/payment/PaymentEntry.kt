package com.naruto.managekhata.screen.payment

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.model.Payment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("SimpleDateFormat")
@Composable
fun PaymentEntry(
    invoiceId: String? = null,
    interestPercent: Double = 0.0,
    dueDate: Long = 0L,
    popUp: () -> Unit,
    paymentEntryViewModel: PaymentEntryViewModel = hiltViewModel()
) {
    val isNewInvoiceScreen = invoiceId==null

    var name by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var days by rememberSaveable { mutableStateOf("") }
    var interest by rememberSaveable { mutableStateOf("") }
    var selectedDate by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp),
                onClick = {
                    if (amount.isNotBlank()) {
                        if (isNewInvoiceScreen){
                            val invoiceDetail = Invoice(
                                name = name,
                                invoiceAmount = amount.toDouble(),
                                invoiceDate = selectedDate,
                                dueAmount = amount.toDouble(),
                                dueDate = getDueDate(selectedDate, days.toInt()),
                                interestPercentage = interest.toDouble()
                            )
                            paymentEntryViewModel.createInvoice(invoiceDetail) { popUp() }
                        }
                        else{
                            val paymentAmount = amount.toDouble()
                            val interestAmount = getInterestAmount(
                                paymentAmount,
                                interestPercent,
                                selectedDate,
                                dueDate
                            )
                            val payment = Payment(
                                amount = amount.toDouble(),
                                interest = interestAmount,
                                date = selectedDate
                            )
                            invoiceId?.let {
                                paymentEntryViewModel.createPayment(invoiceId, payment) { popUp() }
                            }
                        }

                    } else {
                        Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Save")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isNewInvoiceScreen){
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Customer Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Loan Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            if (isNewInvoiceScreen){
                TextField(
                    value = days,
                    onValueChange = { days = it },
                    label = { Text("Days") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = interest,
                    onValueChange = { interest = it },
                    label = { Text("Interest (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = dateFormat.format(Date(selectedDate)),
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Select Date") },
                enabled = false, // prevent manual editing
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                    }
                }
            )
        }
    }
}

private fun getDueDate(invoiceDate: Long, days: Int) = invoiceDate + days.toTimeMillis()

fun Int.toTimeMillis() = this * 24 * 60 * 60 * 1000

private fun getInterestAmount(paymentAmount: Double, interest: Double, paymentDate: Long, dueDate: Long):Double {
    val days = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(dueDate - paymentDate)
    return paymentAmount*days*interest*0.01
}


//fun main(){
//    val dueDate: Long = System.currentTimeMillis()
//    val paymentDate: Long = System.currentTimeMillis() - 2*24*60*60*1000
//    println(DateFormatter.toFormatDate(dueDate))
//    println(DateFormatter.toFormatDate(paymentDate))
//
////    val diffInMillis = date2Millis - date1Millis
////    val diffInDays =
//    println(getInterestAmount(2000.0, 10.0, paymentDate, dueDate))
//}
@Preview
@Composable
fun PaymentEntryPreview(){
    PaymentEntry (popUp = { })
}
