package com.example.financialmanagement.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financialmanagement.ui.dashboard.formatCurrency
import com.example.financialmanagement.ui.theme.*

@Composable
fun ReportsScreen(
    navController: NavController,
    viewModel: ReportsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CinzaFundo)
            .padding(16.dp)
    ) {
        Text(
            text = "Reports",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Preto
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Verde)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Branco)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Summary",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Preto
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Total Incomes",
                                        fontSize = 12.sp,
                                        color = CinzaTexto
                                    )
                                    Text(
                                        text = formatCurrency(uiState.totalIncomes),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Verde
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Total Expenses",
                                        fontSize = 12.sp,
                                        color = CinzaTexto
                                    )
                                    Text(
                                        text = formatCurrency(uiState.totalExpenses),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Vermelho
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "By Category",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Preto
                    )
                }

                if (uiState.reports.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No expenses recorded yet.",
                                color = CinzaTexto
                            )
                        }
                    }
                } else {
                    items(uiState.reports) { item ->
                        CategoryCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(item: ReportItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(getCategoryColor(item.category.id))
                    )
                    Text(
                        text = item.category.name,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = Preto
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "-${formatCurrency(item.total)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Vermelho
                    )
                    Text(
                        text = "${item.percentage.toInt()}% of total",
                        fontSize = 12.sp,
                        color = CinzaTexto
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { item.percentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = getCategoryColor(item.category.id),
                trackColor = CinzaFundo
            )
        }
    }
}

fun getCategoryColor(id: Long): Color {
    val colors = listOf(
        Color(0xFFE53935),
        Color(0xFF1E88E5),
        Color(0xFF8E24AA),
        Color(0xFF43A047),
        Color(0xFFFF8F00),
        Color(0xFF00ACC1),
        Color(0xFFD81B60),
        Color(0xFF6D4C41),
    )
    return colors[(id % colors.size).toInt()]
}
