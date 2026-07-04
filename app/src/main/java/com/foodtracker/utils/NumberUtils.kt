package com.foodtracker.utils

import android.content.Context
import java.text.NumberFormat
import java.util.Locale

object NumberUtils {
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    
    fun formatCurrency(amount: Double): String {
        return currencyFormat.format(amount)
    }
    
    // Get meal rate from SharedPreferences
    fun getMealRate(context: Context): Double {
        val prefs = context.getSharedPreferences("food_tracker_settings", Context.MODE_PRIVATE)
        return prefs.getFloat("meal_rate", 50f).toDouble()
    }
    
    // Get daily rate
    fun getDailyRate(context: Context): Double {
        return getMealRate(context) * 3
    }
    
    // Calculate daily expense
    fun calculateDailyExpense(context: Context, breakfast: Boolean, lunch: Boolean, dinner: Boolean): Double {
        val mealRate = getMealRate(context)
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
