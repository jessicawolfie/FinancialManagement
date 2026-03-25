package com.example.financialmanagement.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financialmanagement.data.entity.Transaction
import com.example.financialmanagement.data.repository.FinancialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

enum class TransactionFilter {
    ALL, INCOMES, EXPENSES
}

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val activeFilter: TransactionFilter = TransactionFilter.ALL,
    val isLoading: Boolean = true
)

class TransactionsViewModel(
    private val repository: FinancialRepository
) : ViewModel() {

    private val _activeFilter = MutableStateFlow(TransactionFilter.ALL)

    val uiState: StateFlow<TransactionsUiState> = combine(
        _activeFilter,
        repository.allTransactions,
        repository.transactionsByType("INCOME"),
        repository.transactionsByType("EXPENSE")
    ) { filter, all, incomes, expenses ->
        val filteredList = when (filter) {
            TransactionFilter.ALL -> all
            TransactionFilter.INCOMES -> incomes
            TransactionFilter.EXPENSES -> expenses
        }
        TransactionsUiState(
            transactions = filteredList,
            activeFilter = filter,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionsUiState()
    )

    fun onFilterChange(filter: TransactionFilter) {
        _activeFilter.update { filter }
    }

    companion object {
        fun factory(repository: FinancialRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TransactionsViewModel(repository) as T
                }
            }
    }
}
