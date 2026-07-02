package com.foodtracker.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodtracker.data.local.entities.FoodEntryEntity
import com.foodtracker.domain.repository.FoodEntryRepository
import com.foodtracker.domain.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: FoodEntryRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()
    
    init {
        loadDashboard()
    }
    
    private fun loadDashboard() {
        viewModelScope.launch {
            val currentMonth = YearMonth.now()
            val today = LocalDate.now()
            
            val todayEntry = repository.getEntry(today)
            val todayBreakfast = todayEntry?.breakfast ?: false
            val todayLunch = todayEntry?.lunch ?: false
            val todayDinner = todayEntry?.dinner ?: false
            val todayExpense = todayEntry?.dailyExpense ?: 0.0
            
            val summary = repository.getMonthlySummary(currentMonth)
            
            val startDate = today.minusDays(6)
            val recentEntries = repository.getEntriesBetween(startDate, today)
                .reversed()
                .take(7)
            
            val startOfMonth = currentMonth.atDay(1)
            val endOfMonth = currentMonth.atEndOfMonth()
            val paidAmount = paymentRepository.getTotalPaymentsBetween(startOfMonth, endOfMonth)
            
            _state.update {
                it.copy(
                    todayBreakfast = todayBreakfast,
                    todayLunch = todayLunch,
                    todayDinner = todayDinner,
                    todayExpense = todayExpense,
                    totalMeals = summary.totalMeals,
                    breakfastCount = summary.totalBreakfast,
                    lunchCount = summary.totalLunch,
                    dinnerCount = summary.totalDinner,
                    totalExpense = summary.totalExpense,
                    paidAmount = paidAmount,
                    balance = summary.totalExpense - paidAmount,
                    recentEntries = recentEntries,
                    isLoading = false
                )
            }
        }
    }
}

data class DashboardState(
    val todayBreakfast: Boolean = false,
    val todayLunch: Boolean = false,
    val todayDinner: Boolean = false,
    val todayExpense: Double = 0.0,
    val totalMeals: Int = 0,
    val breakfastCount: Int = 0,
    val lunchCount: Int = 0,
    val dinnerCount: Int = 0,
    val totalExpense: Double = 0.0,
    val paidAmount: Double = 0.0,
    val balance: Double = 0.0,
    val recentEntries: List<FoodEntryEntity> = emptyList(),
    val isLoading: Boolean = true
)
