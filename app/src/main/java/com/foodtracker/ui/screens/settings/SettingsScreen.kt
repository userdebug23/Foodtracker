package com.foodtracker.ui.screens.settings

import android.content.Context
import android.widget.Toast
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
import androidx.core.content.edit
import com.foodtracker.utils.ThemeManager  // ✅ ADD THIS IMPORT

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("food_tracker_settings", Context.MODE_PRIVATE)
    
    var dailyRate by remember { mutableStateOf(prefs.getFloat("daily_rate", 160f).toDouble()) }
    var isDarkTheme by remember { mutableStateOf(ThemeManager.isDarkTheme(context)) }  // ✅ Use ThemeManager
    var showDialog by remember { mutableStateOf(false) }
    
    fun saveDailyRate(rate: Double) {
        prefs.edit { putFloat("daily_rate", rate.toFloat()) }
        dailyRate = rate
        Toast.makeText(context, "Daily rate updated to ₹${String.format("%.2f", rate)}", Toast.LENGTH_SHORT).show()
    }
    
    val perMealRate = dailyRate / 3
    
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
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Configure your daily food budget",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        item {
            SettingsSection(title = "💰 Daily Rate") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Daily Rate",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "₹${String.format("%.2f", dailyRate)}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Button(
                                onClick = { showDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Change")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Per Meal: ₹${String.format("%.2f", perMealRate)}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
        
        // ✅ FIXED: Appearance Section - Dark Mode Toggle
        item {
            SettingsSection(title = "🎨 Appearance") {
                SettingsSwitch(
                    icon = if (isDarkTheme) "🌙" else "☀️",
                    title = "Dark Mode",
                    subtitle = if (isDarkTheme) "Currently in Dark mode" else "Currently in Light mode",
                    checked = isDarkTheme,
                    onCheckedChange = { 
                        // Toggle theme
                        val newTheme = !isDarkTheme
                        ThemeManager.setDarkTheme(context, newTheme)
                        isDarkTheme = newTheme
                        
                        // Show toast
                        Toast.makeText(
                            context, 
                            if (newTheme) "🌙 Dark mode enabled" else "☀️ Light mode enabled",
                            Toast.LENGTH_SHORT
                        ).show()
                        
                        // Recreate activity to apply theme
                        (context as? androidx.activity.ComponentActivity)?.recreate()
                    }
                )
            }
        }
        
        item {
            SettingsSection(title = "💾 Data Management") {
                SettingsItem(
                    icon = "📤",
                    title = "Export Data",
                    subtitle = "Export to Excel file",
                    onClick = {
                        Toast.makeText(context, "Export feature coming soon", Toast.LENGTH_SHORT).show()
                    }
                )
                
                Divider()
                
                SettingsItem(
                    icon = "📥",
                    title = "Import Data",
                    subtitle = "Import from Excel file",
                    onClick = {
                        Toast.makeText(context, "Import feature coming soon", Toast.LENGTH_SHORT).show()
                    }
                )
                
                Divider()
                
                SettingsItem(
                    icon = "🗑️",
                    title = "Reset Database",
                    subtitle = "Delete all data",
                    onClick = {
                        Toast.makeText(context, "Reset feature coming soon", Toast.LENGTH_SHORT).show()
                    },
                    isDanger = true
                )
            }
        }
        
        item {
            SettingsSection(title = "📱 About") {
                SettingsItem(
                    icon = "📱",
                    title = "App Version",
                    subtitle = "1.0.0"
                )
                
                Divider()
                
                SettingsItem(
                    icon = "❤️",
                    title = "Food Tracker",
                    subtitle = "Track your daily meals and expenses"
                )
            }
        }
        
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
    
    if (showDialog) {
        DailyRateDialog(
            currentRate = dailyRate,
            onDismiss = { showDialog = false },
            onSave = { newRate ->
                saveDailyRate(newRate)
                showDialog = false
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
        Text(text = icon, fontSize = 22.sp)
        
        Column(modifier = Modifier.weight(1f)) {
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
            Text(text = "›", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
        }
    }
}

@Composable
fun SettingsSwitch(
    icon: String,
    title: String,
    subtitle: String? = null,
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
        Text(text = icon, fontSize = 22.sp)
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
        
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
fun DailyRateDialog(
    currentRate: Double,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var rateText by remember { mutableStateOf(String.format("%.0f", currentRate)) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Daily Rate") },
        text = {
            Column {
                Text(
                    text = "Enter your daily food budget",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This will be split equally: ₹${rateText.toDoubleOrNull()?.div(3)?.let { String.format("%.2f", it) } ?: "0.00"} per meal",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = rateText,
                    onValueChange = { 
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            rateText = it
                        }
                    },
                    label = { Text("Daily Rate (₹)") },
                    placeholder = { Text("Enter amount per day") },
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
