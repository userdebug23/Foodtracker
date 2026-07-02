package com.foodtracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.foodtracker.ui.screens.dashboard.DashboardScreen
import com.foodtracker.ui.screens.calendar.CalendarScreen
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
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
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
