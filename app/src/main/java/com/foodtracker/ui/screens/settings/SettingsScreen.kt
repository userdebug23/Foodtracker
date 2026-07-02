package com.foodtracker.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.foodtracker.ui.components.SettingsItem
import com.foodtracker.ui.components.SettingsSection

@Composable
fun SettingsScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            SettingsSection(title = "General") {
                SettingsItem(
                    icon = Icons.Default.AttachMoney,
                    title = "Monthly Charge",
                    subtitle = "₹4800"
                )
                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = "Theme",
                    subtitle = "Light"
                )
            }
        }
        
        item {
            SettingsSection(title = "Data Management") {
                SettingsItem(
                    icon = Icons.Default.Backup,
                    title = "Backup Database"
                )
                SettingsItem(
                    icon = Icons.Default.Restore,
                    title = "Restore Database"
                )
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "Reset Database"
                )
            }
        }
        
        item {
            SettingsSection(title = "About") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Version",
                    subtitle = "1.0.0"
                )
            }
        }
    }
}
