package com.foodtracker.data.repository

import com.foodtracker.data.dao.FoodEntryDao
import com.foodtracker.data.entities.FoodEntry
import java.time.LocalDate
import java.time.YearMonth

class FoodRepository(private val dao: FoodEntryDao) {
    
    suspend fun saveEntry(entry: FoodEntry): Long {
        return dao.insertEntry(entry)
    }
    
    suspend fun getEntry(date: LocalDate): FoodEntry? {
        return dao.getEntryByDate(date)
    }
    
    suspend fun getEntriesBetween(startDate: LocalDate, endDate: LocalDate): List<FoodEntry> {
        return dao.getEntriesBetween(startDate, endDate)
    }
    
    suspend fun getMonthlySummary(yearMonth: YearMonth): MonthlySummary {
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        
        val entries = dao.getEntriesBetween(startDate, endDate)
        val totalDays = endDate.dayOfMonth
        val presentDays = entries.count { it.mealCount > 0 }
        val absentDays = totalDays - presentDays
        
        val totalBreakfast = dao.getBreakfastCount(startDate, endDate)
        val totalLunch = dao.getLunchCount(startDate, endDate)
        val totalDinner = dao.getDinnerCount(startDate, endDate)
        val totalMeals = dao.getTotalMeals(startDate, endDate)
        val totalExpense = dao.getTotalExpense(startDate, endDate)
        val averageDailyExpense = if (totalDays > 0) totalExpense / totalDays else 0.0
        
        return MonthlySummary(
            totalDays = totalDays,
            presentDays = presentDays,
            absentDays = absentDays,
            totalBreakfast = totalBreakfast,
            totalLunch = totalLunch,
            totalDinner = totalDinner,
            totalMeals = totalMeals,
            totalExpense = totalExpense,
            averageDailyExpense = averageDailyExpense
        )
    }
}

data class MonthlySummary(
    val totalDays: Int,
    val presentDays: Int,
    val absentDays: Int,
    val totalBreakfast: Int,
    val totalLunch: Int,
    val totalDinner: Int,
    val totalMeals: Int,
    val totalExpense: Double,
    val averageDailyExpense: Double
)
