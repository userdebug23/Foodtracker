package com.foodtracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.foodtracker.data.local.database.converters.DateConverters
import com.foodtracker.data.local.database.dao.FoodEntryDao
import com.foodtracker.data.local.database.dao.MonthDao
import com.foodtracker.data.local.database.dao.PaymentDao
import com.foodtracker.data.local.entities.FoodEntryEntity
import com.foodtracker.data.local.entities.MonthEntity
import com.foodtracker.data.local.entities.PaymentEntity

@Database(
    entities = [
        FoodEntryEntity::class,
        PaymentEntity::class,
        MonthEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodEntryDao(): FoodEntryDao
    abstract fun paymentDao(): PaymentDao
    abstract fun monthDao(): MonthDao
}
