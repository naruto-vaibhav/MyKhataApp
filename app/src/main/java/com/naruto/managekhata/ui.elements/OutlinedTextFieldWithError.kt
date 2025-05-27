package com.naruto.managekhata.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun OutlinedTextFieldWithError(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorMessage: String,
    modifier: Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(label) },
            isError = isError,
            modifier = modifier.padding(vertical = 2.dp).shadow(4.dp, RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedLabelColor = if (isError) Color.Red else MaterialTheme.colorScheme.primary,
                cursorColor = if (isError) Color.Red else MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.LightGray,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                disabledContainerColor = Color.White
            ),
            keyboardOptions = keyboardOptions,
            trailingIcon = trailingIcon,
            enabled = enabled
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 2.dp)
            )
        }
    }
}

@Preview
@Composable
fun OutlinedTextFieldWithErrorPreview() {
    OutlinedTextFieldWithError(
        value = "",
        onValueChange = {},
        label = "Enter Text",
        isError = false,
        errorMessage = "Please Enter correct text",
        Modifier
    )
}