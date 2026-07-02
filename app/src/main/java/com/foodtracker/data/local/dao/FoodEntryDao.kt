package com.foodtracker.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.foodtracker.data.local.entities.FoodEntryEntity
import java.time.LocalDate

@Dao
interface FoodEntryDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: FoodEntryEntity): Long
    
    @Update
    suspend fun updateEntry(entry: FoodEntryEntity)
    
    @Query("SELECT * FROM food_entries WHERE date = :date")
    suspend fun getEntryByDate(date: LocalDate): FoodEntryEntity?
    
    @Query("SELECT * FROM food_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getEntriesBetween(startDate: LocalDate, endDate: LocalDate): List<FoodEntryEntity>
    
    @Query("SELECT * FROM food_entries WHERE strftime('%Y-%m', date) = :yearMonth ORDER BY date ASC")
    suspend fun getEntriesByMonth(yearMonth: String): List<FoodEntryEntity>
    
    @Query("SELECT COUNT(*) FROM food_entries WHERE date BETWEEN :startDate AND :endDate AND breakfast = 1")
    suspend fun getBreakfastCount(startDate: LocalDate, endDate: LocalDate): Int
    
    @Query("SELECT COUNT(*) FROM food_entries WHERE date BETWEEN :startDate AND :endDate AND lunch = 1")
    suspend fun getLunchCount(startDate: LocalDate, endDate: LocalDate): Int
    
    @Query("SELECT COUNT(*) FROM food_entries WHERE date BETWEEN :startDate AND :endDate AND dinner = 1")
    suspend fun getDinnerCount(startDate: LocalDate, endDate: LocalDate): Int
    
    @Query("SELECT SUM(meal_count) FROM food_entries WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalMeals(startDate: LocalDate, endDate: LocalDate): Int
    
    @Query("SELECT SUM(daily_expense) FROM food_entries WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalExpense(startDate: LocalDate, endDate: LocalDate): Double
    
    @Query("SELECT AVG(daily_expense) FROM food_entries WHERE date BETWEEN :startDate AND :endDate AND daily_expense > 0")
    suspend fun getAverageDailyExpense(startDate: LocalDate, endDate: LocalDate): Double
}
