package com.foodtracker.ui.screens.dashboard

import android.content.Context
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

class DashboardViewModel(private val context: Context) : ViewModel() {
    
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()
    
    private val database = AppDatabase.getInstance(context)
    private val repository = FoodRepository(database.foodEntryDao())
    
    init {
        loadDashboard()
    }
    
    fun toggleMeal(mealType: String) {
        viewModelScope.launch {
            try {
                val today = LocalDate.now()
                val currentEntry = repository.getEntry(today)
                val mealRate = NumberUtils.getMealRate(context)
                
                val newEntry = when (mealType) {
                    "breakfast" -> {
                        val newBreakfast = !(currentEntry?.breakfast ?: false)
                        val mealCount = NumberUtils.calculateMealCount(
                            newBreakfast,
                            currentEntry?.lunch ?: false,
                            currentEntry?.dinner ?: false
                        )
                        val dailyExpense = mealCount * mealRate
                        
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
                        val mealCount = NumberUtils.calculateMealCount(
                            currentEntry?.breakfast ?: false,
                            newLunch,
                            currentEntry?.dinner ?: false
                        )
                        val dailyExpense = mealCount * mealRate
                        
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
                        val mealCount = NumberUtils.calculateMealCount(
                            currentEntry?.breakfast ?: false,
                            currentEntry?.lunch ?: false,
                            newDinner
                        )
                        val dailyExpense = mealCount * mealRate
                        
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun loadDashboard() {
        viewModelScope.launch {
            try {
                val today = LocalDate.now()
                val currentMonth = YearMonth.now()
                
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
    
    fun refresh() {
        loadDashboard()
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
    val recentEntries: List<FoodEntry> = emptyList()
)
