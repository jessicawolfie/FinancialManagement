package com.jesscafezeiro.financialmanagement.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jesscafezeiro.financialmanagement.data.entity.Category
import com.jesscafezeiro.financialmanagement.data.entity.Account
import com.jesscafezeiro.financialmanagement.data.entity.Transaction
import com.jesscafezeiro.financialmanagement.data.repository.FinancialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

data class FormUiState(
    val id: Long = -1L,
    val description: String = "",
    val amount: String = "",
    val type: String = "EXPENSE",
    val selectedCategory: Category? = null,
    val selectedAccount: Account? = null,
    val date: Date = Date(),
    val note: String = "",
    val isEditing: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

class FormViewModel(
    private val repository: FinancialRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState

    val categories: StateFlow<List<Category>> =
        repository.categoriesByType(uiState.value.type)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val accounts: StateFlow<List<Account>> =
        repository.allAccounts
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun loadTransaction(id: Long) {
        if (id == -1L) return
        viewModelScope.launch {
            repository.allTransactions.collect { list ->
                val transaction = list.find { it.id == id }
                transaction?.let { t ->
                    val category = categories.value.find { it.id == t.categoryId }
                    val account = accounts.value.find { it.id == t.accountId }
                    _uiState.update { state ->
                        state.copy(
                            id = t.id,
                            description = t.description,
                            amount = t.amount.toString(),
                            type = t.type,
                            selectedCategory = category,
                            selectedAccount = account,
                            date = t.date,
                            note = t.note ?: "",
                            isEditing = true
                        )
                    }
                }
            }
        }
    }

    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }
    fun onAmountChange(value: String) = _uiState.update { it.copy(amount = value)}
    fun onTypeChange(value: String) = _uiState.update { it.copy(type = value) }
    fun onCategoryChange(value: Category?) = _uiState.update { it.copy(selectedCategory = value) }
    fun onAccountChange(value: Account?) = _uiState.update { it.copy(selectedAccount = value) }
    fun onDataChange(value: Date) = _uiState.update { it.copy(date = value) }
    fun onNoteChange(value: String) = _uiState.update { it.copy(note = value) }

    fun save() {
        val state = _uiState.value
        if (state.description.isBlank()) {
            _uiState.update { it.copy(error = "Enter transaction description")}
            return
        }
        val amountDouble = state.amount.replace(",", ".").toDoubleOrNull()
        if (amountDouble == null || amountDouble <= 0) {
            _uiState.update { it.copy(error = "Enter a valid amount.") }
            return
        }
        if (state.selectedCategory == null) {
            _uiState.update { it.copy(error = "Select a category.") }
            return
        }
        if (state.selectedAccount == null) {
            _uiState.update { it.copy(error = "Select an account.") }
            return
        }
        viewModelScope.launch {
            val transaction = Transaction(
                id = if (state.isEditing) state.id else 0L,
                description = state.description,
                amount = amountDouble,
                type = state.type,
                date = state.date,
                categoryId = state.selectedCategory.id,
                accountId = state.selectedAccount.id,
                note = state.note.ifBlank { null }
            )
            if (state.isEditing) {
                repository.updateTransaction(transaction)
            } else {
                repository.insertTransaction(transaction)
            }
            _uiState.update { it.copy(saved = true) }
        }
    }

    fun delete() {
        val state = _uiState.value
        if (!state.isEditing) return
        viewModelScope.launch {
            val transaction = Transaction(
                id = state.id,
                description = state.description,
                amount = state.amount.toDoubleOrNull() ?: 0.0,
                type = state.type,
                date = state.date,
                categoryId = state.selectedCategory?.id ?: 0L,
                accountId = state.selectedAccount?.id ?: 0L
            )
            repository.deleteTransaction(transaction)
            _uiState.update { it.copy(saved = true) }
        }
    }

    fun clear() {
        _uiState.value = FormUiState()
    }

    companion object {
        fun factory(repository: FinancialRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FormViewModel(repository) as T
                }
            }
    }
}
