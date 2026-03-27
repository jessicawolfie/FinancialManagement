package com.jesscafezeiro.financialmanagement.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jesscafezeiro.financialmanagement.data.dao.AccountDao
import com.jesscafezeiro.financialmanagement.data.dao.CategoryDao
import com.jesscafezeiro.financialmanagement.data.dao.TransactionDao
import com.jesscafezeiro.financialmanagement.data.entity.Account
import com.jesscafezeiro.financialmanagement.data.entity.Category
import com.jesscafezeiro.financialmanagement.data.entity.Transaction
import com.jesscafezeiro.financialmanagement.util.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                )
                .addCallback(DatabaseCallback(context))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDatabase(database.accountDao(), database.categoryDao())
                    }
                }
            }

            private suspend fun seedDatabase(accountDao: AccountDao, categoryDao: CategoryDao) {
                accountDao.insertAccount(Account(name = "Main Wallet", balance = 0.0))
                accountDao.insertAccount(Account(name = "Bank Account", balance = 0.0))


                categoryDao.insertCategory(Category(name = "Salary", type = "INCOME"))
                categoryDao.insertCategory(Category(name = "Investments", type = "INCOME"))
                categoryDao.insertCategory(Category(name = "Gift", type = "INCOME"))


                categoryDao.insertCategory(Category(name = "Food", type = "EXPENSE"))
                categoryDao.insertCategory(Category(name = "Transport", type = "EXPENSE"))
                categoryDao.insertCategory(Category(name = "Health", type = "EXPENSE"))
                categoryDao.insertCategory(Category(name = "Entertainment", type = "EXPENSE"))
                categoryDao.insertCategory(Category(name = "Bills", type = "EXPENSE"))
                categoryDao.insertCategory(Category(name = "Others", type = "EXPENSE"))
            }
        }
    }
}
