package com.foodtracker.ui.screens.calendar

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodtracker.data.database.AppDatabase
import com.foodtracker.data.entities.FoodEntry
import com.foodtracker.data.repository.FoodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarViewModel(private val context: Context) : ViewModel() {
    
    private val _state = MutableStateFlow(CalendarState())
    val state: StateFlow<CalendarState> = _state.asStateFlow()
    
    private val database = AppDatabase.getInstance(context)
    private val repository = FoodRepository(database.foodEntryDao())
    
    private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    private var currentMonth = YearMonth.now()
    
    init {
        loadMonth(currentMonth)
    }
    
    fun navigateMonth(direction: Int) {
        currentMonth = if (direction == 1) {
            currentMonth.plusMonths(1)
        } else {
            currentMonth.minusMonths(1)
        }
        loadMonth(currentMonth)
    }
    
    fun goToToday() {
        currentMonth = YearMonth.now()
        loadMonth(currentMonth)
    }
    
    private fun loadMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            try {
                val startDate = yearMonth.atDay(1)
                val endDate = yearMonth.atEndOfMonth()
                
                val entries = repository.getEntriesBetween(startDate, endDate)
                val entryMap = entries.associateBy { it.date }
                
                // Build calendar grid
                val firstDayOfMonth = yearMonth.atDay(1)
                val dayOfWeek = firstDayOfMonth.dayOfWeek.value // 1=Monday, 7=Sunday
                val startOffset = if (dayOfWeek == 7) 0 else dayOfWeek - 1
                
                val calendarDays = mutableListOf<CalendarDay>()
                
                // Previous month days
                val previousMonth = yearMonth.minusMonths(1)
                val daysInPreviousMonth = previousMonth.lengthOfMonth()
                val startDay = daysInPreviousMonth - startOffset + 1
                
                for (i in 0 until startOffset) {
                    val day = startDay + i
                    val date = previousMonth.atDay(day)
                    calendarDays.add(
                        CalendarDay(
                            date = date,
                            isCurrentMonth = false,
                            breakfast = false,
                            lunch = false,
                            dinner = false,
                            mealCount = 0
                        )
                    )
                }
                
                // Current month days
                val daysInMonth = yearMonth.lengthOfMonth()
                for (day in 1..daysInMonth) {
                    val date = yearMonth.atDay(day)
                    val entry = entryMap[date]
                    calendarDays.add(
                        CalendarDay(
                            date = date,
                            isCurrentMonth = true,
                            breakfast = entry?.breakfast ?: false,
                            lunch = entry?.lunch ?: false,
                            dinner = entry?.dinner ?: false,
                            mealCount = entry?.mealCount ?: 0,
                            dailyExpense = entry?.dailyExpense ?: 0.0
                        )
                    )
                }
                
                // Next month days to complete the grid
                val remainingDays = (7 - (calendarDays.size % 7)) % 7
                val nextMonth = yearMonth.plusMonths(1)
                for (day in 1..remainingDays) {
                    val date = nextMonth.atDay(day)
                    calendarDays.add(
                        CalendarDay(
                            date = date,
                            isCurrentMonth = false,
                            breakfast = false,
                            lunch = false,
                            dinner = false,
                            mealCount = 0
                        )
                    )
                }
                
                // Calculate stats
                val currentMonthEntries = entries.filter { 
                    it.date.month == yearMonth.month && it.date.year == yearMonth.year 
                }
                val totalMeals = currentMonthEntries.sumOf { it.mealCount }
                val totalExpense = currentMonthEntries.sumOf { it.dailyExpense }
                val presentDays = currentMonthEntries.count { it.mealCount > 0 }
                val absentDays = daysInMonth - presentDays
                
                val weeks = calendarDays.chunked(7)
                
                _state.update {
                    it.copy(
                        calendarDays = calendarDays,
                        weeks = weeks,
                        monthTitle = yearMonth.format(monthFormatter),
                        yearMonth = yearMonth,
                        totalMeals = totalMeals,
                        totalExpense = totalExpense,
                        presentDays = presentDays,
                        absentDays = absentDays,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
    
    companion object {
        fun provideFactory(context: Context): androidx.lifecycle.ViewModelProvider.Factory {
            return object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CalendarViewModel(context) as T
                }
            }
        }
    }
}

data class CalendarState(
    val calendarDays: List<CalendarDay> = emptyList(),
    val weeks: List<List<CalendarDay>> = emptyList(),
    val monthTitle: String = "",
    val yearMonth: YearMonth = YearMonth.now(),
    val totalMeals: Int = 0,
    val totalExpense: Double = 0.0,
    val presentDays: Int = 0,
    val absentDays: Int = 0,
    val isLoading: Boolean = true
)

data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val breakfast: Boolean,
    val lunch: Boolean,
    val dinner: Boolean,
    val mealCount: Int,
    val dailyExpense: Double = 0.0
)
