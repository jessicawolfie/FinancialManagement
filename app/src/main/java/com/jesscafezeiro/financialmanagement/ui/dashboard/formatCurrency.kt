package com.jesscafezeiro.financialmanagement.ui.dashboard

import java.text.NumberFormat
import java.util.*

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "US"))
    return format.format(amount)
}
