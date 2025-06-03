package com.naruto.managekhata.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.naruto.managekhata.ui.theme.PrimaryBlue

@Composable
fun TwoInputTextDialog(
    title: String,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var isValidName by rememberSaveable { mutableStateOf(true) }
    var contactInfo by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        isValidName = true
                        name = it
                                    },
                    label = { Text("Name") },
                    singleLine = true,
                    isError = !isValidName,
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = contactInfo,
                    onValueChange = { contactInfo = it },
                    label = { Text("Phone No.") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isEmpty()){
                    isValidName = false
                }
                else{
                    onConfirm(name, contactInfo)
                }

            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        titleContentColor = PrimaryBlue
    )
}

@Preview
@Composable
fun TwoInputTextDialogPreview(){
    TwoInputTextDialog("Add Group", {_, _ -> }) { }
}
