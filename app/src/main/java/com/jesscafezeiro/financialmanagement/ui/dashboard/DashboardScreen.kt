package com.jesscafezeiro.financialmanagement.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jesscafezeiro.financialmanagement.data.entity.Transaction
import com.jesscafezeiro.financialmanagement.navigation.Routes
import com.jesscafezeiro.financialmanagement.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.form()) },
                containerColor = Verde
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Branco)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(CinzaFundo)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Header()
            }

            item {
                BalanceCard(
                    totalBalance = uiState.totalBalance,
                    incomes = uiState.totalIncomes,
                    expenses = uiState.totalExpenses
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Preto
                    )
                    Text(
                        text = "See All",
                        color = Verde,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            navController.navigate(Routes.TRANSACTIONS)
                        }
                    )
                }
            }

            if (uiState.recentTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No transactions yet.", color = CinzaTexto)
                    }
                }
            } else {
                items(uiState.recentTransactions) { transaction ->
                    TransactionItem(transaction = transaction)
                }
            }
        }
    }
}

@Composable
fun Header() {
    Column {
        Text(
            text = "Welcome back,",
            fontSize = 14.sp,
            color = CinzaTexto
        )
        Text(
            text = "Your Finances",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Preto
        )
    }
}

@Composable
fun BalanceCard(
    totalBalance: Double,
    incomes: Double,
    expenses: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Verde)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Balance",
                color = Branco.copy(alpha = 0.8f),
                fontSize = 16.sp
            )
            Text(
                text = formatCurrency(totalBalance),
                color = Branco,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    label = "Incomes",
                    amount = incomes,
                    icon = Icons.Default.ArrowUpward,
                    color = Branco
                )
                SummaryItem(
                    label = "Expenses",
                    amount = expenses,
                    icon = Icons.Default.ArrowDownward,
                    color = Branco
                )
            }
        }
    }
}

@Composable
fun SummaryItem(
    label: String,
    amount: Double,
    icon: ImageVector,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, color = color.copy(alpha = 0.8f), fontSize = 12.sp)
            Text(
                text = formatCurrency(amount),
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val isIncome = transaction.type == "INCOME"
    val color = if (isIncome) Verde else Vermelho
    val icon = if (isIncome) "+" else "-"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = transaction.description,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Preto
                )
                Text(
                    text = formatDate(transaction.date),
                    fontSize = 12.sp,
                    color = CinzaTexto
                )
            }
            Text(
                text = "$icon ${formatCurrency(transaction.amount)}",
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

fun formatDate(date: Date): String {
    val format = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    return format.format(date)
}
