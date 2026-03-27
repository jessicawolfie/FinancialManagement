package com.jesscafezeiro.financialmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jesscafezeiro.financialmanagement.navigation.Routes
import com.jesscafezeiro.financialmanagement.ui.dashboard.DashboardScreen
import com.jesscafezeiro.financialmanagement.ui.dashboard.DashboardViewModel
import com.jesscafezeiro.financialmanagement.ui.reports.ReportsScreen
import com.jesscafezeiro.financialmanagement.ui.reports.ReportsViewModel
import com.jesscafezeiro.financialmanagement.ui.splash.SplashScreen
import com.jesscafezeiro.financialmanagement.ui.theme.ControleFinanceiroTheme
import com.jesscafezeiro.financialmanagement.ui.transactions.FormScreen
import com.jesscafezeiro.financialmanagement.ui.transactions.FormViewModel
import com.jesscafezeiro.financialmanagement.ui.transactions.TransactionsScreen
import com.jesscafezeiro.financialmanagement.ui.transactions.TransactionsViewModel

data class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ControleFinanceiroTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val navigationItems = listOf(
        NavigationItem(Routes.DASHBOARD, "Summary", Icons.Default.Home),
        NavigationItem(Routes.TRANSACTIONS, "Transactions", Icons.AutoMirrored.Filled.List),
        NavigationItem(Routes.REPORTS, "Reports", Icons.Default.BarChart)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val routesWithNav = listOf(Routes.DASHBOARD, Routes.TRANSACTIONS, Routes.REPORTS)

    Scaffold(
        bottomBar = {
            if (currentRoute in routesWithNav) {
                NavigationBar {
                    navigationItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(Routes.DASHBOARD) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(navController = navController)
            }

            composable(Routes.DASHBOARD) {
                val context = LocalContext.current
                val app = context.applicationContext as FinancialApplication
                val viewModel: DashboardViewModel = viewModel(
                    factory = DashboardViewModel.factory(app.repository)
                )
                DashboardScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }

            composable(Routes.TRANSACTIONS) {
                val context = LocalContext.current
                val app = context.applicationContext as FinancialApplication
                val viewModel: TransactionsViewModel = viewModel(
                    factory = TransactionsViewModel.factory(app.repository)
                )
                TransactionsScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }

            composable(
                route = Routes.FORM,
                arguments = listOf(
                    navArgument("id") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: -1L
                val context = LocalContext.current
                val app = context.applicationContext as FinancialApplication
                val viewModel: FormViewModel = viewModel(
                    factory = FormViewModel.factory(app.repository)
                )
                FormScreen(
                    navController = navController,
                    transactionId = id,
                    viewModel = viewModel
                )
            }

            composable(Routes.REPORTS) {
                val context = LocalContext.current
                val app = context.applicationContext as FinancialApplication
                val viewModel: ReportsViewModel = viewModel(
                    factory = ReportsViewModel.factory(app.repository)
                )
                ReportsScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}
