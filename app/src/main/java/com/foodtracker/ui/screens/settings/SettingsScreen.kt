package com.foodtracker.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.provideFactory(context)
    )
    
    val state by viewModel.state.collectAsState()
    
    // Show toast messages
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "⚙️ Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // General Section
        item {
            SettingsSection(title = "General") {
                // Meal Rate
                var showMealRateDialog by remember { mutableStateOf(false) }
                
                SettingsItem(
                    icon = "💰",
                    title = "Meal Rate",
                    subtitle = "₹${String.format("%.2f", state.mealRate)} per meal",
                    onClick = { showMealRateDialog = true }
                )
                
                Divider()
                
                // Theme Toggle
                SettingsSwitch(
                    icon = if (state.isDarkTheme) "🌙" else "☀️",
                    title = "Dark Mode",
                    checked = state.isDarkTheme,
                    onCheckedChange = {
                        viewModel.toggleTheme()
                    }
                )
            }
        }
        
        // Data Management Section
        item {
            SettingsSection(title = "Data Management") {
                SettingsItem(
                    icon = "📤",
                    title = "Export Data",
                    subtitle = "Export to Excel file",
                    onClick = {
                        val file = viewModel.exportData()
                        if (file != null) {
                            showToast("Data exported: ${file.name}")
                        } else {
                            showToast("Export failed")
                        }
                    }
                )
                
                Divider()
                
                SettingsItem(
                    icon = "📥",
                    title = "Import Data",
                    subtitle = "Import from Excel file",
                    onClick = {
                        // File picker would be implemented here
                        showToast("Import feature coming soon")
                    }
                )
                
                Divider()
                
                SettingsItem(
                    icon = "🗑️",
                    title = "Reset Database",
                    subtitle = "Delete all data",
                    onClick = {
                        // Show confirmation dialog
                    },
                    isDanger = true
                )
            }
        }
        
        // About Section
        item {
            SettingsSection(title = "About") {
                SettingsItem(
                    icon = "📱",
                    title = "App Version",
                    subtitle = "1.0.0"
                )
                
                Divider()
                
                SettingsItem(
                    icon = "❤️",
                    title = "Food Tracker",
                    subtitle = "Built with ❤️"
                )
            }
        }
        
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
    
    // Meal Rate Dialog
    if (showMealRateDialog) {
        MealRateDialog(
            currentRate = state.mealRate,
            onDismiss = { showMealRateDialog = false },
            onSave = { newRate ->
                viewModel.saveMealRate(newRate)
                showMealRateDialog = false
                showToast("Meal rate updated to ₹${String.format("%.2f", newRate)}")
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: String,
    title: String,
    subtitle: String? = null,
    isDanger: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 22.sp
        )
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = if (isDanger) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) 
                           else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
        
        if (onClick != null && !isDanger) {
            Text(
                text = "›",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun SettingsSwitch(
    icon: String,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 22.sp
        )
        
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun MealRateDialog(
    currentRate: Double,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var rateText by remember { mutableStateOf(String.format("%.2f", currentRate)) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Meal Rate") },
        text = {
            Column {
                Text(
                    text = "Enter the cost per meal",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = rateText,
                    onValueChange = { 
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            rateText = it
                        }
                    },
                    label = { Text("Amount (₹)") },
                    placeholder = { Text("Enter rate per meal") },
                    singleLine = true,
                    leadingIcon = { Text("₹", fontSize = 18.sp) }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val rate = rateText.toDoubleOrNull()
                    if (rate != null && rate > 0) {
                        onSave(rate)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ViewModel Factory
class SettingsViewModelFactory(private val context: Context) {
    fun create(): SettingsViewModel {
        return SettingsViewModel(context)
    }
}

// Extension to provide ViewModel factory
fun SettingsViewModel.provideFactory(context: Context): androidx.lifecycle.ViewModelProvider.Factory {
    return object : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(context) as T
        }
    }
}
