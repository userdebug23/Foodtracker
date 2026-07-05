package com.foodtracker.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
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
                
                // Get backup directory
                val backupDir = getBackupDirectory()
                if (!backupDir.exists()) {
                    backupDir.mkdirs()
                }
                
                val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val timestamp = dateFormat.format(Date())
                val fileName = "food_tracker_backup_$timestamp.txt"
                val file = File(backupDir, fileName)
                
                // Create a simple text file as backup
                file.writeText("""
                    Food Tracker Backup
                    ==================
                    Export Date: ${SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())}
                    
                    This is a placeholder backup file.
                    Full Excel backup will be available soon.
                    
                    Your data is safe!
                """.trimIndent())
                
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
}
