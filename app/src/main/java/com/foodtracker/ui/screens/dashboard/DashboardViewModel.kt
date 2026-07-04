package com.foodtracker.ui.screens.dashboard

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodtracker.data.database.AppDatabase
import com.foodtracker.data.entities.FoodEntry
import com.foodtracker.data.repository.FoodRepository
import com.foodtracker.utils.DateUtils
import com.foodtracker.utils.NumberUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class DashboardViewModel : ViewModel() {
    
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()
    
    private lateinit var repository: FoodRepository
    
    fun init(context: android.content.Context) {
        val database = AppDatabase.getInstance(context)
        repository = FoodRepository(database.foodEntryDao())
        loadDashboard()
    }
    
    fun toggleMeal(mealType: String) {
        viewModelScope.launch {
            val today = LocalDate.now()
            val currentEntry = repository.getEntry(today)
            
            val newEntry = when (mealType) {
                "breakfast" -> {
                    val newBreakfast = !(currentEntry?.breakfast ?: false)
                    val mealCount = NumberUtils.calculateMealCount(newBreakfast, currentEntry?.lunch ?: false, currentEntry?.dinner ?: false)
                    val dailyExpense = NumberUtils.calculateDailyExpense(newBreakfast, currentEntry?.lunch ?: false, currentEntry?.dinner ?: false)
                    
                    FoodEntry(
                        id = currentEntry?.id ?: 0,
                        date = today,
                        dayOfWeek = DateUtils.getDayOfWeek(today),
                        breakfast = newBreakfast,
                        lunch = currentEntry?.lunch ?: false,
                        dinner = currentEntry?.dinner ?: false,
                        mealCount = mealCount,
                        dailyExpense = dailyExpense
                    )
                }
                "lunch" -> {
                    val newLunch = !(currentEntry?.lunch ?: false)
                    val mealCount = NumberUtils.calculateMealCount(currentEntry?.breakfast ?: false, newLunch, currentEntry?.dinner ?: false)
                    val dailyExpense = NumberUtils.calculateDailyExpense(currentEntry?.breakfast ?: false, newLunch, currentEntry?.dinner ?: false)
                    
                    FoodEntry(
                        id = currentEntry?.id ?: 0,
                        date = today,
                        dayOfWeek = DateUtils.getDayOfWeek(today),
                        breakfast = currentEntry?.breakfast ?: false,
                        lunch = newLunch,
                        dinner = currentEntry?.dinner ?: false,
                        mealCount = mealCount,
                        dailyExpense = dailyExpense
                    )
                }
                "dinner" -> {
                    val newDinner = !(currentEntry?.dinner ?: false)
                    val mealCount = NumberUtils.calculateMealCount(currentEntry?.breakfast ?: false, currentEntry?.lunch ?: false, newDinner)
                    val dailyExpense = NumberUtils.calculateDailyExpense(currentEntry?.breakfast ?: false, currentEntry?.lunch ?: false, newDinner)
                    
                    FoodEntry(
                        id = currentEntry?.id ?: 0,
                        date = today,
                        dayOfWeek = DateUtils.getDayOfWeek(today),
                        breakfast = currentEntry?.breakfast ?: false,
                        lunch = currentEntry?.lunch ?: false,
                        dinner = newDinner,
                        mealCount = mealCount,
                        dailyExpense = dailyExpense
                    )
                }
                else -> return@launch
            }
            
            repository.saveEntry(newEntry)
            loadDashboard()
        }
    }
    
    private fun loadDashboard() {
        viewModelScope.launch {
            try {
                val today = LocalDate.now()
                val currentMonth = YearMonth.now()
                val startDate = currentMonth.atDay(1)
                val endDate = currentMonth.atEndOfMonth()
                
                val todayEntry = repository.getEntry(today)
                val summary = repository.getMonthlySummary(currentMonth)
                val recentEntries = repository.getEntriesBetween(today.minusDays(6), today)
                
                _state.update {
                    it.copy(
                        todayBreakfast = todayEntry?.breakfast ?: false,
                        todayLunch = todayEntry?.lunch ?: false,
                        todayDinner = todayEntry?.dinner ?: false,
                        todayMeals = todayEntry?.mealCount ?: 0,
                        todayExpense = todayEntry?.dailyExpense ?: 0.0,
                        totalMeals = summary.totalMeals,
                        breakfastCount = summary.totalBreakfast,
                        lunchCount = summary.totalLunch,
                        dinnerCount = summary.totalDinner,
                        totalExpense = summary.totalExpense,
                        averageDailyExpense = summary.averageDailyExpense,
                        recentEntries = recentEntries.reversed().take(7)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
    val recentEntries: List<FoodEntry> = emptyList()
)
