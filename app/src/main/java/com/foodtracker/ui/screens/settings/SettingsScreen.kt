package com.foodtracker.ui.screens.settings

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
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.launch

// DataStore extension
private val android.content.Context.dataStore by preferencesDataStore(name = "food_settings")
private val DAILY_AMOUNT_KEY = doublePreferencesKey("daily_amount")
private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Read settings from DataStore
    var dailyAmount by remember { mutableStateOf(160.0) }
    var isDarkTheme by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    
    // Load settings
    LaunchedEffect(Unit) {
        try {
            context.dataStore.data.collect { preferences ->
                dailyAmount = preferences[DAILY_AMOUNT_KEY] ?: 160.0
                isDarkTheme = preferences[DARK_THEME_KEY] ?: false
                isLoading = false
            }
        } catch (e: Exception) {
            isLoading = false
        }
    }
    
    fun saveDailyAmount(amount: Double) {
        scope.launch {
            context.dataStore.edit { preferences ->
                preferences[DAILY_AMOUNT_KEY] = amount
            }
            dailyAmount = amount
            Toast.makeText(context, "Daily amount updated to ₹${String.format("%.2f", amount)}", Toast.LENGTH_SHORT).show()
        }
    }
    
    fun toggleTheme() {
        scope.launch {
            val newTheme = !isDarkTheme
            context.dataStore.edit { preferences ->
                preferences[DARK_THEME_KEY] = newTheme
            }
            isDarkTheme = newTheme
            Toast.makeText(context, if (newTheme) "Dark mode enabled" else "Light mode enabled", Toast.LENGTH_SHORT).show()
            // Recreate activity to apply theme
            (context as? androidx.activity.ComponentActivity)?.recreate()
        }
    }
    
    val perMealRate = dailyAmount / 3
    
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
        
        // In the SettingsScreen, replace the Daily Amount section with:

// Daily Rate Section
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
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "This is the cost per full day (3 meals)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}
                
                Spacer(modifier = Modifier.height(8.dp))
                
                SettingsItem(
                    icon = "📊",
                    title = "Per Meal Rate",
                    subtitle = "₹${String.format("%.2f", perMealRate)} per meal"
                )
            }
        }
        
        // Theme Section
        item {
            SettingsSection(title = "🎨 Appearance") {
                SettingsSwitch(
                    icon = if (isDarkTheme) "🌙" else "☀️",
                    title = "Dark Mode",
                    subtitle = if (isDarkTheme) "Currently in Dark mode" else "Currently in Light mode",
                    checked = isDarkTheme,
                    onCheckedChange = { toggleTheme() }
                )
            }
        }
        
        // Data Management Section
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
        
        // About Section
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
    
    // Daily Amount Dialog
    if (showDialog) {
        DailyAmountDialog(
            currentAmount = dailyAmount,
            onDismiss = { showDialog = false },
            onSave = { newAmount ->
                saveDailyAmount(newAmount)
                showDialog = false
            }
        )
    }
}

@Composable
fun MealBreakdownItem(
    label: String,
    amount: Double,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 20.sp)
        Text(
            text = "₹${String.format("%.2f", amount)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
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
fun DailyAmountDialog(
    currentAmount: Double,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf(String.format("%.0f", currentAmount)) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Daily Budget") },
        text = {
            Column {
                Text(
                    text = "Enter your daily food budget",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This will be split equally between meals",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { 
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amountText = it
                        }
                    },
                    label = { Text("Daily Amount (₹)") },
                    placeholder = { Text("Enter amount") },
                    singleLine = true,
                    leadingIcon = { Text("₹", fontSize = 18.sp) }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                val amount = amountText.toDoubleOrNull()
                if (amount != null && amount > 0) {
                    val perMeal = amount / 3
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🌅", fontSize = 16.sp)
                                Text(
                                    text = "₹${String.format("%.2f", perMeal)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("Breakfast", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("☀️", fontSize = 16.sp)
                                Text(
                                    text = "₹${String.format("%.2f", perMeal)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("Lunch", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🌙", fontSize = 16.sp)
                                Text(
                                    text = "₹${String.format("%.2f", perMeal)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("Dinner", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        onSave(amount)
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
