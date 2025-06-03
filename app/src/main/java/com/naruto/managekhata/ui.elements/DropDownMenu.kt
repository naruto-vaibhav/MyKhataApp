package com.naruto.managekhata.ui.elements

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.naruto.managekhata.CustomInterestDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownWithDefaultValue(
    modifier: Modifier = Modifier,
    options: List<CustomInterestDuration>,
    selectedOption: CustomInterestDuration,
    onOptionSelect: (CustomInterestDuration) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(targetValue = if (expanded) 1f else 0f)
    val scale by animateFloatAsState(targetValue = if (expanded) 1f else 0.95f)

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
            shape = RoundedCornerShape(8.dp),
            modifier = modifier
                .padding(vertical = 2.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp)).menuAnchor(),
            colors = TextFieldDefaults.colors(
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.LightGray,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                disabledContainerColor = Color.White
            ),
        )

        // The actual dropdown menu
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .graphicsLayer {
                    this.alpha = alpha
                    this.scaleY = scale
                }
                .background(Color.White, shape = RoundedCornerShape(8.dp))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.value, color = Color.Black) },
                    onClick = {
                        onOptionSelect(option)
                        expanded = false
                    },
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

    }
}

@Preview
@Composable
fun DropDownMenuPreview(){
    val options = CustomInterestDuration.entries
    DropdownWithDefaultValue(options = options, selectedOption = CustomInterestDuration.DAILY) {}
}
