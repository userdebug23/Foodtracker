package com.foodtracker.ui.screens.payments

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodtracker.data.database.AppDatabase
import com.foodtracker.data.entities.PaymentEntity
import com.foodtracker.data.repository.PaymentRepository
import com.foodtracker.data.repository.FoodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class PaymentViewModel(private val context: Context) : ViewModel() {
    
    private val _state = MutableStateFlow(PaymentState())
    val state: StateFlow<PaymentState> = _state.asStateFlow()
    
    private val database = AppDatabase.getInstance(context)
    private val paymentRepository = PaymentRepository(database.paymentDao())
    private val foodRepository = FoodRepository(database.foodEntryDao())
    
    init {
        loadPayments()
    }
    
    fun loadPayments() {
        viewModelScope.launch {
            try {
                val allPayments = paymentRepository.getAllPayments()
                val completedPayments = allPayments.filter { it.status == "Completed" }
                val pendingPayments = allPayments.filter { it.status == "Pending" }
                val overduePayments = allPayments.filter { 
                    it.status == "Pending" && it.paymentDate.isBefore(LocalDate.now()) 
                }
                
                val totalPaid = completedPayments.sumOf { it.amount }
                
                // Calculate monthly expense
                val currentMonth = YearMonth.now()
                val startDate = currentMonth.atDay(1)
                val endDate = currentMonth.atEndOfMonth()
                val monthlyExpense = foodRepository.getMonthlySummary(currentMonth).totalExpense
                val monthlyPaid = paymentRepository.getTotalCompletedPaymentsBetween(startDate, endDate)
                
                val balance = monthlyExpense - monthlyPaid
                
                _state.update {
                    it.copy(
                        payments = allPayments,
                        completedPayments = completedPayments,
                        pendingPayments = pendingPayments,
                        overduePayments = overduePayments,
                        totalPaid = totalPaid,
                        monthlyExpense = monthlyExpense,
                        monthlyPaid = monthlyPaid,
                        balance = balance,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
    
    fun addPayment(amount: Double, paymentMethod: String, remarks: String, paymentDate: LocalDate) {
        viewModelScope.launch {
            try {
                val today = LocalDate.now()
                val isScheduled = paymentDate.isAfter(today)
                val status = if (isScheduled) "Pending" else "Completed"
                
                val payment = PaymentEntity(
                    paymentDate = paymentDate,
                    amount = amount,
                    paymentMethod = paymentMethod.ifEmpty { "Cash" },
                    remarks = remarks,
                    isScheduled = isScheduled,
                    status = status
                )
                paymentRepository.addPayment(payment)
                // ✅ Force refresh after adding
                loadPayments()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun markPaymentAsCompleted(paymentId: Long) {
        viewModelScope.launch {
            try {
                paymentRepository.markPaymentAsCompleted(paymentId)
                // ✅ Force refresh after marking as completed
                loadPayments()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun deletePayment(paymentId: Long) {
        viewModelScope.launch {
            try {
                paymentRepository.deletePayment(paymentId)
                // ✅ Force refresh after deleting
                loadPayments()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun refresh() {
        loadPayments()
    }
    
    companion object {
        fun provideFactory(context: Context): androidx.lifecycle.ViewModelProvider.Factory {
            return object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PaymentViewModel(context) as T
                }
            }
        }
    }
}

data class PaymentState(
    val payments: List<PaymentEntity> = emptyList(),
    val completedPayments: List<PaymentEntity> = emptyList(),
    val pendingPayments: List<PaymentEntity> = emptyList(),
    val overduePayments: List<PaymentEntity> = emptyList(),
    val totalPaid: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val monthlyPaid: Double = 0.0,
    val balance: Double = 0.0,
    val isLoading: Boolean = true
)
