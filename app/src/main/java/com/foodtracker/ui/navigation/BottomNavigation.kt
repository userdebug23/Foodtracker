package com.foodtracker.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.foodtracker.R

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Screen.Dashboard to R.drawable.ic_dashboard,
        Screen.Calendar to R.drawable.ic_calendar,
        Screen.Payments to R.drawable.ic_payment,
        Screen.Reports to R.drawable.ic_reports,
        Screen.Settings to R.drawable.ic_settings
    )
    
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    
    NavigationBar(modifier = modifier) {
        items.forEach { (screen, icon) ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination to avoid multiple copies
                        popUpTo(Screen.Dashboard.route) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = screen.route
                    )
                },
                label = { Text(screen.route.replaceFirstChar { it.uppercase() }) }
            )
        }
    }
}
