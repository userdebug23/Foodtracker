package com.foodtracker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.foodtracker.data.entities.PaymentEntity
import java.time.LocalDate

@Dao
interface PaymentDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long
    
    @Update
    suspend fun updatePayment(payment: PaymentEntity)
    
    @Query("SELECT * FROM payments ORDER BY payment_date DESC")
    suspend fun getAllPayments(): List<PaymentEntity>
    
    @Query("SELECT * FROM payments WHERE status = :status ORDER BY payment_date DESC")
    suspend fun getPaymentsByStatus(status: String): List<PaymentEntity>
    
    @Query("SELECT * FROM payments WHERE payment_date BETWEEN :startDate AND :endDate ORDER BY payment_date DESC")
    suspend fun getPaymentsBetween(startDate: LocalDate, endDate: LocalDate): List<PaymentEntity>
    
    @Query("SELECT * FROM payments WHERE payment_date >= :date AND status = 'Pending' ORDER BY payment_date ASC")
    suspend fun getPendingPayments(date: LocalDate): List<PaymentEntity>
    
    @Query("SELECT SUM(amount) FROM payments WHERE payment_date BETWEEN :startDate AND :endDate AND status = 'Completed'")
    suspend fun getTotalCompletedPaymentsBetween(startDate: LocalDate, endDate: LocalDate): Double?
    
    @Query("SELECT SUM(amount) FROM payments WHERE status = 'Completed'")
    suspend fun getTotalCompletedPayments(): Double?
    
    @Query("SELECT SUM(amount) FROM payments WHERE status = 'Pending' AND payment_date <= :date")
    suspend fun getOverduePayments(date: LocalDate): Double?
    
    @Query("DELETE FROM payments WHERE id = :paymentId")
    suspend fun deletePayment(paymentId: Long)
}
