package com.foodtracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.foodtracker.ui.screens.calendar.CalendarScreen
import com.foodtracker.ui.screens.dashboard.DashboardScreen
import com.foodtracker.ui.screens.payments.PaymentScreen
import com.foodtracker.ui.screens.reports.ReportsScreen
import com.foodtracker.ui.screens.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Calendar : Screen("calendar")
    object Payments : Screen("payments")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Dashboard.route
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(navController)
            }
            composable(Screen.Calendar.route) {
                CalendarScreen(navController)
            }
            composable(Screen.Payments.route) {
                PaymentScreen(navController)
            }
            composable(Screen.Reports.route) {
                ReportsScreen(navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController)
            }
        }
    }
}
