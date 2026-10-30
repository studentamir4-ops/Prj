package com.example.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object NumberFormatUtils {

    private val formatter = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))

    fun convertPersianDigitsToEnglish(input: String): String {
        return input.map { char ->
            when (char) {
                '۰' -> '0'
                '۱' -> '1'
                '۲' -> '2'
                '۳' -> '3'
                '۴' -> '4'
                '۵' -> '5'
                '۶' -> '6'
                '۷' -> '7'
                '۸' -> '8'
                '۹' -> '9'
                '٫', '،' -> ','
                else -> char
            }
        }.joinToString("")
    }

    fun cleanNumberString(input: String): String {
        val converted = convertPersianDigitsToEnglish(input)
        return converted.filter { it.isDigit() }
    }

    fun parseAmount(input: String): Long {
        val cleaned = cleanNumberString(input)
        return cleaned.toLongOrNull() ?: 0L
    }

    fun formatNumber(amount: Long): String {
        return formatter.format(amount)
    }

    fun formatNumber(amount: Double): String {
        return formatter.format(amount.toLong())
    }

    fun formatToman(amount: Long): String {
        return "${formatNumber(amount)} تومان"
    }

    fun formatToman(amount: Double): String {
        return "${formatNumber(amount)} تومان"
    }
}

/**
 * VisualTransformation that dynamically inserts commas every 3 digits
 * while preserving cursor mapping accurately.
 */
class ThousandsSeparatorVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val cleaned = NumberFormatUtils.cleanNumberString(originalText)
        if (cleaned.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val formattedBuilder = StringBuilder()
        val len = cleaned.length
        for (i in 0 until len) {
            formattedBuilder.append(cleaned[i])
            val remaining = len - 1 - i
            if (remaining > 0 && remaining % 3 == 0) {
                formattedBuilder.append(",")
            }
        }
        val formatted = formattedBuilder.toString()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, cleaned.length)
                val commasBefore = if (cleaned.length <= 3) 0 else {
                    val remainingFromEnd = cleaned.length - safeOffset
                    val totalCommas = (cleaned.length - 1) / 3
                    val commasAfter = remainingFromEnd / 3
                    (totalCommas - commasAfter).coerceAtLeast(0)
                }
                return (safeOffset + commasBefore).coerceIn(0, formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, formatted.length)
                val substring = formatted.substring(0, safeOffset)
                val commas = substring.count { it == ',' }
                return (safeOffset - commas).coerceIn(0, cleaned.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
