package com.foodtracker.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.foodtracker.utils.DateUtils
import com.foodtracker.utils.NumberUtils
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun DashboardScreen(
    selectedDate: LocalDate = LocalDate.now()  // ✅ Allow passing a date
) {
    val context = LocalContext.current
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.provideFactory(context)
    )
    
    val state by viewModel.state.collectAsState()
    
    // ✅ Refresh when selected date changes
    LaunchedEffect(selectedDate) {
        viewModel.refresh(selectedDate)
    }
    
    // Auto-refresh every 30 seconds
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(30000)
            viewModel.refresh(selectedDate)
        }
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (selectedDate == LocalDate.now()) "📊 Dashboard" else "📊 ${DateUtils.formatDate(selectedDate)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (selectedDate == LocalDate.now()) 
                            DateUtils.formatMonthYear(DateUtils.getCurrentMonth()) 
                        else 
                            DateUtils.formatDate(selectedDate),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
                // Refresh button
                IconButton(
                    onClick = { viewModel.refresh(selectedDate) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Text("🔄", fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Today's Meals Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = if (selectedDate == LocalDate.now()) "Today's Meals" else "Meals on ${DateUtils.formatDate(selectedDate)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MealStatus(
                            label = "Breakfast",
                            isPresent = state.todayBreakfast
                        )
                        MealStatus(
                            label = "Lunch",
                            isPresent = state.todayLunch
                        )
                        MealStatus(
                            label = "Dinner",
                            isPresent = state.todayDinner
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Daily Expense",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Text(
                            text = NumberUtils.formatCurrency(state.todayExpense),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Meals",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "${state.todayMeals}/3",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        // Balance & Remaining Days
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.balance >= 0) 
                            Color(0xFF4CAF50).copy(alpha = 0.15f)
                        else 
                            Color(0xFFF44336).copy(alpha = 0.15f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("💰 Balance", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        Text(
                            text = NumberUtils.formatCurrency(state.balance),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (state.balance >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                        Text(
                            text = if (state.balance >= 0) "Available" else "Overdue",
                            fontSize = 10.sp,
                            color = if (state.balance >= 0) Color(0xFF4CAF50).copy(alpha = 0.7f) else Color(0xFFF44336).copy(alpha = 0.7f)
                        )
                    }
                }
                
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2196F3).copy(alpha = 0.15f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📅 Days Left", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        Text(
                            text = "${state.remainingDays}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )
                        Text(
                            text = "@ ₹${String.format("%.0f", state.dailyRate)}/day",
                            fontSize = 10.sp,
                            color = Color(0xFF2196F3).copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

// Last Payment Card
if (state.lastPaymentDate.isNotEmpty()) {
    item {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF9C27B0).copy(alpha = 0.12f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "💳 Last Payment",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = state.lastPaymentDate,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = NumberUtils.formatCurrency(state.lastPaymentAmount),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9C27B0)
                )
            }
        }
    }
}
        
        // Monthly Summary
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📊 Monthly Summary",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.totalMeals.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("Total Meals", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = NumberUtils.formatCurrency(state.totalExpense),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("Total Expense", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${state.presentDays}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                            Text("Days Present", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        }
                    }
                }
            }
        }
        
        // Recent Entries
        if (state.recentEntries.isNotEmpty()) {
            item {
                Text(
                    text = "Recent Entries",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            
            items(state.recentEntries) { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = DateUtils.formatDate(entry.date),
                                fontWeight = FontWeight.Medium
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MealDot(entry.breakfast, "B")
                                MealDot(entry.lunch, "L")
                                MealDot(entry.dinner, "D")
                            }
                        }
                        Text(
                            text = NumberUtils.formatCurrency(entry.dailyExpense),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MealStatus(
    label: String,
    isPresent: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isPresent) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isPresent) "✓" else "✗",
                fontSize = 18.sp,
                color = if (isPresent) 
                    MaterialTheme.colorScheme.onPrimary 
                else 
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
        }
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun MealDot(isPresent: Boolean, label: String) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                if (isPresent) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isPresent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
            fontWeight = FontWeight.Bold
        )
    }
}
