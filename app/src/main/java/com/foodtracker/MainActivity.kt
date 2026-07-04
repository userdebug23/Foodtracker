package com.foodtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.foodtracker.ui.navigation.NavGraph
import com.foodtracker.ui.theme.FoodTrackerTheme
import com.foodtracker.utils.ThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodTrackerApp()
        }
    }
}

@Composable
fun FoodTrackerApp() {
    val context = LocalContext.current
    
    var isDarkTheme by remember {
        mutableStateOf(ThemeManager.isDarkTheme(context))
    }
    
    // Re-read theme when activity recreates
    LaunchedEffect(Unit) {
        isDarkTheme = ThemeManager.isDarkTheme(context)
    }
    
    FoodTrackerTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavGraph()
        }
    }
}
