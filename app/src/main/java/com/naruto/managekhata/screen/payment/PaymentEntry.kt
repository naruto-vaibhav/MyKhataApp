package com.naruto.managekhata.screen.payment

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.naruto.managekhata.CustomInterestDuration
import com.naruto.managekhata.model.Invoice
import com.naruto.managekhata.model.Payment
import com.naruto.managekhata.ui.elements.DropdownWithDefaultValue
import com.naruto.managekhata.ui.elements.OutlinedTextFieldWithError
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


const val TAG = "PaymentEntry"

@SuppressLint("SimpleDateFormat")
@Composable
fun PaymentEntry(
    invoiceId: String? = null,
    paymentId: String ?= null,
    interestPercent: Double = 0.0,
    dueDate: Long = 0L,
    dueAmount: Double = 0.0,
    popUp: () -> Unit,
    paymentEntryViewModel: PaymentEntryViewModel = hiltViewModel()
) {
    val isNewInvoiceScreen = invoiceId==null
    val isEditPayment = paymentId!=null

    val options = CustomInterestDuration.entries
    var name by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var days by rememberSaveable { mutableStateOf("") }
    var interest by rememberSaveable { mutableStateOf("") }
    var selectedDate by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    var isValidAmount by rememberSaveable { mutableStateOf(true) }
    var isValidPaymentDate by rememberSaveable { mutableStateOf(true) }
    var remarks by rememberSaveable { mutableStateOf("") }
    var duration by rememberSaveable { mutableStateOf(options[0]) }

    LaunchedEffect(Unit) {
        if (isEditPayment){
            invoiceId?.let { invoiceId ->
                paymentId?.let { paymentId ->
                    paymentEntryViewModel.getPayment(invoiceId, paymentId){
                        amount = it.amount.toString()
                        selectedDate = it.date
                    }
                }
            }
        }
    }

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
        bottomBar = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .defaultMinSize(minHeight = 56.dp),
                onClick = {
                    if (amount.isNotBlank()) {
                        if (isNewInvoiceScreen){
                            val invoiceDetail = Invoice(
                                name = name,
                                invoiceAmount = amount.toDouble(),
                                invoiceDate = selectedDate,
                                dueAmount = amount.toDouble(),
                                dueDate = getDueDate(selectedDate, days.toInt()),
                                interestPercentage = interest.toDouble(),
                                interestDuration = duration.value,
                                interestPerDay = (interest.toDouble()/ duration.days).also {
                                    Log.i(TAG, "interest per day - ${interest.toDouble()}")
                                    Log.i(TAG, "interest per day - ${duration.days}")
                                    Log.i(TAG, "interest per day - $it")
                                },
                                remarks = remarks
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
                                id = paymentId,
                                amount = amount.toDouble(),
                                interest = interestAmount,
                                date = selectedDate,
                                remarks = remarks
                            )
                            invoiceId?.let {
                                paymentEntryViewModel.createPayment(invoiceId, payment) { popUp() }
                            }
                        }

                    } else {
                        Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                    }
                },
                shape = FloatingActionButtonDefaults.shape,
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
                OutlinedTextFieldWithError(
                    value = name,
                    onValueChange = { name = it},
                    label = "Customer Name",
                    isError = false,
                    errorMessage = "",
                    modifier = Modifier.fillMaxWidth()
                )
            }
            OutlinedTextFieldWithError(
                value = amount,
                onValueChange = {
                    amount = it
                    isValidAmount = if (!isNewInvoiceScreen) isValidAmount(dueAmount, amount) else true
                                },
                label = "Amount",
                isError = !isValidAmount,
                errorMessage = "Payment cannot be more than Due Amount",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            if (isNewInvoiceScreen){
                OutlinedTextFieldWithError(
                    value = days,
                    onValueChange = { days = it},
                    label = "Days",
                    isError = false,
                    errorMessage = "",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row {
                    Column(modifier = Modifier.weight(0.5f)) {
                        OutlinedTextFieldWithError(
                            value = interest,
                            onValueChange = { interest = it},
                            label = "Interest",
                            isError = false,
                            errorMessage = "",
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Column(modifier = Modifier.weight(0.5f)) {
                        DropdownWithDefaultValue(options, duration) {
                            duration = it
                        }
                    }

                }
            }

            OutlinedTextFieldWithError(
                value = dateFormat.format(Date(selectedDate)),
                onValueChange = {},
                label = "Select Date",
                isError = false,
                errorMessage = "",
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                    }
                },
                enabled = false
            )

            OutlinedTextFieldWithError(
                value = remarks,
                onValueChange = {
                    remarks = it
                },
                label = "Remarks",
                isError = false,
                errorMessage = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp),
                singleLine = false
            )
        }
    }
}

private fun isValidAmount(dueAmount: Double, enteredAmount: String): Boolean {
    return enteredAmount.toDoubleOrNull()?.let { it<=dueAmount } ?: true
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
