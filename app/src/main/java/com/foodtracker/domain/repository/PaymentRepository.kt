package com.foodtracker.domain.repository

import com.foodtracker.data.local.entities.PaymentEntity
import java.time.LocalDate

interface PaymentRepository {
    suspend fun addPayment(payment: PaymentEntity): Long
    suspend fun getAllPayments(): List<PaymentEntity>
    suspend fun getPaymentsBetween(startDate: LocalDate, endDate: LocalDate): List<PaymentEntity>
    suspend fun getTotalPaymentsBetween(startDate: LocalDate, endDate: LocalDate): Double
    suspend fun getTotalPayments(): Double
}
