package com.foodtracker.data.repository

import com.foodtracker.data.dao.PaymentDao
import com.foodtracker.data.entities.PaymentEntity
import java.time.LocalDate

class PaymentRepository(private val dao: PaymentDao) {
    
    suspend fun addPayment(payment: PaymentEntity): Long {
        return dao.insertPayment(payment)
    }
    
    suspend fun updatePayment(payment: PaymentEntity) {
        dao.updatePayment(payment)
    }
    
    suspend fun getAllPayments(): List<PaymentEntity> {
        return dao.getAllPayments()
    }
    
    suspend fun getPaymentsByStatus(status: String): List<PaymentEntity> {
        return dao.getPaymentsByStatus(status)
    }
    
    suspend fun getPaymentsBetween(startDate: LocalDate, endDate: LocalDate): List<PaymentEntity> {
        return dao.getPaymentsBetween(startDate, endDate)
    }
    
    suspend fun getPendingPayments(date: LocalDate): List<PaymentEntity> {
        return dao.getPendingPayments(date)
    }
    
    suspend fun getTotalCompletedPaymentsBetween(startDate: LocalDate, endDate: LocalDate): Double {
        val result = dao.getTotalCompletedPaymentsBetween(startDate, endDate)
        return result ?: 0.0
    }
    
    suspend fun getTotalCompletedPayments(): Double {
        val result = dao.getTotalCompletedPayments()
        return result ?: 0.0
    }
    
    suspend fun getOverduePayments(date: LocalDate): Double {
        val result = dao.getOverduePayments(date)
        return result ?: 0.0
    }
    
    suspend fun deletePayment(paymentId: Long) {
        dao.deletePayment(paymentId)
    }
    
    suspend fun markPaymentAsCompleted(paymentId: Long) {
        val payments = dao.getAllPayments()
        val payment = payments.find { it.id == paymentId }
        payment?.let {
            val updatedPayment = it.copy(
                status = "Completed",
                isScheduled = false
            )
            dao.updatePayment(updatedPayment)
        }
    }
}
