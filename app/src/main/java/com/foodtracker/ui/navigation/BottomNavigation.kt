package com.foodtracker.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.foodtracker.R
import com.foodtracker.ui.navigation.Screen

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val screens = listOf(
        Screen.Dashboard to R.drawable.ic_dashboard,
        Screen.Calendar to R.drawable.ic_calendar,
        Screen.Payments to R.drawable.ic_payment,
        Screen.Reports to R.drawable.ic_reports,
        Screen.Settings to R.drawable.ic_settings
    )
    
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    
    NavigationBar(modifier = modifier) {
        screens.forEach { (screen, iconRes) ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = screen.route
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
