package com.foodtracker.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object ThemeManager {
    private const val PREFS_NAME = "food_tracker_settings"
    private const val KEY_DARK_THEME = "dark_theme"
    
    fun isDarkTheme(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DARK_THEME, false)
    }
    
    fun setDarkTheme(context: Context, isDark: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_DARK_THEME, isDark) }
    }
    
    fun toggleTheme(context: Context): Boolean {
        val newValue = !isDarkTheme(context)
        setDarkTheme(context, newValue)
        return newValue
    }
}
