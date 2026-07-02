# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Room
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase

# Keep Compose
-keep class androidx.compose.** { *; }

# Keep models
-keep class com.foodtracker.data.local.entities.** { *; }
