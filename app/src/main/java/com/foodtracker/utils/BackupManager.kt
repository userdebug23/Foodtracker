package com.foodtracker.utils

import android.content.Context
import android.util.Log
import com.foodtracker.data.database.AppDatabase
import com.foodtracker.data.entities.FoodEntry
import com.foodtracker.data.entities.PaymentEntity
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        return withContext(Dispatchers.IO) {
            try {
                val database = AppDatabase.getInstance(context)
                val foodEntries = database.foodEntryDao().getAllEntries()
                val payments = database.paymentDao().getAllPayments()
                
                val workbook = XSSFWorkbook()
                val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val timestamp = dateFormat.format(Date())
                
                // Food Entries Sheet
                createFoodSheet(workbook, foodEntries)
                
                // Payments Sheet
                createPaymentSheet(workbook, payments)
                
                // Summary Sheet
                createSummarySheet(workbook, foodEntries, payments)
                
                // Save file
                val backupDir = File(context.getExternalFilesDir(null), BACKUP_FOLDER)
                if (!backupDir.exists()) {
                    backupDir.mkdirs()
                }
                
                val fileName = "food_tracker_backup_$timestamp.xlsx"
                val file = File(backupDir, fileName)
                FileOutputStream(file).use { workbook.write(it) }
                workbook.close()
                
                Log.d(TAG, "Backup created: ${file.absolutePath}")
                file
            } catch (e: Exception) {
                Log.e(TAG, "Error creating backup: ${e.message}")
                e.printStackTrace()
                null
            }
        }
    }
    
    // 📤 Restore from Local Backup
    suspend fun restoreFromLocalBackup(file: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val workbook = XSSFWorkbook(file.inputStream())
                val database = AppDatabase.getInstance(context)
                
                // Parse and restore food entries
                val foodSheet = workbook.getSheet("Food Entries")
                if (foodSheet != null) {
                    // Clear existing entries (optional - could merge)
                    // database.foodEntryDao().deleteAll()
                    
                    val entries = parseFoodEntries(foodSheet)
                    entries.forEach { entry ->
                        database.foodEntryDao().insertEntry(entry)
                    }
                }
                
                // Parse and restore payments
                val paymentSheet = workbook.getSheet("Payments")
                if (paymentSheet != null) {
                    val payments = parsePayments(paymentSheet)
                    payments.forEach { payment ->
                        database.paymentDao().insertPayment(payment)
                    }
                }
                
                workbook.close()
                Log.d(TAG, "Backup restored successfully")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error restoring backup: ${e.message}")
                e.printStackTrace()
                false
            }
        }
    }
    
    // 📤 Upload to Google Drive
    suspend fun uploadToGoogleDrive(
        credential: GoogleAccountCredential,
        file: File
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val drive = Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    GsonFactory.getDefaultInstance(),
                    credential
                ).setApplicationName("Food Tracker")
                 .build()
                
                // Check if backup folder exists, create if not
                val folderId = getOrCreateBackupFolder(drive)
                
                // Upload file
                val fileMetadata = com.google.api.services.drive.model.File()
                fileMetadata.name = file.name
                fileMetadata.parents = listOf(folderId)
                fileMetadata.mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                
                val content = java.io.FileInputStream(file)
                val driveFile = drive.files().create(fileMetadata, content)
                    .setFields("id")
                    .execute()
                
                Log.d(TAG, "File uploaded to Google Drive: ${driveFile.id}")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading to Google Drive: ${e.message}")
                e.printStackTrace()
                false
            }
        }
    }
    
    // 📥 Download from Google Drive
    suspend fun listGoogleDriveBackups(credential: GoogleAccountCredential): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val drive = Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    GsonFactory.getDefaultInstance(),
                    credential
                ).setApplicationName("Food Tracker")
                 .build()
                
                val folderId = getOrCreateBackupFolder(drive)
                
                val result = drive.files().list()
                    .setQ("'$folderId' in parents and mimeType='application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'")
                    .setFields("files(name, id, createdTime)")
                    .execute()
                
                result.files.map { it.name }
            } catch (e: Exception) {
                Log.e(TAG, "Error listing backups: ${e.message}")
                emptyList()
            }
        }
    }
    
    // 📥 Download specific backup from Google Drive
    suspend fun downloadFromGoogleDrive(
        credential: GoogleAccountCredential,
        fileName: String
    ): File? {
        return withContext(Dispatchers.IO) {
            try {
                val drive = Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    GsonFactory.getDefaultInstance(),
                    credential
                ).setApplicationName("Food Tracker")
                 .build()
                
                val folderId = getOrCreateBackupFolder(drive)
                
                val result = drive.files().list()
                    .setQ("'$folderId' in parents and name='$fileName'")
                    .setFields("files(id, name)")
                    .execute()
                
                val file = result.files.firstOrNull()
                if (file == null) {
                    Log.e(TAG, "File not found: $fileName")
                    return@withContext null
                }
                
                val outputFile = File(context.cacheDir, file.name)
                val outputStream = FileOutputStream(outputFile)
                drive.files().get(file.id).executeMediaAndDownloadTo(outputStream)
                outputStream.close()
                
                outputFile
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading backup: ${e.message}")
                e.printStackTrace()
                null
            }
        }
    }
    
    // 📋 Helper: Create Food Entries Sheet
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
    
    // 📋 Helper: Create Payments Sheet
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
    
    // 📋 Helper: Create Summary Sheet
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
    
    // 📋 Helper: Parse Food Entries from Excel
    private fun parseFoodEntries(sheet: Sheet): List<FoodEntry> {
        val entries = mutableListOf<FoodEntry>()
        val rows = sheet.iterator()
        if (rows.hasNext()) rows.next() // Skip header
        
        while (rows.hasNext()) {
            val row = rows.next()
            try {
                val date = java.time.LocalDate.parse(row.getCell(0).stringCellValue)
                val dayOfWeek = row.getCell(1).stringCellValue
                val breakfast = row.getCell(2).stringCellValue == "Yes"
                val lunch = row.getCell(3).stringCellValue == "Yes"
                val dinner = row.getCell(4).stringCellValue == "Yes"
                val mealCount = row.getCell(5).numericCellValue.toInt()
                val dailyExpense = row.getCell(6).numericCellValue
                val remarks = row.getCell(7).stringCellValue
                
                entries.add(
                    FoodEntry(
                        date = date,
                        dayOfWeek = dayOfWeek,
                        breakfast = breakfast,
                        lunch = lunch,
                        dinner = dinner,
                        mealCount = mealCount,
                        dailyExpense = dailyExpense,
                        remarks = remarks
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing row: ${e.message}")
            }
        }
        return entries
    }
    
    // 📋 Helper: Parse Payments from Excel
    private fun parsePayments(sheet: Sheet): List<PaymentEntity> {
        val payments = mutableListOf<PaymentEntity>()
        val rows = sheet.iterator()
        if (rows.hasNext()) rows.next() // Skip header
        
        while (rows.hasNext()) {
            val row = rows.next()
            try {
                val date = java.time.LocalDate.parse(row.getCell(0).stringCellValue)
                val amount = row.getCell(1).numericCellValue
                val paymentMethod = row.getCell(2).stringCellValue
                val status = row.getCell(3).stringCellValue
                val remarks = row.getCell(4).stringCellValue
                
                payments.add(
                    PaymentEntity(
                        paymentDate = date,
                        amount = amount,
                        paymentMethod = paymentMethod,
                        status = status,
                        remarks = remarks
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing row: ${e.message}")
            }
        }
        return payments
    }
    
    // 📂 Helper: Get or Create Backup Folder in Google Drive
    private suspend fun getOrCreateBackupFolder(drive: Drive): String {
        return withContext(Dispatchers.IO) {
            try {
                // Check if folder exists
                val query = drive.files().list()
                    .setQ("name='$BACKUP_FOLDER' and mimeType='application/vnd.google-apps.folder'")
                    .setFields("files(id)")
                    .execute()
                
                val existingFolder = query.files.firstOrNull()
                if (existingFolder != null) {
                    return@withContext existingFolder.id
                }
                
                // Create folder
                val folderMetadata = com.google.api.services.drive.model.File()
                folderMetadata.name = BACKUP_FOLDER
                folderMetadata.mimeType = "application/vnd.google-apps.folder"
                
                val folder = drive.files().create(folderMetadata)
                    .setFields("id")
                    .execute()
                
                folder.id
            } catch (e: Exception) {
                Log.e(TAG, "Error creating folder: ${e.message}")
                throw e
            }
        }
    }
}
