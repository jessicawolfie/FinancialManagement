package com.example.financialmanagement.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financialmanagement.data.entity.Transaction
import com.example.financialmanagement.navigation.Routes
import com.example.financialmanagement.ui.dashboard.formatDate
import com.example.financialmanagement.ui.dashboard.formatCurrency
import com.example.financialmanagement.ui.theme.*

@Composable
fun TransactionsScreen(
    navController: NavController,
    viewModel: TransactionsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Routes.form())
                },
                containerColor = Verde
            ) {
                Icon(Icons.Default.Add, contentDescription = "New transaction", tint = Branco)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CinzaFundo)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Transactions",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Preto
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    text = "All",
                    selected = uiState.activeFilter == TransactionFilter.ALL,
                    onClick = { viewModel.onFilterChange(TransactionFilter.ALL) }
                )
                FilterChip(
                    text = "Incomes",
                    selected = uiState.activeFilter == TransactionFilter.INCOMES,
                    onClick = { viewModel.onFilterChange(TransactionFilter.INCOMES) }
                )
                FilterChip(
                    text = "Expenses",
                    selected = uiState.activeFilter == TransactionFilter.EXPENSES,
                    onClick = { viewModel.onFilterChange(TransactionFilter.EXPENSES) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Verde)
                }
            } else if (uiState.transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transactions found.",
                        color = CinzaTexto
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.transactions) { transaction ->
                        TransactionListItem(
                            transaction = transaction,
                            onClick = {
                                navController.navigate(
                                    Routes.form(transaction.id)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (selected) Verde else Branco
    val textColor = if (selected) Branco else CinzaTexto

    Surface(
        shape = RoundedCornerShape(50.dp),
        color = bgColor,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun TransactionListItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val amountColor = if (transaction.type == "INCOME") Verde else Vermelho
    val prefix = if (transaction.type == "INCOME") "+" else "-"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Preto
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatDate(transaction.date),
                    fontSize = 12.sp,
                    color = CinzaTexto
                )
            }
            Text(
                text = "$prefix${formatCurrency(transaction.amount)}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = amountColor
            )
        }
    }
}
