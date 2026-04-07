package com.jesscafezeiro.financialmanagement.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jesscafezeiro.financialmanagement.data.db.AppDatabase
import com.jesscafezeiro.financialmanagement.data.entity.Account
import com.jesscafezeiro.financialmanagement.data.entity.Category
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        databaseCallback: DatabaseCallback
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "financial_database"
        )
            .addCallback(databaseCallback)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAccountDao(database: AppDatabase) = database.accountDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase) = database.categoryDao()

    @Provides
    fun provideTransactionDao(database: AppDatabase) = database.transactionDao()
}

class DatabaseCallback @Inject constructor(
    private val databaseProvider: Provider<AppDatabase>
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            seedDatabase()
        }
    }

    private suspend fun seedDatabase() {
        val database = databaseProvider.get()
        val accountDao = database.accountDao()
        val categoryDao = database.categoryDao()

        accountDao.insertAccount(Account(name = "Main Wallet", balance = 0.0))
        accountDao.insertAccount(Account(name = "Bank Account", balance = 0.0))

        categoryDao.insertCategory(Category(name = "Salary", type = "INCOME"))
        categoryDao.insertCategory(Category(name = "Investments", type = "INCOME"))

        categoryDao.insertCategory(Category(name = "Food", type = "EXPENSE"))
        categoryDao.insertCategory(Category(name = "Transport", type = "EXPENSE"))
        categoryDao.insertCategory(Category(name = "Health", type = "EXPENSE"))
        categoryDao.insertCategory(Category(name = "Others", type = "EXPENSE"))
    }
}