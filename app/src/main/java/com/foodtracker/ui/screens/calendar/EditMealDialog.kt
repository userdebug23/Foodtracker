package com.foodtracker.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.foodtracker.data.database.AppDatabase
import com.foodtracker.data.entities.FoodEntry
import com.foodtracker.data.repository.FoodRepository
import com.foodtracker.utils.DateUtils
import com.foodtracker.utils.NumberUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Composable
fun EditMealDialog(
    date: LocalDate,
    onDismiss: () -> Unit,
    onMealUpdated: () -> Unit
) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)
    val repository = FoodRepository(database.foodEntryDao())
    
    var entry by remember { mutableStateOf<FoodEntry?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    var breakfast by remember { mutableStateOf(false) }
    var lunch by remember { mutableStateOf(false) }
    var dinner by remember { mutableStateOf(false) }
    
    LaunchedEffect(date) {
        withContext(Dispatchers.IO) {
            entry = repository.getEntry(date)
            breakfast = entry?.breakfast ?: false
            lunch = entry?.lunch ?: false
            dinner = entry?.dinner ?: false
            isLoading = false
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📝 Edit Meals",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = DateUtils.formatDate(date),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                
                Text(
                    text = DateUtils.getDayOfWeek(date),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    MealToggleRow(
                        label = "Breakfast",
                        isChecked = breakfast,
                        onToggle = { breakfast = it }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    MealToggleRow(
                        label = "Lunch",
                        isChecked = lunch,
                        onToggle = { lunch = it }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    MealToggleRow(
                        label = "Dinner",
                        isChecked = dinner,
                        onToggle = { dinner = it }
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Calculate meal count
                    val mealCount = NumberUtils.calculateMealCount(breakfast, lunch, dinner)
                    
                    // Calculate expense using daily rate
                    val mealRate = NumberUtils.getMealRate(context)
                    val dailyExpense = mealCount * mealRate
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Total Meals",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "$mealCount/3",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Daily Expense",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = NumberUtils.formatCurrency(dailyExpense),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        
                        Button(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val newEntry = FoodEntry(
                                        id = entry?.id ?: 0,
                                        date = date,
                                        dayOfWeek = DateUtils.getDayOfWeek(date),
                                        breakfast = breakfast,
                                        lunch = lunch,
                                        dinner = dinner,
                                        mealCount = mealCount,
                                        dailyExpense = dailyExpense
                                    )
                                    repository.saveEntry(newEntry)
                                    withContext(Dispatchers.Main) {
                                        onMealUpdated()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MealToggleRow(
    label: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isChecked) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isChecked) {
                    Text(
                        text = "✓",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        )
    }
}
