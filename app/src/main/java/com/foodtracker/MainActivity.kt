package com.foodtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.foodtracker.ui.components.BottomNavigationBar
import com.foodtracker.ui.navigation.NavigationGraph
import com.foodtracker.ui.theme.FoodTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodTrackerTheme {
                val navController = rememberNavController()
                
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController)
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { /* Navigate to add entry */ }
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Add,
                                contentDescription = "Add Entry"
                            )
                        }
                    }
                ) { paddingValues ->
                    NavigationGraph(
                        navController = navController,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}
