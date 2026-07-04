package com.foodtracker.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Screen.Dashboard to "📊",
        Screen.Calendar to "📅",
        Screen.Payments to "💰",
        Screen.Reports to "📈",
        Screen.Settings to "⚙️"
    )
    
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    
    NavigationBar(modifier = modifier) {
        items.forEach { (screen, icon) ->
            NavigationBarItem(
                icon = {
                    Text(
                        text = icon,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    )
                },
                label = {
                    Text(
                        text = when (screen) {
                            Screen.Dashboard -> "Home"
                            Screen.Calendar -> "Calendar"
                            Screen.Payments -> "Payments"
                            Screen.Reports -> "Reports"
                            Screen.Settings -> "Settings"
                        }
                    )
                },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(Screen.Dashboard.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
