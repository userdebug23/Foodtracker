# Keep all Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Room
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

# Keep DataStore
-keep class androidx.datastore.** { *; }

# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Apache POI
-keep class org.apache.poi.** { *; }
-dontwarn org.apache.poi.**

# Keep iText
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**

# Keep model classes
-keep class com.foodtracker.data.local.entities.** { *; }
-keep class com.foodtracker.domain.model.** { *; }
