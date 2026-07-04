package com.foodtracker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.foodtracker.data.entities.PaymentEntity
import java.time.LocalDate

@Dao
interface PaymentDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long
    
    @Query("SELECT * FROM payments ORDER BY payment_date DESC")
    suspend fun getAllPayments(): List<PaymentEntity>
    
    @Query("SELECT * FROM payments WHERE payment_date BETWEEN :startDate AND :endDate ORDER BY payment_date DESC")
    suspend fun getPaymentsBetween(startDate: LocalDate, endDate: LocalDate): List<PaymentEntity>
    
    @Query("SELECT SUM(amount) FROM payments WHERE payment_date BETWEEN :startDate AND :endDate")
    suspend fun getTotalPaymentsBetween(startDate: LocalDate, endDate: LocalDate): Double
    
    @Query("SELECT SUM(amount) FROM payments")
    suspend fun getTotalPayments(): Double
    
    @Query("DELETE FROM payments WHERE id = :paymentId")
    suspend fun deletePayment(paymentId: Long)
}
