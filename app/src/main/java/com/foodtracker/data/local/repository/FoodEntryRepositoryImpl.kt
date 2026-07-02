package com.foodtracker.data.local.repository

import com.foodtracker.data.local.database.dao.FoodEntryDao
import com.foodtracker.data.local.database.dao.MonthDao
import com.foodtracker.data.local.database.dao.PaymentDao
import com.foodtracker.data.local.entities.FoodEntryEntity
import com.foodtracker.domain.repository.FoodEntryRepository
import com.foodtracker.domain.repository.MealCounts
import com.foodtracker.domain.repository.MonthlySummary
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class FoodEntryRepositoryImpl @Inject constructor(
    private val foodEntryDao: FoodEntryDao,
    private val paymentDao: PaymentDao,
    private val monthDao: MonthDao
) : FoodEntryRepository {
    
    override suspend fun saveEntry(entry: FoodEntryEntity): Long {
        return foodEntryDao.insertEntry(entry)
    }
    
    override suspend fun getEntry(date: LocalDate): FoodEntryEntity? {
        return foodEntryDao.getEntryByDate(date)
    }
    
    override suspend fun getEntriesBetween(startDate: LocalDate, endDate: LocalDate): List<FoodEntryEntity> {
        return foodEntryDao.getEntriesBetween(startDate, endDate)
    }
    
    override suspend fun getEntriesByMonth(yearMonth: YearMonth): List<FoodEntryEntity> {
        return foodEntryDao.getEntriesByMonth(yearMonth.toString())
    }
    
    override suspend fun getMonthlySummary(yearMonth: YearMonth): MonthlySummary {
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()
        
        val entries = foodEntryDao.getEntriesBetween(startDate, endDate)
        val totalDays = endDate.dayOfMonth
        val presentDays = entries.count { it.mealCount > 0 }
        val absentDays = totalDays - presentDays
        
        val totalBreakfast = foodEntryDao.getBreakfastCount(startDate, endDate)
        val totalLunch = foodEntryDao.getLunchCount(startDate, endDate)
        val totalDinner = foodEntryDao.getDinnerCount(startDate, endDate)
        val totalMeals = foodEntryDao.getTotalMeals(startDate, endDate)
        val totalExpense = foodEntryDao.getTotalExpense(startDate, endDate) ?: 0.0
        val averageDailyExpense = foodEntryDao.getAverageDailyExpense(startDate, endDate) ?: 0.0
        
        val monthEntity = monthDao.getMonthByYearMonth(yearMonth)
        val monthlyCharge = monthEntity?.monthlyCharge ?: 0.0
        
        val paidAmount = paymentDao.getTotalPaymentsBetween(startDate, endDate) ?: 0.0
        val balance = totalExpense - paidAmount
        
        val daysCovered = if (monthlyCharge > 0) {
            (paidAmount / monthlyCharge * totalDays).toInt()
        } else 0
        val remainingDays = totalDays - daysCovered
        val remainingMeals = remainingDays * 3
        
        return MonthlySummary(
            yearMonth = yearMonth,
            totalDays = totalDays,
            presentDays = presentDays,
            absentDays = absentDays,
            totalBreakfast = totalBreakfast,
            totalLunch = totalLunch,
            totalDinner = totalDinner,
            totalMeals = totalMeals,
            totalExpense = totalExpense,
            averageDailyExpense = averageDailyExpense,
            monthlyCharge = monthlyCharge,
            paidAmount = paidAmount,
            balance = balance,
            daysCovered = daysCovered,
            remainingDays = remainingDays,
            remainingMeals = remainingMeals
        )
    }
    
    override suspend fun getMealCounts(startDate: LocalDate, endDate: LocalDate): MealCounts {
        val breakfast = foodEntryDao.getBreakfastCount(startDate, endDate)
        val lunch = foodEntryDao.getLunchCount(startDate, endDate)
        val dinner = foodEntryDao.getDinnerCount(startDate, endDate)
        val totalMeals = foodEntryDao.getTotalMeals(startDate, endDate)
        
        return MealCounts(
            breakfast = breakfast,
            lunch = lunch,
            dinner = dinner,
            totalMeals = totalMeals
        )
    }
    
    override suspend fun getTotalExpense(startDate: LocalDate, endDate: LocalDate): Double {
        return foodEntryDao.getTotalExpense(startDate, endDate) ?: 0.0
    }
    
    override suspend fun getAverageDailyExpense(startDate: LocalDate, endDate: LocalDate): Double {
        return foodEntryDao.getAverageDailyExpense(startDate, endDate) ?: 0.0
    }
}
