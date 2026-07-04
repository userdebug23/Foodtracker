package com.foodtracker.utils

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat
import java.util.Locale

private val Context.dataStore by preferencesDataStore(name = "food_settings")
private val DAILY_RATE_KEY = doublePreferencesKey("daily_rate")
private val MEAL_RATE_KEY = doublePreferencesKey("meal_rate")

object NumberUtils {
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    
    fun formatCurrency(amount: Double): String {
        return currencyFormat.format(amount)
    }
    
    // Get meal rate (per meal cost)
    suspend fun getMealRate(context: Context): Double {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[MEAL_RATE_KEY] ?: 50.0
        } catch (e: Exception) {
            50.0
        }
    }
    
    fun getMealRateSync(context: Context): Double {
        return try {
            runBlocking {
                val preferences = context.dataStore.data.first()
                preferences[MEAL_RATE_KEY] ?: 50.0
            }
        } catch (e: Exception) {
            50.0
        }
    }
    
    // Get daily rate (full day cost = 3 meals)
    suspend fun getDailyRate(context: Context): Double {
        return getMealRate(context) * 3
    }
    
    fun getDailyRateSync(context: Context): Double {
        return getMealRateSync(context) * 3
    }
    
    // Calculate daily expense
    suspend fun calculateDailyExpense(context: Context, breakfast: Boolean, lunch: Boolean, dinner: Boolean): Double {
        val mealRate = getMealRate(context)
        var count = 0
        if (breakfast) count++
        if (lunch) count++
        if (dinner) count++
        return count * mealRate
    }
    
    fun calculateDailyExpenseSync(context: Context, breakfast: Boolean, lunch: Boolean, dinner: Boolean): Double {
        val mealRate = getMealRateSync(context)
        var count = 0
        if (breakfast) count++
        if (lunch) count++
        if (dinner) count++
        return count * mealRate
    }
    
    fun calculateMealCount(breakfast: Boolean, lunch: Boolean, dinner: Boolean): Int {
        var count = 0
        if (breakfast) count++
        if (lunch) count++
        if (dinner) count++
        return count
    }
}
