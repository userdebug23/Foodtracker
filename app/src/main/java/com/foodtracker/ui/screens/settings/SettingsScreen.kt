import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.foodtracker.utils.BackupManager

// In the Backup section:
item {
    SettingsSection(title = "💾 Backup") {
        SettingsItem(
            icon = "💾",
            title = "Create Backup",
            subtitle = "Save backup to Documents folder",
            onClick = {
                Toast.makeText(context, "Creating backup...", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    val backupManager = BackupManager(context)
                    val file = backupManager.createLocalBackup()
                    if (file != null) {
                        // Show success with file path
                        val message = "✅ Backup saved!\n📁 ${file.absolutePath}"
                        android.util.Log.d("Backup", "Saved to: ${file.absolutePath}")
                        // Use runOnUiThread or withContext to show toast
                        kotlinx.coroutines.withContext(Dispatchers.Main) {
                            Toast.makeText(context, "✅ Backup saved to Documents", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        kotlinx.coroutines.withContext(Dispatchers.Main) {
                            Toast.makeText(context, "❌ Backup failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        )
        
        Divider()
        
        SettingsItem(
            icon = "📂",
            title = "Restore Backup",
            subtitle = "Restore from local backup (Coming Soon)",
            onClick = {
                Toast.makeText(context, "Restore feature coming soon", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
