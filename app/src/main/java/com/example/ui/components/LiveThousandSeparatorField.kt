package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalGlassTokens
import com.example.util.NumberFormatUtils
import com.example.util.ThousandsSeparatorVisualTransformation

@Composable
fun LiveThousandSeparatorField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "۰",
    suffixText: String = "تومان",
    isError: Boolean = false,
    errorMessage: String? = null,
    testTag: String = "amount_input"
) {
    val tokens = LocalGlassTokens.current

    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            // Filter digits only, convert Persian digits to English digits
            val cleaned = NumberFormatUtils.cleanNumberString(input)
            onValueChange(cleaned)
        },
        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = tokens.textMuted) },
        suffix = {
            if (suffixText.isNotEmpty()) {
                Text(
                    text = suffixText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        visualTransformation = ThousandsSeparatorVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        isError = isError,
        supportingText = if (isError && !errorMessage.isNullOrEmpty()) {
            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = tokens.glassBorder,
            focusedContainerColor = tokens.glassSurfaceVariant.copy(alpha = 0.5f),
            unfocusedContainerColor = tokens.glassSurfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}
