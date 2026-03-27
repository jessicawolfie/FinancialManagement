package com.jesscafezeiro.financialmanagement.navigation

object Routes {
    const val SPLASH = "splash"
    const val DASHBOARD = "dashboard"
    const val TRANSACTIONS = "transactions"
    const val FORM = "form/{id}"
    const val REPORTS = "reports"

    fun form(id: Long = -1L) = "form/$id"
}
