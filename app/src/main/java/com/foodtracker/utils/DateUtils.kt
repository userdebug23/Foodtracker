package com.foodtracker.utils

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
    private val dayFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
    private val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    
    fun formatDate(date: LocalDate): String {
        return date.format(dateFormatter)
    }
    
    fun getDayOfWeek(date: LocalDate): String {
        return date.format(dayFormatter)
    }
    
    fun formatMonthYear(yearMonth: YearMonth): String {
        return yearMonth.format(monthYearFormatter)
    }
    
    fun getDaysInMonth(yearMonth: YearMonth): Int {
        return yearMonth.lengthOfMonth()
    }
    
    fun getFirstDayOfMonth(yearMonth: YearMonth): LocalDate {
        return yearMonth.atDay(1)
    }
    
    fun getLastDayOfMonth(yearMonth: YearMonth): LocalDate {
        return yearMonth.atEndOfMonth()
    }
    
    fun getCurrentMonth(): YearMonth {
        return YearMonth.now()
    }
    
    fun getPreviousMonth(yearMonth: YearMonth): YearMonth {
        return yearMonth.minusMonths(1)
    }
    
    fun getNextMonth(yearMonth: YearMonth): YearMonth {
        return yearMonth.plusMonths(1)
    }
}
