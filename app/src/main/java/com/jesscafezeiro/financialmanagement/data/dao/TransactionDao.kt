package com.jesscafezeiro.financialmanagement.data.dao

import androidx.room.*
import com.jesscafezeiro.financialmanagement.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

data class TotalByCategory(
    val categoryId: Long,
    val total: Double
)

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: String): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncomes(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpenses(): Flow<Double?>

    @Query("SELECT categoryId, SUM(amount) as total FROM transactions WHERE type = 'EXPENSE' GROUP BY categoryId")
    fun getTotalByCategory(): Flow<List<TotalByCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
}
