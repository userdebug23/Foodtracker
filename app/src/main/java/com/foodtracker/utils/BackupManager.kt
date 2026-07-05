package com.foodtracker.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import com.foodtracker.data.database.AppDatabase
import com.foodtracker.data.entities.FoodEntry
import com.foodtracker.data.entities.PaymentEntity
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class BackupManager(private val context: Context) {
    
    companion object {
        private const val TAG = "BackupManager"
        private const val BACKUP_FOLDER = "FoodTrackerBackups"
    }
    
    // 📥 Create Local Backup
    suspend fun createLocalBackup(): File? {
        return try {
            val database = AppDatabase.getInstance(context)
            
            // Get all entries and payments
            val allEntries = database.foodEntryDao().getAllEntries()
            val allPayments = database.paymentDao().getAllPayments()
            
            val workbook = XSSFWorkbook()
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val timestamp = dateFormat.format(Date())
            
            // Food Entries Sheet
            createFoodSheet(workbook, allEntries)
            
            // Payments Sheet
            createPaymentSheet(workbook, allPayments)
            
            // Summary Sheet
            createSummarySheet(workbook, allEntries, allPayments)
            
            // Save file to Documents folder
            val backupDir = getBackupDirectory()
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }
            
            val fileName = "food_tracker_backup_$timestamp.xlsx"
            val file = File(backupDir, fileName)
            FileOutputStream(file).use { workbook.write(it) }
            workbook.close()
            
            Log.d(TAG, "✅ Backup saved: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error creating backup: ${e.message}")
            e.printStackTrace()
            null
        }
    }
    
    // 📂 Get backup directory
    private fun getBackupDirectory(): File {
        // Try external documents directory first
        val documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (documentsDir != null) {
            return File(documentsDir, BACKUP_FOLDER)
        }
        
        // Fallback to internal storage
        return File(context.filesDir, BACKUP_FOLDER)
    }
    
    // 📋 Create Food Entries Sheet
    private fun createFoodSheet(workbook: XSSFWorkbook, entries: List<FoodEntry>) {
        val sheet = workbook.createSheet("Food Entries")
        val headerStyle = createHeaderStyle(workbook)
        
        // Headers
        val headers = arrayOf("Date", "Day", "Breakfast", "Lunch", "Dinner", "Meals", "Expense", "Remarks")
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }
        
        // Data rows
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
    
    // 📋 Create Payments Sheet
    private fun createPaymentSheet(workbook: XSSFWorkbook, payments: List<PaymentEntity>) {
        val sheet = workbook.createSheet("Payments")
        val headerStyle = createHeaderStyle(workbook)
        
        val headers = arrayOf("Date", "Amount", "Method", "Status", "Remarks")
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }
        
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
    
    // 📋 Create Summary Sheet
    private fun createSummarySheet(
        workbook: XSSFWorkbook,
        entries: List<FoodEntry>,
        payments: List<PaymentEntity>
    ) {
        val sheet = workbook.createSheet("Summary")
        val headerStyle = createHeaderStyle(workbook)
        
        val totalMeals = entries.sumOf { it.mealCount }
        val totalExpense = entries.sumOf { it.dailyExpense }
        val totalPaid = payments.sumOf { it.amount }
        val balance = totalPaid - totalExpense
        
        val data = listOf(
            "Export Date" to SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date()),
            "Total Entries" to entries.size.toString(),
            "Total Meals" to totalMeals.toString(),
            "Total Expense" to "₹${String.format("%.2f", totalExpense)}",
            "Total Paid" to "₹${String.format("%.2f", totalPaid)}",
            "Balance" to "₹${String.format("%.2f", balance)}",
            "Total Payments" to payments.size.toString()
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
    
    // 📋 Helper: Create Header Style
    private fun createHeaderStyle(workbook: XSSFWorkbook): CellStyle {
        return workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            val font = workbook.createFont()
            font.bold = true
            setFont(font)
        }
    }
}
