package com.foodtracker.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "food_entries")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "date")
    val date: LocalDate,
    
    @ColumnInfo(name = "day_of_week")
    val dayOfWeek: String,
    
    @ColumnInfo(name = "breakfast")
    val breakfast: Boolean = false,
    
    @ColumnInfo(name = "lunch")
    val lunch: Boolean = false,
    
    @ColumnInfo(name = "dinner")
    val dinner: Boolean = false,
    
    @ColumnInfo(name = "meal_count")
    val mealCount: Int = 0,
    
    @ColumnInfo(name = "daily_expense")
    val dailyExpense: Double = 0.0
)
