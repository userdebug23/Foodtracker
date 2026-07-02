package com.foodtracker.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.foodtracker.ui.components.SummaryCard
import com.foodtracker.utils.DateUtils
import com.foodtracker.utils.NumberUtils

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Dashboard",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = DateUtils.formatMonthYear(DateUtils.getCurrentMonth()),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
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
                        text = "Today",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TodayMealItem("Breakfast", state.todayBreakfast)
                        TodayMealItem("Lunch", state.todayLunch)
                        TodayMealItem("Dinner", state.todayDinner)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Today's Expense",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Text(
                            text = NumberUtils.formatCurrency(state.todayExpense),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        item {
            SummaryCard(
                title = "Monthly Summary",
                items = listOf(
                    "Total Meals" to state.totalMeals.toString(),
                    "Breakfast" to state.breakfastCount.toString(),
                    "Lunch" to state.lunchCount.toString(),
                    "Dinner" to state.dinnerCount.toString(),
                    "Total Expense" to NumberUtils.formatCurrency(state.totalExpense),
                    "Paid" to NumberUtils.formatCurrency(state.paidAmount),
                    "Balance" to NumberUtils.formatCurrency(state.balance)
                )
            )
        }
        
        item {
            Text(
                text = "Recent Entries",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        items(state.recentEntries) { entry ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Navigate to entry detail */ },
                shape = RoundedCornerShape(12.dp)
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
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
        
        item {
            if (state.recentEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No entries found",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun TodayMealItem(label: String, isPresent: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isPresent) Color(0xFF4CAF50)
                    else Color(0xFFBDBDBD)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isPresent) "✓" else "✗",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
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
                if (isPresent) Color(0xFF4CAF50)
                else Color(0xFFBDBDBD)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
