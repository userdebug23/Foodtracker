package com.foodtracker.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Calendar : Screen("calendar")
    object Payments : Screen("payments")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
}
