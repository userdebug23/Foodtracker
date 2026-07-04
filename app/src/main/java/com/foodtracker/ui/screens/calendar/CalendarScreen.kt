package com.foodtracker.ui.screens.calendar

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.foodtracker.utils.NumberUtils
import java.time.LocalDate

@Composable
fun CalendarScreen() {
    val context = LocalContext.current
    val viewModel: CalendarViewModel = viewModel(
        factory = CalendarViewModel.provideFactory(context)
    )
    
    val state by viewModel.state.collectAsState()
    
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅 Calendar",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Button(
                    onClick = { viewModel.goToToday() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                ) {
                    Text("Today", color = MaterialTheme.colorScheme.primary)
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateMonth(-1) }) {
                    Text("◀", fontSize = 24.sp)
                }
                Text(
                    text = state.monthTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                IconButton(onClick = { viewModel.navigateMonth(1) }) {
                    Text("▶", fontSize = 24.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                    Text(
                        text = day,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        
        items(state.weeks.size) { weekIndex ->
            val week = state.weeks[weekIndex]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                week.forEach { day ->
                    CalendarDayCell(
                        day = day,
                        onDayClick = {
                            if (day.isCurrentMonth) {
                                selectedDate = day.date
                                showEditDialog = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                    )
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(8.dp)) }
        
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📊 Month Summary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.totalMeals.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("Total Meals", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = NumberUtils.formatCurrency(state.totalExpense),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("Total Expense", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LegendItemWithCount("Present", state.presentDays.toString(), Color(0xFF4CAF50))
                        LegendItemWithCount("Partial", state.partialDays.toString(), Color(0xFFFFC107))
                        LegendItemWithCount("Absent", state.absentDays.toString(), Color(0xFFBDBDBD))
                    }
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(8.dp)) }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem("All Meals", Color(0xFF4CAF50))
                LegendItem("Partial", Color(0xFFFFC107))
                LegendItem("No Meals", Color(0xFFBDBDBD))
            }
        }
        
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
    
    if (showEditDialog && selectedDate != null) {
        EditMealDialog(
            date = selectedDate!!,
            onDismiss = { showEditDialog = false },
            onMealUpdated = {
                showEditDialog = false
                viewModel.refresh()
            }
        )
    }
}

@Composable
fun CalendarDayCell(
    day: CalendarDay,
    onDayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dayColor = if (day.isCurrentMonth) {
        when (day.mealCount) {
            3 -> Color(0xFF4CAF50)
            0 -> Color(0xFFE0E0E0)
            else -> Color(0xFFFFC107)
        }
    } else {
        Color(0xFFF5F5F5)
    }
    
    val isToday = day.date == LocalDate.now() && day.isCurrentMonth
    
    Card(
        modifier = modifier.clickable { onDayClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = dayColor),
        border = if (isToday) {
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                fontSize = 14.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (day.isCurrentMonth && day.mealCount == 3) Color.White 
                    else if (day.isCurrentMonth) Color.Black 
                    else Color.Gray
            )
            if (day.isCurrentMonth && day.mealCount > 0) {
                Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                    if (day.breakfast) Text("B", fontSize = 7.sp, color = if (day.mealCount == 3) Color.White else Color.Black)
                    if (day.lunch) Text("L", fontSize = 7.sp, color = if (day.mealCount == 3) Color.White else Color.Black)
                    if (day.dinner) Text("D", fontSize = 7.sp, color = if (day.mealCount == 3) Color.White else Color.Black)
                }
            }
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(4.dp)).background(color))
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
    }
}

@Composable
fun LegendItemWithCount(label: String, count: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp)).background(color))
        Text(text = count, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
    }
}
