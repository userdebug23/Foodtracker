package com.foodtracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.foodtracker.data.entities.FoodEntry
import java.time.LocalDate

@Dao
interface FoodEntryDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: FoodEntry): Long
    
    @Update
    suspend fun updateEntry(entry: FoodEntry)
    
    @Query("SELECT * FROM food_entries WHERE date = :date")
    suspend fun getEntryByDate(date: String): FoodEntry?
    
    @Query("SELECT * FROM food_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getEntriesBetween(startDate: String, endDate: String): List<FoodEntry>
    
    @Query("SELECT * FROM food_entries WHERE substr(date, 1, 7) = :yearMonth ORDER BY date ASC")
    suspend fun getEntriesByMonth(yearMonth: String): List<FoodEntry>
    
    @Query("SELECT * FROM food_entries ORDER BY date DESC")
    suspend fun getAllEntries(): List<FoodEntry>
    
    @Query("SELECT COUNT(*) FROM food_entries WHERE date BETWEEN :startDate AND :endDate AND breakfast = 1")
    suspend fun getBreakfastCount(startDate: String, endDate: String): Int
    
    @Query("SELECT COUNT(*) FROM food_entries WHERE date BETWEEN :startDate AND :endDate AND lunch = 1")
    suspend fun getLunchCount(startDate: String, endDate: String): Int
    
    @Query("SELECT COUNT(*) FROM food_entries WHERE date BETWEEN :startDate AND :endDate AND dinner = 1")
    suspend fun getDinnerCount(startDate: String, endDate: String): Int
    
    @Query("SELECT SUM(meal_count) FROM food_entries WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalMeals(startDate: String, endDate: String): Int
    
    @Query("SELECT SUM(daily_expense) FROM food_entries WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalExpense(startDate: String, endDate: String): Double?
    
    @Query("SELECT AVG(daily_expense) FROM food_entries WHERE date BETWEEN :startDate AND :endDate AND daily_expense > 0")
    suspend fun getAverageDailyExpense(startDate: String, endDate: String): Double?
    
    @Query("DELETE FROM food_entries WHERE date BETWEEN :startDate AND :endDate")
    suspend fun deleteEntriesBetween(startDate: String, endDate: String)
}
