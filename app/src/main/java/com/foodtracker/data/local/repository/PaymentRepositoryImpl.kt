package com.foodtracker.data.local.repository

import com.foodtracker.data.local.database.dao.FoodEntryDao
import com.foodtracker.data.local.database.dao.PaymentDao
import com.foodtracker.data.local.entities.PaymentEntity
import com.foodtracker.domain.repository.PaymentRepository
import java.time.LocalDate
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val paymentDao: PaymentDao,
    private val foodEntryDao: FoodEntryDao
) : PaymentRepository {
    
    override suspend fun addPayment(payment: PaymentEntity): Long {
        return paymentDao.insertPayment(payment)
    }
    
    override suspend fun getAllPayments(): List<PaymentEntity> {
        return paymentDao.getAllPayments()
    }
    
    override suspend fun getPaymentsBetween(startDate: LocalDate, endDate: LocalDate): List<PaymentEntity> {
        return paymentDao.getPaymentsBetween(startDate, endDate)
    }
    
    override suspend fun getTotalPaymentsBetween(startDate: LocalDate, endDate: LocalDate): Double {
        return paymentDao.getTotalPaymentsBetween(startDate, endDate) ?: 0.0
    }
    
    override suspend fun getTotalPayments(): Double {
        return paymentDao.getTotalPayments() ?: 0.0
    }
}
