package com.jesscafezeiro.financialmanagement.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jesscafezeiro.financialmanagement.data.entity.Transaction
import com.jesscafezeiro.financialmanagement.data.repository.FinancialRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val totalBalance: Double = 0.0,
    val totalIncomes: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true
)

class DashboardViewModel(
    private val repository: FinancialRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.totalIncomes,
        repository.totalExpenses,
        repository.allTransactions.map { it.take(5) }
    ) { incomes, expenses, recent ->
        val totalInc = incomes ?: 0.0
        val totalExp = expenses ?: 0.0
        DashboardUiState(
            totalBalance = totalInc - totalExp,
            totalIncomes = totalInc,
            totalExpenses = totalExp,
            recentTransactions = recent,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    companion object {
        fun factory(repository: FinancialRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DashboardViewModel(repository) as T
                }
            }
    }
}
