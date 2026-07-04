package com.foodtracker.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foodtracker.data.datastore.SettingsDataStore
import com.foodtracker.data.database.AppDatabase
import com.foodtracker.data.entities.FoodEntry
import com.foodtracker.data.entities.PaymentEntity
import com.foodtracker.data.repository.FoodRepository
import com.foodtracker.data.repository.PaymentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class SettingsViewModel(private val context: Context) : ViewModel() {
    
    private val settingsDataStore = SettingsDataStore(context)
    private val database = AppDatabase.getInstance(context)
    private val foodRepository = FoodRepository(database.foodEntryDao())
    private val paymentRepository = PaymentRepository(database.paymentDao())
    
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            settingsDataStore.getMealRate.collect { rate ->
                _state.update { it.copy(mealRate = rate) }
            }
        }
        
        viewModelScope.launch {
            settingsDataStore.getTheme.collect { isDark ->
                _state.update { it.copy(isDarkTheme = isDark) }
            }
        }
    }
    
    fun saveMealRate(rate: Double) {
        viewModelScope.launch {
            settingsDataStore.saveMealRate(rate)
        }
    }
    
    fun toggleTheme() {
        viewModelScope.launch {
            val newTheme = !_state.value.isDarkTheme
            settingsDataStore.saveTheme(newTheme)
            _state.update { it.copy(isDarkTheme = newTheme) }
        }
    }
    
    fun exportData(): File? {
        try {
            val workbook = XSSFWorkbook()
            val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val exportDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            
            // Export Food Entries
            val foodSheet = workbook.createSheet("Food Entries")
            createFoodEntrySheet(foodSheet)
            
            // Export Payments
            val paymentSheet = workbook.createSheet("Payments")
            createPaymentSheet(paymentSheet)
            
            // Export Summary
            val summarySheet = workbook.createSheet("Summary")
            createSummarySheet(summarySheet)
            
            val fileName = "food_tracker_backup_$exportDate.xlsx"
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { workbook.write(it) }
            workbook.close()
            
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    private fun createFoodEntrySheet(sheet: Sheet) {
        val headerStyle = sheet.workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            val font = sheet.workbook.createFont()
            font.bold = true
            setFont(font)
        }
        
        val headers = arrayOf("Date", "Day", "Breakfast", "Lunch", "Dinner", "Meals", "Expense", "Remarks")
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }
        
        // Get all entries (simplified - get current month for demo)
        val currentMonth = YearMonth.now()
        val entries = foodRepository.getEntriesBetween(currentMonth.atDay(1), currentMonth.atEndOfMonth())
        
        entries.forEachIndexed { index, entry ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(entry.date.toString())
            row.createCell(1).setCellValue(entry.dayOfWeek)
            row.createCell(2).setCellValue(if (entry.breakfast) "Yes" else "No")
            row.createCell(3).setCellValue(if (entry.lunch) "Yes" else "No")
            row.createCell(4).setCellValue(if (entry.dinner) "Yes" else "No")
            row.createCell(5).setCellValue(entry.mealCount.toDouble())
            row.createCell(6).setCellValue(entry.dailyExpense)
            row.createCell(7).setCellValue(entry.remarks ?: "")
        }
        
        for (i in 0..7) {
            sheet.autoSizeColumn(i)
        }
    }
    
    private fun createPaymentSheet(sheet: Sheet) {
        val headerStyle = sheet.workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            val font = sheet.workbook.createFont()
            font.bold = true
            setFont(font)
        }
        
        val headers = arrayOf("Date", "Amount", "Method", "Status", "Remarks")
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }
        
        val payments = paymentRepository.getAllPayments()
        
        payments.forEachIndexed { index, payment ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(payment.paymentDate.toString())
            row.createCell(1).setCellValue(payment.amount)
            row.createCell(2).setCellValue(payment.paymentMethod)
            row.createCell(3).setCellValue(payment.status)
            row.createCell(4).setCellValue(payment.remarks)
        }
        
        for (i in 0..4) {
            sheet.autoSizeColumn(i)
        }
    }
    
    private fun createSummarySheet(sheet: Sheet) {
        val headerStyle = sheet.workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.LIGHT_GREEN.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            val font = sheet.workbook.createFont()
            font.bold = true
            setFont(font)
        }
        
        val currentMonth = YearMonth.now()
        val summary = foodRepository.getMonthlySummary(currentMonth)
        val totalPaid = paymentRepository.getTotalCompletedPayments()
        
        val data = listOf(
            "Export Date" to LocalDate.now().toString(),
            "Month" to currentMonth.toString(),
            "Total Meals" to summary.totalMeals.toString(),
            "Breakfast Count" to summary.totalBreakfast.toString(),
            "Lunch Count" to summary.totalLunch.toString(),
            "Dinner Count" to summary.totalDinner.toString(),
            "Total Expense" to summary.totalExpense.toString(),
            "Total Paid" to totalPaid.toString(),
            "Balance" to (summary.totalExpense - totalPaid).toString()
        )
        
        data.forEachIndexed { index, (label, value) ->
            val row = sheet.createRow(index)
            val labelCell = row.createCell(0)
            labelCell.setCellValue(label)
            labelCell.cellStyle = headerStyle
            row.createCell(1).setCellValue(value)
        }
        
        sheet.autoSizeColumn(0)
        sheet.autoSizeColumn(1)
    }
    
    fun importData(file: File): Boolean {
        try {
            val workbook = XSSFWorkbook(file.inputStream())
            // Import logic - simplified for demo
            // In a real app, you would parse the Excel and save to database
            workbook.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
    
    fun resetDatabase(): Boolean {
        return try {
            context.deleteDatabase("food_tracker_database")
            true
        } catch (e: Exception) {
            false
        }
    }
}

data class SettingsState(
    val mealRate: Double = 50.0,
    val isDarkTheme: Boolean = false,
    val isLoading: Boolean = false
)
