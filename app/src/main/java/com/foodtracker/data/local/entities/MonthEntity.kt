package com.foodtracker.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.YearMonth

@Entity(tableName = "months")
data class MonthEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "year_month")
    val yearMonth: YearMonth,
    
    @ColumnInfo(name = "monthly_charge")
    val monthlyCharge: Double = 0.0,
    
    @ColumnInfo(name = "is_closed")
    val isClosed: Boolean = false
)
