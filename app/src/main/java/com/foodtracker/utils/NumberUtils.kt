package com.foodtracker.utils

import java.text.NumberFormat
import java.util.Locale

object NumberUtils {
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    
    fun formatCurrency(amount: Double): String {
        return currencyFormat.format(amount)
    }
    
    fun calculateDailyExpense(breakfast: Boolean, lunch: Boolean, dinner: Boolean, mealRate: Double): Double {
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
