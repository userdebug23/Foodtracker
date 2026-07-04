package com.foodtracker.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "payment_date")
    val paymentDate: LocalDate,
    
    @ColumnInfo(name = "amount")
    val amount: Double,
    
    @ColumnInfo(name = "payment_method")
    val paymentMethod: String = "Cash",
    
    @ColumnInfo(name = "remarks")
    val remarks: String = "",
    
    @ColumnInfo(name = "is_scheduled")
    val isScheduled: Boolean = false,
    
    @ColumnInfo(name = "status")
    val status: String = "Completed",
    
    @ColumnInfo(name = "created_at")
    val createdAt: String = LocalDate.now().toString()
)
