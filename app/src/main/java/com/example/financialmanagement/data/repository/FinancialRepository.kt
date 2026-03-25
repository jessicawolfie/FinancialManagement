package com.example.financialmanagement.data.repository

import com.example.financialmanagement.data.dao.CategoryDao
import com.example.financialmanagement.data.dao.AccountDao
import com.example.financialmanagement.data.dao.TransactionDao
import com.example.financialmanagement.data.dao.TotalByCategory
import com.example.financialmanagement.data.entity.Category
import com.example.financialmanagement.data.entity.Account
import com.example.financialmanagement.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

class FinancialRepository(
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao
) {
    val allAccounts: Flow<List<Account>> = accountDao.getAll()

    // Accounts
    suspend fun insertAccount(account: Account) = accountDao.insert(account)
    suspend fun deleteAccount(account: Account) = accountDao.delete(account)

    // Categories
    val allCategories: Flow<List<Category>> = categoryDao.getAll()

    fun categoriesByType(type: String): Flow<List<Category>> = categoryDao.getByType(type)

    suspend fun insertCategory(category: Category) = categoryDao.insert(category)
    suspend fun deleteCategory(category: Category) = categoryDao.delete(category)

    // Transactions
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAll()
    val latestTransactions: Flow<List<Transaction>> = transactionDao.getLatest()
    val totalIncomes: Flow<Double?> = transactionDao.getTotalIncomes()
    val totalExpenses: Flow<Double?> = transactionDao.getTotalExpenses()
    val totalByCategory: Flow<List<TotalByCategory>> = transactionDao.getTotalByCategory()

    fun transactionsByType(type: String): Flow<List<Transaction>> = transactionDao.getByType(type)

    suspend fun insertTransaction(transaction: Transaction) = transactionDao.insert(transaction)

    suspend fun updateTransaction(transaction: Transaction) = transactionDao.update(transaction)

    suspend fun deleteTransaction(transaction: Transaction) = transactionDao.delete(transaction)
}