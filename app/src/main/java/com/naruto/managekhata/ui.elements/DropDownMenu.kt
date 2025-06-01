package com.naruto.managekhata.ui.elements

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.naruto.managekhata.CustomInterestDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownWithDefaultValue(
    options: List<CustomInterestDuration>,
    selectedOption: CustomInterestDuration,
    onOptionSelect: (CustomInterestDuration) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        // TextField that displays the selected option
        TextField(
            value = selectedOption.value,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Option") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor()
        )

        // The actual dropdown menu
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.value) },
                    onClick = {
                        onOptionSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

//@Preview
//@Composable
//fun DropDownMenuPreview(){
//    DropdownWithDefaultValue()
//}
