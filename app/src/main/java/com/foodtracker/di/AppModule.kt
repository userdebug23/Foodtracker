package com.foodtracker.di

import android.content.Context
import androidx.room.Room
import com.foodtracker.data.local.database.AppDatabase
import com.foodtracker.data.local.database.converters.DateConverters
import com.foodtracker.data.local.database.dao.FoodEntryDao
import com.foodtracker.data.local.database.dao.MonthDao
import com.foodtracker.data.local.database.dao.PaymentDao
import com.foodtracker.data.local.repository.FoodEntryRepositoryImpl
import com.foodtracker.data.local.repository.PaymentRepositoryImpl
import com.foodtracker.domain.repository.FoodEntryRepository
import com.foodtracker.domain.repository.PaymentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "food_tracker_database"
        )
        .addTypeConverter(DateConverters())
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    @Singleton
    fun provideFoodEntryDao(database: AppDatabase): FoodEntryDao {
        return database.foodEntryDao()
    }
    
    @Provides
    @Singleton
    fun providePaymentDao(database: AppDatabase): PaymentDao {
        return database.paymentDao()
    }
    
    @Provides
    @Singleton
    fun provideMonthDao(database: AppDatabase): MonthDao {
        return database.monthDao()
    }
    
    @Provides
    @Singleton
    fun provideFoodEntryRepository(
        foodEntryDao: FoodEntryDao,
        paymentDao: PaymentDao,
        monthDao: MonthDao
    ): FoodEntryRepository {
        return FoodEntryRepositoryImpl(foodEntryDao, paymentDao, monthDao)
    }
    
    @Provides
    @Singleton
    fun providePaymentRepository(
        paymentDao: PaymentDao,
        foodEntryDao: FoodEntryDao
    ): PaymentRepository {
        return PaymentRepositoryImpl(paymentDao, foodEntryDao)
    }
}
