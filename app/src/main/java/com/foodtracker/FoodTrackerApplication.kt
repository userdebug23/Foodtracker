package com.foodtracker

import android.app.Application

// REMOVE @HiltAndroidApp for now to avoid Hilt issues
class FoodTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
