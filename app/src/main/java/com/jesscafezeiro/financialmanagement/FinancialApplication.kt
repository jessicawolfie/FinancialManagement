package com.jesscafezeiro.financialmanagement

import android.app.Application
import com.jesscafezeiro.financialmanagement.data.db.AppDatabase
import com.jesscafezeiro.financialmanagement.data.repository.FinancialRepository

class FinancialApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy {
        FinancialRepository(
            database.accountDao(),
            database.categoryDao(),
            database.transactionDao()
        )
    }
}
