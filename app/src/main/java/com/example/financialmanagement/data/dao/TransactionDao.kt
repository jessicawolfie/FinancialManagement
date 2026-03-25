package com.example.financialmanagement.data.dao

import androidx.room.*
import com.example.financialmanagement.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date ASC")
    fun getAll(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date ASC")
    fun getByType(type: String): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncomes(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpenses(): Flow<Double?>

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT 5")
    fun getLatest(): Flow<List<Transaction>>

    @Query("""
        SELECT categoryId, SUM(amount) AS total
        FROM transactions
        WHERE type = 'EXPENSE'
        GROUP BY categoryId
    """)
    fun getTotalByCategory(): Flow<List<TotalByCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)
}

data class TotalByCategory(
    val categoryId: Long,
    val total: Double
)