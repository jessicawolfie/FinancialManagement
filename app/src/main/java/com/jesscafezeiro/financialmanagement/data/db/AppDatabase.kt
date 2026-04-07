package com.jesscafezeiro.financialmanagement.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jesscafezeiro.financialmanagement.data.dao.AccountDao
import com.jesscafezeiro.financialmanagement.data.dao.CategoryDao
import com.jesscafezeiro.financialmanagement.data.dao.TransactionDao
import com.jesscafezeiro.financialmanagement.data.entity.Account
import com.jesscafezeiro.financialmanagement.data.entity.Category
import com.jesscafezeiro.financialmanagement.data.entity.Transaction
import com.jesscafezeiro.financialmanagement.util.Converters

@Database(
    entities = [Account::class, Category::class, Transaction::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
}