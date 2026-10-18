package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.*

@Database(
    entities = [
        SurahEntity::class,
        AyahEntity::class,
        AdhkarItemEntity::class,
        AdhkarProgressEntity::class,
        TasbeehLogEntity::class,
        TadabburNoteEntity::class,
        DailyWirdGoalEntity::class,
        DailyWirdLogEntity::class,
        CalendarNoteEntity::class,
        BookmarkEntity::class,
        FavoriteEntity::class,
        PreferenceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahDao
    abstract fun ayahDao(): AyahDao
    abstract fun adhkarDao(): AdhkarDao
    abstract fun userDataDao(): UserDataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rafiq_muslim_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
