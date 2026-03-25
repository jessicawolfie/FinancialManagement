package com.example.financialmanagement

import android.app.Application
import com.example.financialmanagement.data.db.AppDatabase
import com.example.financialmanagement.data.entity.Category
import com.example.financialmanagement.data.entity.Account
import com.example.financialmanagement.data.repository.FinancialRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FinancialApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }

    val repository by lazy {
        FinancialRepository(
            database.accountDao(),
            database.categoryDao(),
            database.transactionDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            populateInitialData()
        }
    }

    private suspend fun populateInitialData() {
        val expenseCategories = listOf(
            Category(name = "Food", type = "EXPENSE"),
            Category(name = "Transport", type = "EXPENSE"),
            Category(name = "Entertainment", type = "EXPENSE"),
            Category(name = "Health", type = "EXPENSE"),
            Category(name = "Housing", type = "EXPENSE"),
            Category(name = "Education", type = "EXPENSE"),
            Category(name = "Clothing", type = "EXPENSE"),
            Category(name = "Others", type = "EXPENSE")
        )

        val incomeCategories = listOf(
            Category(name = "Salary", type = "INCOME"),
            Category(name = "Freelance", type = "INCOME"),
            Category(name = "Investments", type = "INCOME"),
            Category(name = "Extra", type = "INCOME")
        )

        val accounts = listOf(
            Account(name = "Wallet"),
            Account(name = "Bank"),
            Account(name = "Savings"),
            Account(name = "Cash")
        )

        val existingCategories = database.categoryDao().getAll().first()
        if (existingCategories.isEmpty()) {
            expenseCategories.forEach { repository.insertCategory(it) }
            incomeCategories.forEach { repository.insertCategory(it) }
        }

        val existingAccounts = database.accountDao().getAll().first()
        if (existingAccounts.isEmpty()) {
            accounts.forEach { repository.insertAccount(it) }
        }
    }
}