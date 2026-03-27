package com.jesscafezeiro.financialmanagement.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "financial_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
