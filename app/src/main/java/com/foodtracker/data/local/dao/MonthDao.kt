package com.foodtracker.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.foodtracker.data.local.entities.MonthEntity
import java.time.YearMonth

@Dao
interface MonthDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonth(month: MonthEntity): Long
    
    @Update
    suspend fun updateMonth(month: MonthEntity)
    
    @Query("SELECT * FROM months WHERE year_month = :yearMonth")
    suspend fun getMonthByYearMonth(yearMonth: YearMonth): MonthEntity?
    
    @Query("SELECT * FROM months ORDER BY year_month DESC")
    suspend fun getAllMonths(): List<MonthEntity>
    
    @Query("DELETE FROM months WHERE id = :monthId")
    suspend fun deleteMonth(monthId: Long)
}
