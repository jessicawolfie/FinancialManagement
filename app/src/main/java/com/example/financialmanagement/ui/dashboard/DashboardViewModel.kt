package com.example.financialmanagement.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financialmanagement.data.entity.Transaction
import com.example.financialmanagement.data.repository.FinancialRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


data class DashboardUiState(
    val totalIncomes: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val currentBalance: Double = 0.0,
    val latestTransactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true
)

class DashboardViewModel(
    private val repository: FinancialRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.totalIncomes,
        repository.totalExpenses,
        repository.latestTransactions
    ){ incomes, expenses, latest ->

        val totalInc = incomes ?: 0.0
        val totalExp = expenses ?: 0.0

        DashboardUiState(
            totalIncomes = totalInc,
            totalExpenses = totalExp,
            currentBalance = totalInc - totalExp,
            latestTransactions = latest,
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