package com.foodtracker.ui.screens.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.window.Dialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AddPaymentDialog(
    onDismiss: () -> Unit,
    onAddPayment: (Double, String, String, LocalDate) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var remarks by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val paymentMethods = listOf("Cash", "UPI", "Bank Transfer", "Card", "Other")
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "💰 Add Payment",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Enter payment details below",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Amount Input
                OutlinedTextField(
                    value = amount,
                    onValueChange = { 
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amount = it
                        }
                    },
                    label = { Text("Amount (₹)") },
                    placeholder = { Text("Enter amount") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Payment Date - Click to select
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Payment Date",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                            Text(
                                text = selectedDate.format(dateFormatter),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = "📅",
                            fontSize = 20.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Payment Method
                var expanded by remember { mutableStateOf(false) }
                
                OutlinedTextField(
                    value = paymentMethod,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Payment Method") },
                    trailingIcon = { 
                        Text(
                            if (expanded) "▲" else "▼",
                            fontSize = 16.sp,
                            modifier = Modifier.clickable { expanded = !expanded }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    paymentMethods.forEach { method ->
                        DropdownMenuItem(
                            text = { Text(method) },
                            onClick = {
                                paymentMethod = method
                                expanded = false
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Remarks Input
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Remarks / Notes") },
                    placeholder = { Text("e.g., Monthly payment for July") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Show status indicator for scheduled payments
                val isScheduled = selectedDate.isAfter(LocalDate.now())
                if (isScheduled) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⏳", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Scheduled for ${selectedDate.format(dateFormatter)}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Summary
                if (amount.isNotEmpty() && amount.toDoubleOrNull() != null) {
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
                            Text(
                                text = "Total Payment",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "₹$amount",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Buttons
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
                            val amountValue = amount.toDoubleOrNull()
                            if (amountValue != null && amountValue > 0) {
                                onAddPayment(amountValue, paymentMethod, remarks, selectedDate)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = amount.toDoubleOrNull() != null && amount.toDoubleOrNull()!! > 0
                    ) {
                        Text(if (selectedDate.isAfter(LocalDate.now())) "Schedule" else "Add Payment")
                    }
                }
            }
        }
    }
    
    // Simple Date Picker Dialog
    if (showDatePicker) {
        SimpleDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            },
            initialDate = selectedDate
        )
    }
}

@Composable
fun SimpleDatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    initialDate: LocalDate
) {
    var year by remember { mutableStateOf(initialDate.year) }
    var month by remember { mutableStateOf(initialDate.monthValue) }
    var day by remember { mutableStateOf(initialDate.dayOfMonth) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Date") },
        text = {
            Column {
                // Month/Year navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { 
                        if (month == 1) {
                            month = 12
                            year--
                        } else {
                            month--
                        }
                    }) {
                        Text("◀")
                    }
                    Text(
                        text = "${getMonthName(month)} $year",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { 
                        if (month == 12) {
                            month = 1
                            year++
                        } else {
                            month++
                        }
                    }) {
                        Text("▶")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Day headers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { dayLabel ->
                        Text(
                            text = dayLabel,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Days grid
                val daysInMonth = getDaysInMonth(month, year)
                val firstDayOfWeek = getFirstDayOfWeek(month, year)
                
                var dayCounter = 1
                var weekCounter = 0
                
                while (weekCounter < 6 && dayCounter <= daysInMonth) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (i in 0..6) {
                            val isDayVisible = if (weekCounter == 0) {
                                i >= firstDayOfWeek
                            } else {
                                true
                            }
                            
                            val currentDay = if (isDayVisible) dayCounter else 0
                            
                            if (isDayVisible && currentDay <= daysInMonth) {
                                val isSelected = currentDay == day && month == initialDate.monthValue && year == initialDate.year
                                val isToday = currentDay == LocalDate.now().dayOfMonth && 
                                             month == LocalDate.now().monthValue && 
                                             year == LocalDate.now().year
                                
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                            else if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                            else Color.Transparent,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .clickable {
                                            day = currentDay
                                            onDateSelected(LocalDate.of(year, month, currentDay))
                                        },
                                    contentAlignment = androidx.compose.ui.Alignment.Center
                                ) {
                                    Text(
                                        text = currentDay.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (currentDay > 0 && LocalDate.of(year, month, currentDay).isBefore(LocalDate.now())) 
                                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                        else MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                dayCounter++
                            } else {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                )
                            }
                        }
                    }
                    weekCounter++
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onDateSelected(LocalDate.of(year, month, day)) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Helper functions
fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        else -> "December"
    }
}

fun getDaysInMonth(month: Int, year: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> 30
    }
}

fun getFirstDayOfWeek(month: Int, year: Int): Int {
    val date = LocalDate.of(year, month, 1)
    return date.dayOfWeek.value % 7
}
