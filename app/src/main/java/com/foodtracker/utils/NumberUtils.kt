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
private val DAILY_RATE_KEY = doublePreferencesKey("daily_rate")

object NumberUtils {
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    
    fun formatCurrency(amount: Double): String {
        return currencyFormat.format(amount)
    }
    
    // Get daily amount (Daily Budget)
    suspend fun getDailyAmount(context: Context): Double {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[DAILY_AMOUNT_KEY] ?: 160.0
        } catch (e: Exception) {
            160.0
        }
    }
    
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
    
    // Get daily rate (cost per full day)
    suspend fun getDailyRate(context: Context): Double {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[DAILY_RATE_KEY] ?: 160.0
        } catch (e: Exception) {
            160.0
        }
    }
    
    fun getDailyRateSync(context: Context): Double {
        return try {
            runBlocking {
                val preferences = context.dataStore.data.first()
                preferences[DAILY_RATE_KEY] ?: 160.0
            }
        } catch (e: Exception) {
            160.0
        }
    }
    
    // Get per meal rate
    suspend fun getMealRate(context: Context): Double {
        val dailyRate = getDailyRate(context)
        return dailyRate / 3
    }
    
    fun getMealRateSync(context: Context): Double {
        val dailyRate = getDailyRateSync(context)
        return dailyRate / 3
    }
    
    // Calculate daily expense based on meals
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
    
    // Calculate remaining days based on balance and daily rate
    fun calculateRemainingDays(balance: Double, dailyRate: Double): Int {
        return if (dailyRate > 0) (balance / dailyRate).toInt() else 0
    }
}
