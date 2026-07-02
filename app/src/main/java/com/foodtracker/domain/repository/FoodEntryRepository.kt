package com.foodtracker.domain.repository

import com.foodtracker.data.local.entities.FoodEntryEntity
import java.time.LocalDate
import java.time.YearMonth

interface FoodEntryRepository {
    suspend fun saveEntry(entry: FoodEntryEntity): Long
    suspend fun getEntry(date: LocalDate): FoodEntryEntity?
    suspend fun getEntriesBetween(startDate: LocalDate, endDate: LocalDate): List<FoodEntryEntity>
    suspend fun getEntriesByMonth(yearMonth: YearMonth): List<FoodEntryEntity>
    suspend fun getMonthlySummary(yearMonth: YearMonth): MonthlySummary
    suspend fun getMealCounts(startDate: LocalDate, endDate: LocalDate): MealCounts
    suspend fun getTotalExpense(startDate: LocalDate, endDate: LocalDate): Double
    suspend fun getAverageDailyExpense(startDate: LocalDate, endDate: LocalDate): Double
}

data class MonthlySummary(
    val yearMonth: YearMonth,
    val totalDays: Int,
    val presentDays: Int,
    val absentDays: Int,
    val totalBreakfast: Int,
    val totalLunch: Int,
    val totalDinner: Int,
    val totalMeals: Int,
    val totalExpense: Double,
    val averageDailyExpense: Double,
    val monthlyCharge: Double,
    val paidAmount: Double,
    val balance: Double,
    val daysCovered: Int,
    val remainingDays: Int,
    val remainingMeals: Int
)

data class MealCounts(
    val breakfast: Int,
    val lunch: Int,
    val dinner: Int,
    val totalMeals: Int
)
