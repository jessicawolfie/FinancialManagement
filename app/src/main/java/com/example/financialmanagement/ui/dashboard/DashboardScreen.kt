package com.example.financialmanagement.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financialmanagement.data.entity.Transaction
import com.example.financialmanagement.navigation.Routes
import com.example.financialmanagement.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel
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
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New transaction",
                    tint = Branco
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(CinzaFundo),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { HeaderDashboard() }
            item {
                CardBalance(
                    balance = uiState.currentBalance,
                    isLoading = uiState.isLoading
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CardSummary(
                        modifier = Modifier.weight(1f),
                        title = "INCOME",
                        amount = uiState.totalIncomes,
                        isIncome = true
                    )
                    CardSummary(
                        modifier = Modifier.weight(1f),
                        title = "EXPENSES",
                        amount = uiState.totalExpenses,
                        isIncome = false
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Preto
                    )
                    Text(
                        text = "See all",
                        color = Verde,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            navController.navigate(Routes.TRANSACTIONS)
                        }
                    )
                }
            }
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Verde)
                    }
                }
            } else if (uiState.latestTransactions.isEmpty()) {
                item {
                    Text(
                        text = "No transactions yet. \nClick + to add!",
                        color = CinzaTexto,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(uiState.latestTransactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = {
                            navController.navigate(Routes.form(transaction.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderDashboard() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Summary",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Preto
            )
        }
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = CinzaTexto
        )
    }
}

@Composable
fun CardBalance(balance: Double, isLoading: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Verde)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Current Balance",
                fontSize = 14.sp,
                color = Branco.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (isLoading) {
                CircularProgressIndicator(color = Branco, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = formatCurrency(balance),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Branco
                )
            }
        }
    }
}

@Composable
fun CardSummary(
    modifier: Modifier = Modifier,
    title: String,
    amount: Double,
    isIncome: Boolean
) {
    val bgColor = if (isIncome) VerdeFundo else VermelhoFundo
    val amountColor = if (isIncome) Verde else Vermelho
    val icon = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = amountColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 11.sp, color = CinzaTexto)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatCurrency(amount),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}

@Composable
fun TransactionItem(
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
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
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
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}

fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(amount)
}

fun formatDate(date: Date): String {
    return SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date)
}