package com.foodtracker.ui.screens.payments

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.foodtracker.data.entities.PaymentEntity
import com.foodtracker.utils.NumberUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PaymentScreen() {
    val context = LocalContext.current
    val viewModel: PaymentViewModel = viewModel(
        factory = PaymentViewModel.provideFactory(context)
    )
    
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedPaymentId by remember { mutableStateOf<Long?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }
    
    // Refresh handler
    fun refresh() {
        isRefreshing = true
        viewModel.refresh()
        isRefreshing = false
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header with refresh button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💰 Payments",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Refresh Button
                    IconButton(
                        onClick = { refresh() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text("🔄", fontSize = 20.sp)
                    }
                    // Add Button
                    FloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Text("+", fontSize = 24.sp, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        
        // Pending Payments Alert
        if (state.pendingPayments.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFC107).copy(alpha = 0.15f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⏳", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${state.pendingPayments.size} pending payment(s)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFFFC107)
                            )
                        }
                        Text(
                            text = NumberUtils.formatCurrency(state.pendingPayments.sumOf { it.amount }),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFC107)
                        )
                    }
                }
            }
        }
        
        // Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryCard(
                    title = "Total Paid",
                    amount = state.totalPaid,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Balance",
                    amount = state.balance,
                    color = if (state.balance >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryCard(
                    title = "Monthly Expense",
                    amount = state.monthlyExpense,
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Monthly Paid",
                    amount = state.monthlyPaid,
                    color = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Payments List
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Payment History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${state.payments.size} entries",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        if (state.payments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💳", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No payments yet",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Tap the + button to add your first payment",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        } else {
            items(state.payments) { payment ->
                PaymentItem(
                    payment = payment,
                    onComplete = {
                        if (payment.status == "Pending") {
                            viewModel.markPaymentAsCompleted(payment.id)
                        }
                    },
                    onDelete = { 
                        selectedPaymentId = payment.id
                        showDeleteDialog = true
                    }
                )
            }
        }
        
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
    
    if (showAddDialog) {
        AddPaymentDialog(
            onDismiss = { showAddDialog = false },
            onAddPayment = { amount, method, remarks, date ->
                viewModel.addPayment(amount, method, remarks, date)
                showAddDialog = false
                // Refresh after dialog closes
                refresh()
            }
        )
    }
    
    if (showDeleteDialog && selectedPaymentId != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false
                selectedPaymentId = null
            },
            title = { Text("Delete Payment") },
            text = { Text("Are you sure you want to delete this payment?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedPaymentId?.let { viewModel.deletePayment(it) }
                        showDeleteDialog = false
                        selectedPaymentId = null
                        // Refresh after delete
                        refresh()
                    }
                ) {
                    Text("Delete", color = Color(0xFFF44336))
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showDeleteDialog = false
                    selectedPaymentId = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Text(
                text = NumberUtils.formatCurrency(amount),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun PaymentItem(
    payment: PaymentEntity,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    var showOptions by remember { mutableStateOf(false) }
    
    val isPending = payment.status == "Pending"
    val isOverdue = isPending && payment.paymentDate.isBefore(LocalDate.now())
    val statusColor = when {
        isOverdue -> Color(0xFFF44336)
        isPending -> Color(0xFFFFC107)
        else -> Color(0xFF4CAF50)
    }
    val statusText = when {
        isOverdue -> "Overdue"
        isPending -> "Pending"
        else -> "Completed"
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showOptions = true },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) 
                MaterialTheme.colorScheme.error.copy(alpha = 0.05f)
            else if (isPending)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            else
                MaterialTheme.colorScheme.surfaceVariant
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (payment.paymentMethod) {
                            "Cash" -> "💵"
                            "UPI" -> "📱"
                            "Bank Transfer" -> "🏦"
                            "Card" -> "💳"
                            else -> "💰"
                        },
                        fontSize = 20.sp
                    )
                    Text(
                        text = payment.paymentDate.format(
                            java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")
                        ),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    // Status badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }
                Text(
                    text = payment.paymentMethod,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                if (payment.remarks.isNotEmpty()) {
                    Text(
                        text = "📝 ${payment.remarks}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                }
            }
            Text(
                text = NumberUtils.formatCurrency(payment.amount),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPending) Color(0xFFFFC107) else MaterialTheme.colorScheme.primary
            )
        }
    }
    
    // Options Dialog
    if (showOptions) {
        AlertDialog(
            onDismissRequest = { showOptions = false },
            title = { Text("Payment Options") },
            text = {
                Column {
                    Text("Amount: ${NumberUtils.formatCurrency(payment.amount)}")
                    Text("Date: ${payment.paymentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"))}")
                    Text("Method: ${payment.paymentMethod}")
                    if (payment.remarks.isNotEmpty()) {
                        Text("Note: ${payment.remarks}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Status: $statusText", color = statusColor)
                }
            },
            confirmButton = {
                if (payment.status == "Pending") {
                    TextButton(
                        onClick = {
                            onComplete()
                            showOptions = false
                        }
                    ) {
                        Text("✅ Mark Completed", color = Color(0xFF4CAF50))
                    }
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            onDelete()
                            showOptions = false
                        }
                    ) {
                        Text("🗑️ Delete", color = Color(0xFFF44336))
                    }
                    TextButton(onClick = { showOptions = false }) {
                        Text("Close")
                    }
                }
            }
        )
    }
}
