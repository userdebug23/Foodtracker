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
private val DAILY_AMOUNT_KEY = doublePreferencesKey("daily_amount")

object NumberUtils {
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    
    fun formatCurrency(amount: Double): String {
        return currencyFormat.format(amount)
    }
    
    // Get daily amount from DataStore (synchronous version for non-suspend contexts)
    fun getDailyAmountSync(context: Context): Double {
        return try {
            runBlocking {
                val preferences = context.dataStore.data.first()
                preferences[DAILY_AMOUNT_KEY] ?: 160.0
            }
        } catch (e: Exception) {
            160.0
        }
    }
    
    // Get daily amount (suspend version)
    suspend fun getDailyAmount(context: Context): Double {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[DAILY_AMOUNT_KEY] ?: 160.0
        } catch (e: Exception) {
            160.0
        }
    }
    
    // Get per meal rate (suspend version)
    suspend fun getMealRate(context: Context): Double {
        val dailyAmount = getDailyAmount(context)
        return dailyAmount / 3
    }
    
    // Get per meal rate (synchronous version)
    fun getMealRateSync(context: Context): Double {
        val dailyAmount = getDailyAmountSync(context)
        return dailyAmount / 3
    }
    
    // Calculate daily expense based on meals (suspend version)
    suspend fun calculateDailyExpense(context: Context, breakfast: Boolean, lunch: Boolean, dinner: Boolean): Double {
        val mealRate = getMealRate(context)
        var count = 0
        if (breakfast) count++
        if (lunch) count++
        if (dinner) count++
        return count * mealRate
    }
    
    // Calculate daily expense (synchronous version)
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
