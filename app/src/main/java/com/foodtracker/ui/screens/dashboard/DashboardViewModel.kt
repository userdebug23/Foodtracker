package com.foodtracker.ui.screens.dashboard

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodtracker.data.database.AppDatabase
import com.foodtracker.data.entities.FoodEntry
import com.foodtracker.data.repository.FoodRepository
import com.foodtracker.data.repository.PaymentRepository
import com.foodtracker.utils.DateUtils
import com.foodtracker.utils.NumberUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class DashboardViewModel(private val context: Context) : ViewModel() {
    
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()
    
    private val database = AppDatabase.getInstance(context)
    private val foodRepository = FoodRepository(database.foodEntryDao())
    private val paymentRepository = PaymentRepository(database.paymentDao())
    
    init {
        refresh(LocalDate.now())
    }
    
    // ✅ Updated to accept a date parameter
    fun refresh(selectedDate: LocalDate = LocalDate.now()) {
        loadDashboard(selectedDate)
    }
    
    private fun loadDashboard(selectedDate: LocalDate) {
        viewModelScope.launch {
            try {
                val dailyRate = NumberUtils.getDailyRate(context)
                val currentMonth = YearMonth.from(selectedDate)
                
                Log.d("DashboardVM", "Loading for date: $selectedDate")
                
                // Get entry for selected date
                val selectedEntry = foodRepository.getEntry(selectedDate)
                
                // Get current month summary
                val summary = foodRepository.getMonthlySummary(currentMonth)
                
                // Get recent entries (7 days around selected date)
                val startDate = selectedDate.minusDays(7)
                val endDate = selectedDate.plusDays(1)
                val recentEntries = foodRepository.getEntriesBetween(startDate, endDate)
                
                // Get ALL entries for total expense (all time)
                val allEntries = foodRepository.getEntriesBetween(
                    LocalDate.of(2024, 1, 1),
                    LocalDate.now()
                )
                val totalExpenseAllTime = allEntries.sumOf { it.dailyExpense }
                
                // Get total paid
                val totalPaid = paymentRepository.getTotalCompletedPayments()
                
                // Balance = Total Paid - Total Expense
                val balance = totalPaid - totalExpenseAllTime
                
                // Remaining Days = Balance / Daily Rate
                val remainingDays = if (dailyRate > 0) (balance / dailyRate).toInt() else 0
                
                _state.update {
                    it.copy(
                        // Selected date data
                        todayBreakfast = selectedEntry?.breakfast ?: false,
                        todayLunch = selectedEntry?.lunch ?: false,
                        todayDinner = selectedEntry?.dinner ?: false,
                        todayMeals = selectedEntry?.mealCount ?: 0,
                        todayExpense = selectedEntry?.dailyExpense ?: 0.0,
                        // Monthly data
                        totalMeals = summary.totalMeals,
                        breakfastCount = summary.totalBreakfast,
                        lunchCount = summary.totalLunch,
                        dinnerCount = summary.totalDinner,
                        totalExpense = summary.totalExpense,
                        averageDailyExpense = summary.averageDailyExpense,
                        presentDays = summary.presentDays,
                        // Balance data
                        balance = balance,
                        remainingDays = remainingDays,
                        dailyRate = dailyRate,
                        // Recent entries
                        recentEntries = recentEntries.sortedByDescending { it.date }.take(7)
                    )
                }
            } catch (e: Exception) {
                Log.e("DashboardVM", "Error loading dashboard: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    companion object {
        fun provideFactory(context: Context): androidx.lifecycle.ViewModelProvider.Factory {
            return object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DashboardViewModel(context) as T
                }
            }
        }
    }
}

data class DashboardState(
    val todayBreakfast: Boolean = false,
    val todayLunch: Boolean = false,
    val todayDinner: Boolean = false,
    val todayMeals: Int = 0,
    val todayExpense: Double = 0.0,
    val totalMeals: Int = 0,
    val breakfastCount: Int = 0,
    val lunchCount: Int = 0,
    val dinnerCount: Int = 0,
    val totalExpense: Double = 0.0,
    val averageDailyExpense: Double = 0.0,
    val presentDays: Int = 0,
    val balance: Double = 0.0,
    val remainingDays: Int = 0,
    val dailyRate: Double = 160.0,
    val recentEntries: List<FoodEntry> = emptyList()
)
