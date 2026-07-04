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

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen()
            }
            composable(Screen.Calendar.route) {
                CalendarScreen()
            }
            composable(Screen.Payments.route) {
                PaymentScreen()
            }
            composable(Screen.Reports.route) {
                ReportsScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
