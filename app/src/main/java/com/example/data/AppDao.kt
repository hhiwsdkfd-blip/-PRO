package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahDao {
    @Query("SELECT * FROM surahs ORDER BY number ASC")
    fun getAllSurahs(): Flow<List<SurahEntity>>

    @Query("SELECT * FROM surahs WHERE number = :number LIMIT 1")
    fun getSurah(number: Int): Flow<SurahEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurahs(surahs: List<SurahEntity>)

    @Query("SELECT COUNT(*) FROM surahs")
    suspend fun getSurahCount(): Int
}

@Dao
interface AyahDao {
    @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber ORDER BY numberInSurah ASC")
    fun getAyahsForSurah(surahNumber: Int): Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayahs WHERE juz = :juz ORDER BY surahNumber ASC, numberInSurah ASC")
    fun getAyahsByJuz(juz: Int): Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayahs WHERE text LIKE '%' || :query || '%' LIMIT 100")
    suspend fun searchAyahs(query: String): List<AyahEntity>

    @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber AND numberInSurah = :number LIMIT 1")
    suspend fun getAyah(surahNumber: Int, number: Int): AyahEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAyahs(ayahs: List<AyahEntity>)

    @Query("SELECT COUNT(*) FROM ayahs")
    suspend fun getAyahCount(): Int

    @Query("SELECT DISTINCT juz FROM ayahs ORDER BY juz ASC")
    fun getAvailableJuzList(): Flow<List<Int>>
}

@Dao
interface AdhkarDao {
    @Query("SELECT * FROM adhkar WHERE category = :category ORDER BY orderIndex ASC")
    fun getAdhkarByCategory(category: String): Flow<List<AdhkarItemEntity>>

    @Query("SELECT * FROM adhkar ORDER BY orderIndex ASC")
    fun getAllAdhkar(): Flow<List<AdhkarItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdhkar(items: List<AdhkarItemEntity>)

    @Query("SELECT COUNT(*) FROM adhkar")
    suspend fun getAdhkarCount(): Int

    @Query("SELECT * FROM adhkar_progress WHERE date = :date")
    fun getAdhkarProgress(date: String): Flow<List<AdhkarProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: AdhkarProgressEntity)

    @Query("DELETE FROM adhkar_progress WHERE date = :date")
    suspend fun clearProgressForDate(date: String)
}

@Dao
interface UserDataDao {
    // Tasbeeh
    @Query("SELECT * FROM tasbeeh_logs ORDER BY timestamp DESC LIMIT 200")
    fun getTasbeehLogs(): Flow<List<TasbeehLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasbeehLog(log: TasbeehLogEntity)

    // Tadabbur Notes
    @Query("SELECT * FROM tadabbur_notes ORDER BY timestamp DESC")
    fun getTadabburNotes(): Flow<List<TadabburNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTadabburNote(note: TadabburNoteEntity)

    @Query("DELETE FROM tadabbur_notes WHERE id = :id")
    suspend fun deleteTadabburNote(id: String)

    // Daily Wird Goals
    @Query("SELECT * FROM wird_goals ORDER BY orderIndex ASC")
    fun getWirdGoals(): Flow<List<DailyWirdGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWirdGoal(goal: DailyWirdGoalEntity)

    @Query("DELETE FROM wird_goals WHERE id = :id")
    suspend fun deleteWirdGoal(id: String)

    @Query("SELECT * FROM wird_logs WHERE date = :date")
    fun getWirdLogsForDate(date: String): Flow<List<DailyWirdLogEntity>>

    @Query("SELECT * FROM wird_logs WHERE date IN (:dates)")
    fun getWirdLogsForDates(dates: List<String>): Flow<List<DailyWirdLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWirdLog(log: DailyWirdLogEntity)

    // Calendar Notes
    @Query("SELECT * FROM calendar_notes ORDER BY timestamp DESC")
    fun getCalendarNotes(): Flow<List<CalendarNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendarNote(note: CalendarNoteEntity)

    @Query("DELETE FROM calendar_notes WHERE id = :id")
    suspend fun deleteCalendarNote(id: String)

    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmark(id: String)

    // Favorites
    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteFavorite(id: String)

    // Preferences Key-Value
    @Query("SELECT value FROM preferences WHERE `key` = :key LIMIT 1")
    suspend fun getPreference(key: String): String?

    @Query("SELECT value FROM preferences WHERE `key` = :key LIMIT 1")
    fun observePreference(key: String): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setPreference(preference: PreferenceEntity)

    // Reset All Data
    @Query("DELETE FROM tasbeeh_logs")
    suspend fun clearTasbeehLogs()

    @Query("DELETE FROM tadabbur_notes")
    suspend fun clearTadabbur()

    @Query("DELETE FROM wird_goals")
    suspend fun clearWirdGoals()

    @Query("DELETE FROM wird_logs")
    suspend fun clearWirdLogs()

    @Query("DELETE FROM calendar_notes")
    suspend fun clearCalendarNotes()

    @Query("DELETE FROM bookmarks")
    suspend fun clearBookmarks()

    @Query("DELETE FROM favorites")
    suspend fun clearFavorites()

    @Query("DELETE FROM adhkar_progress")
    suspend fun clearAllAdhkarProgress()

    @Query("DELETE FROM preferences")
    suspend fun clearPreferences()
}
