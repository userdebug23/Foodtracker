package com.foodtracker.data.repository

import com.foodtracker.data.dao.FoodEntryDao
import com.foodtracker.data.entities.FoodEntry
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class FoodRepository(private val dao: FoodEntryDao) {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    
    suspend fun saveEntry(entry: FoodEntry): Long {
        return dao.insertEntry(entry)
    }
    
    suspend fun getEntry(date: LocalDate): FoodEntry? {
        return dao.getEntryByDate(date.format(dateFormatter))
    }
    
    ssuspend fun getEntriesBetween(startDate: LocalDate, endDate: LocalDate): List<FoodEntry> {
    return dao.getEntriesBetween(startDate.toString(), endDate.toString())
}
    
    suspend fun getMonthlySummary(yearMonth: YearMonth): MonthlySummary {
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        val startStr = startDate.format(dateFormatter)
        val endStr = endDate.format(dateFormatter)
        
        val entries = dao.getEntriesBetween(startStr, endStr)
        val totalDays = endDate.dayOfMonth
        val presentDays = entries.count { it.mealCount > 0 }
        val absentDays = totalDays - presentDays
        
        val totalBreakfast = dao.getBreakfastCount(startStr, endStr)
        val totalLunch = dao.getLunchCount(startStr, endStr)
        val totalDinner = dao.getDinnerCount(startStr, endStr)
        val totalMeals = dao.getTotalMeals(startStr, endStr)
        val totalExpense = dao.getTotalExpense(startStr, endStr)
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
