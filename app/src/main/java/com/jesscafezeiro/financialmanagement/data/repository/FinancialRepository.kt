package com.jesscafezeiro.financialmanagement.data.repository

import com.jesscafezeiro.financialmanagement.data.dao.AccountDao
import com.jesscafezeiro.financialmanagement.data.dao.CategoryDao
import com.jesscafezeiro.financialmanagement.data.dao.TotalByCategory
import com.jesscafezeiro.financialmanagement.data.dao.TransactionDao
import com.jesscafezeiro.financialmanagement.data.entity.Account
import com.jesscafezeiro.financialmanagement.data.entity.Category
import com.jesscafezeiro.financialmanagement.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

class FinancialRepository(
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao
) {
    // Accounts
    val allAccounts: Flow<List<Account>> = accountDao.getAllAccounts()
    suspend fun insertAccount(account: Account) = accountDao.insertAccount(account)
    suspend fun updateAccount(account: Account) = accountDao.updateAccount(account)
    suspend fun deleteAccount(account: Account) = accountDao.deleteAccount(account)

    // Categories
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()
    fun categoriesByType(type: String): Flow<List<Category>> = categoryDao.getCategoriesByType(type)
    suspend fun insertCategory(category: Category) = categoryDao.insertCategory(category)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

    // Transactions
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
    fun transactionsByType(type: String): Flow<List<Transaction>> = transactionDao.getTransactionsByType(type)
    val totalIncomes: Flow<Double?> = transactionDao.getTotalIncomes()
    val totalExpenses: Flow<Double?> = transactionDao.getTotalExpenses()
    val totalByCategory: Flow<List<TotalByCategory>> = transactionDao.getTotalByCategory()

    suspend fun insertTransaction(transaction: Transaction) = transactionDao.insertTransaction(transaction)
    suspend fun updateTransaction(transaction: Transaction) = transactionDao.updateTransaction(transaction)
    suspend fun deleteTransaction(transaction: Transaction) = transactionDao.deleteTransaction(transaction)
}
