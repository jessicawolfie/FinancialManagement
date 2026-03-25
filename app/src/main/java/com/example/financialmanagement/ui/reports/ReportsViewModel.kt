package com.example.financialmanagement.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.financialmanagement.data.entity.Category
import com.example.financialmanagement.data.repository.FinancialRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ReportItem(
    val category: Category,
    val total: Double,
    val percentage: Float
)

data class ReportsUiState (
    val totalIncomes: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val reports: List<ReportItem> = emptyList(),
    val isLoading: Boolean = true
)

class ReportsViewModel(
    private val repository: FinancialRepository
) : ViewModel() {
    val uiState: StateFlow<ReportsUiState> = combine(
        repository.totalIncomes,
        repository.totalExpenses,
        repository.totalByCategory,
        repository.allCategories
    ) { incomes, expenses, totalsByCategory, categories ->

        val totalInc = incomes ?: 0.0
        val totalExp = expenses ?: 0.0

        val items = totalsByCategory.mapNotNull { totalByCategory ->
            val category = categories.find { it.id == totalByCategory.categoryId }
            category?.let {
                ReportItem(
                    category = it,
                    total = totalByCategory.total,
                    percentage = if (totalExp > 0) {
                        ((totalByCategory.total / totalExp) * 100).toFloat()
                    } else {
                        0f
                    }
                )
            }
        }.sortedByDescending { it.total }

        ReportsUiState(
            totalIncomes = totalInc,
            totalExpenses = totalExp,
            reports = items,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportsUiState()
    )

    companion object {
        fun factory(repository: FinancialRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ReportsViewModel(repository) as T
                }
            }
    }
}
