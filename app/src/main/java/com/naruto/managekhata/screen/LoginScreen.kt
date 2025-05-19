package com.naruto.managekhata.screen

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naruto.managekhata.navigation.NavigationGraphComponent
import com.naruto.managekhata.service.impl.AccountServiceImpl

const val PHONE_INITIAL = "+91"

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun LoginScreen(
    modifier: Modifier,
    navigateAndPopUp: (NavigationGraphComponent, NavigationGraphComponent) -> Unit,
    viewModel: LoginScreenViewModel = hiltViewModel()
) {
    var phoneNo by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var isOtpRequested by remember { mutableStateOf(false) }
    val activity = LocalActivity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(0.9f),
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(8.dp)) {
                    Text(text = "+91", fontSize = 16.sp, modifier = Modifier.padding(top = 24.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        TextField(
                            modifier = Modifier.padding(bottom = 4.dp),
                            value = phoneNo,
                            onValueChange = { phoneNo = it },
                            label = { Text("Mobile Number") })
                        if (isOtpRequested){
                            TextField(
                                modifier = Modifier.padding(bottom = 4.dp),
                                value = otp,
                                onValueChange = { otp = it },
                                label = { Text("Enter Otp") },
                                visualTransformation = PasswordVisualTransformation()
                            )
                        }
                    }
                }
                Button(modifier = Modifier.padding(top = 4.dp),
                    enabled = !(isOtpRequested && otp.isEmpty()),
                    onClick = {
                    if (!isOtpRequested && isValidIndianPhoneNumber(phoneNo)) {
                        activity?.let {
                            viewModel.onVerification(it, "$PHONE_INITIAL$phoneNo", {
                                navigateAndPopUp(NavigationGraphComponent.NavHomeScreen, NavigationGraphComponent.NavLoginScreen)
                            }, {})
                        }
                        isOtpRequested = true
                    }
                    else{
                        activity?.let {
                            viewModel.onSignInClick(it, otp, {
                                navigateAndPopUp(NavigationGraphComponent.NavHomeScreen, NavigationGraphComponent.NavLoginScreen)
                            }, {})
                        }
                    }
                }

                ) {
                    Text(text = if (isOtpRequested) "Login" else "Send OTP")
                }
            }
        }
    }
}

fun isValidIndianPhoneNumber(phone: String): Boolean = phone.matches(Regex("^[6-9]\\d{9}$"))

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(Modifier, { _, _ -> Unit })
}