package com.foodtracker.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    
    companion object {
        private val MEAL_RATE = doublePreferencesKey("meal_rate")
        private val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    }
    
    // Meal Rate
    suspend fun saveMealRate(rate: Double) {
        context.dataStore.edit { preferences ->
            preferences[MEAL_RATE] = rate
        }
    }
    
    val getMealRate: Flow<Double> = context.dataStore.data
        .map { preferences ->
            preferences[MEAL_RATE] ?: 50.0 // Default ₹50 per meal
        }
    
    // Dark Theme
    suspend fun saveTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_THEME] = isDark
        }
    }
    
    val getTheme: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_DARK_THEME] ?: false // Default Light theme
        }
}
