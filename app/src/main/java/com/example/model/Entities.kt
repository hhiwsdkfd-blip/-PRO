package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val number: Int,
    val name: String,
    val englishName: String,
    val ayahCount: Int,
    val revelationType: String,
    val juzStart: Int,
    val hasBasmala: Boolean = true
)

@Entity(tableName = "ayahs")
data class AyahEntity(
    @PrimaryKey val id: String, // e.g. "1_1"
    val surahNumber: Int,
    val numberInSurah: Int,
    val text: String,
    val juz: Int
)

@Entity(tableName = "adhkar")
data class AdhkarItemEntity(
    @PrimaryKey val id: String,
    val category: String, // morning, evening, after_prayer, sleep, wake, misc
    val title: String,
    val text: String,
    val repeat: Int,
    val reference: String,
    val source: String,
    val orderIndex: Int
)

@Entity(tableName = "adhkar_progress")
data class AdhkarProgressEntity(
    @PrimaryKey val id: String, // date_dhikrId
    val date: String,
    val dhikrId: String,
    val currentCount: Int
)

@Entity(tableName = "tasbeeh_logs")
data class TasbeehLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val dhikr: String,
    val count: Int,
    val target: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tadabbur_notes")
data class TadabburNoteEntity(
    @PrimaryKey val id: String,
    val text: String,
    val surahNumber: Int? = null,
    val ayahNumber: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "wird_goals")
data class DailyWirdGoalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val target: Int,
    val kind: String, // manual, adhkar, tasbeeh
    val orderIndex: Int = 0
)

@Entity(tableName = "wird_logs")
data class DailyWirdLogEntity(
    @PrimaryKey val id: String, // date_goalId
    val date: String,
    val goalId: String,
    val progress: Int
)

@Entity(tableName = "calendar_notes")
data class CalendarNoteEntity(
    @PrimaryKey val id: String,
    val date: String, // YYYY-MM-DD
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: String, // surah_ayah
    val surahNumber: Int,
    val ayahNumber: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String, // surah:X or dhikr:Y
    val type: String, // "surah" or "dhikr"
    val itemId: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "preferences")
data class PreferenceEntity(
    @PrimaryKey val key: String,
    val value: String
)
