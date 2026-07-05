package com.foodtracker.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import com.foodtracker.data.database.AppDatabase
import com.foodtracker.data.entities.FoodEntry
import com.foodtracker.data.entities.PaymentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    
    suspend fun createLocalBackup(): File? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Starting backup...")
                
                val database = AppDatabase.getInstance(context)
                
                // Get all data
                val allEntries = database.foodEntryDao().getAllEntries()
                val allPayments = database.paymentDao().getAllPayments()
                
                Log.d(TAG, "Entries: ${allEntries.size}, Payments: ${allPayments.size}")
                
                val workbook = XSSFWorkbook()
                val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val timestamp = dateFormat.format(Date())
                
                // Create sheets
                createFoodSheet(workbook, allEntries)
                createPaymentSheet(workbook, allPayments)
                createSummarySheet(workbook, allEntries, allPayments)
                
                // Get backup directory
                val backupDir = getBackupDirectory()
                if (!backupDir.exists()) {
                    backupDir.mkdirs()
                }
                
                val fileName = "food_tracker_backup_$timestamp.xlsx"
                val file = File(backupDir, fileName)
                
                // Write file
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
    }
    
    private fun getBackupDirectory(): File {
        // Try Documents folder first
        val documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (documentsDir != null && documentsDir.exists()) {
            return File(documentsDir, BACKUP_FOLDER)
        }
        
        // Fallback to app's private storage
        val filesDir = context.filesDir
        if (filesDir != null && filesDir.exists()) {
            return File(filesDir, BACKUP_FOLDER)
        }
        
        // Final fallback - cache directory
        return File(context.cacheDir, BACKUP_FOLDER)
    }
    
    private fun createFoodSheet(workbook: XSSFWorkbook, entries: List<FoodEntry>) {
        val sheet = workbook.createSheet("Food Entries")
        
        val headers = arrayOf("Date", "Day", "Breakfast", "Lunch", "Dinner", "Meals", "Expense")
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, header ->
            headerRow.createCell(index).setCellValue(header)
        }
        
        entries.forEachIndexed { index, entry ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(entry.date.toString())
            row.createCell(1).setCellValue(entry.dayOfWeek)
            row.createCell(2).setCellValue(if (entry.breakfast) "Yes" else "No")
            row.createCell(3).setCellValue(if (entry.lunch) "Yes" else "No")
            row.createCell(4).setCellValue(if (entry.dinner) "Yes" else "No")
            row.createCell(5).setCellValue(entry.mealCount.toDouble())
            row.createCell(6).setCellValue(entry.dailyExpense)
        }
        
        for (i in 0..6) {
            sheet.autoSizeColumn(i)
        }
    }
    
    private fun createPaymentSheet(workbook: XSSFWorkbook, payments: List<PaymentEntity>) {
        val sheet = workbook.createSheet("Payments")
        
        val headers = arrayOf("Date", "Amount", "Method", "Status", "Remarks")
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, header ->
            headerRow.createCell(index).setCellValue(header)
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
    
    private fun createSummarySheet(
        workbook: XSSFWorkbook,
        entries: List<FoodEntry>,
        payments: List<PaymentEntity>
    ) {
        val sheet = workbook.createSheet("Summary")
        
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
            row.createCell(0).setCellValue(label)
            row.createCell(1).setCellValue(value)
        }
        
        sheet.autoSizeColumn(0)
        sheet.autoSizeColumn(1)
    }
}
