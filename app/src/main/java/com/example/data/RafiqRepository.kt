package com.example.data

import android.content.Context
import com.example.model.*
import com.example.utils.ArabicUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class RafiqRepository(private val database: AppDatabase) {

    private val surahDao = database.surahDao()
    private val ayahDao = database.ayahDao()
    private val adhkarDao = database.adhkarDao()
    private val userDataDao = database.userDataDao()

    suspend fun initializeDefaultDataIfNeeded() = withContext(Dispatchers.IO) {
        if (surahDao.getSurahCount() == 0) {
            surahDao.insertSurahs(DefaultIslamicData.getInitialSurahs())
        }
        if (ayahDao.getAyahCount() == 0) {
            ayahDao.insertAyahs(DefaultIslamicData.getInitialAyahs())
        }
        if (adhkarDao.getAdhkarCount() == 0) {
            adhkarDao.insertAdhkar(DefaultIslamicData.INITIAL_ADHKAR)
        }
        val existingGoals = userDataDao.getWirdGoals().firstOrNull()
        if (existingGoals.isNullOrEmpty()) {
            DefaultIslamicData.DEFAULT_WIRD_GOALS.forEach {
                userDataDao.insertWirdGoal(it)
            }
        }
    }

    // Quran
    fun getAllSurahs(): Flow<List<SurahEntity>> = surahDao.getAllSurahs()

    fun getSurah(number: Int): Flow<SurahEntity?> = surahDao.getSurah(number)

    fun getAyahsForSurah(surahNumber: Int): Flow<List<AyahEntity>> =
        ayahDao.getAyahsForSurah(surahNumber)

    fun getAyahsByJuz(juz: Int): Flow<List<AyahEntity>> =
        ayahDao.getAyahsByJuz(juz)

    suspend fun searchQuranAyahs(query: String): List<AyahEntity> = withContext(Dispatchers.IO) {
        val normQuery = ArabicUtils.normalizeArabic(query)
        if (normQuery.length < 2) return@withContext emptyList()
        val allAyahs = ayahDao.searchAyahs(query)
        if (allAyahs.isNotEmpty()) return@withContext allAyahs
        // Fallback normalized search
        ayahDao.getAyahsByJuz(1) // dummy trigger if needed
        emptyList()
    }

    suspend fun downloadFullQuran(onProgress: (Int, Int) -> Unit = { _, _ -> }): QuranNetworkDownloader.DownloadResult {
        val result = QuranNetworkDownloader.downloadFullQuran(onProgress)
        if (result.success && result.surahs.isNotEmpty()) {
            withContext(Dispatchers.IO) {
                surahDao.insertSurahs(result.surahs)
                ayahDao.insertAyahs(result.ayahs)
            }
        }
        return result
    }

    suspend fun downloadAdhkar(): QuranNetworkDownloader.AdhkarDownloadResult {
        val result = QuranNetworkDownloader.downloadAdhkar()
        if (result.success && result.items.isNotEmpty()) {
            withContext(Dispatchers.IO) {
                adhkarDao.insertAdhkar(result.items)
            }
        }
        return result
    }

    // Adhkar
    fun getAdhkarByCategory(category: String): Flow<List<AdhkarItemEntity>> =
        adhkarDao.getAdhkarByCategory(category)

    fun getAllAdhkar(): Flow<List<AdhkarItemEntity>> = adhkarDao.getAllAdhkar()

    fun getAdhkarProgress(date: String): Flow<List<AdhkarProgressEntity>> =
        adhkarDao.getAdhkarProgress(date)

    suspend fun incrementDhikrCount(date: String, dhikrId: String, currentCount: Int, maxRepeat: Int) =
        withContext(Dispatchers.IO) {
            val newCount = minOf(maxRepeat, currentCount + 1)
            adhkarDao.saveProgress(
                AdhkarProgressEntity(
                    id = "${date}_$dhikrId",
                    date = date,
                    dhikrId = dhikrId,
                    currentCount = newCount
                )
            )
        }

    // Tasbeeh
    fun getTasbeehLogs(): Flow<List<TasbeehLogEntity>> = userDataDao.getTasbeehLogs()

    suspend fun logTasbeeh(dhikr: String, count: Int, target: Int) = withContext(Dispatchers.IO) {
        if (count > 0) {
            userDataDao.insertTasbeehLog(
                TasbeehLogEntity(
                    date = ArabicUtils.getTodayIso(),
                    dhikr = dhikr,
                    count = count,
                    target = target
                )
            )
        }
    }

    // Daily Wird
    fun getWirdGoals(): Flow<List<DailyWirdGoalEntity>> = userDataDao.getWirdGoals()

    fun getWirdLogsForDate(date: String): Flow<List<DailyWirdLogEntity>> =
        userDataDao.getWirdLogsForDate(date)

    fun getWirdLogsForDates(dates: List<String>): Flow<List<DailyWirdLogEntity>> =
        userDataDao.getWirdLogsForDates(dates)

    suspend fun addWirdGoal(title: String, target: Int, kind: String) = withContext(Dispatchers.IO) {
        val id = "goal_" + UUID.randomUUID().toString().take(8)
        userDataDao.insertWirdGoal(
            DailyWirdGoalEntity(
                id = id,
                title = title.trim(),
                target = target,
                kind = kind
            )
        )
    }

    suspend fun updateWirdProgress(date: String, goalId: String, currentVal: Int, delta: Int) =
        withContext(Dispatchers.IO) {
            val newVal = maxOf(0, currentVal + delta)
            userDataDao.saveWirdLog(
                DailyWirdLogEntity(
                    id = "${date}_$goalId",
                    date = date,
                    goalId = goalId,
                    progress = newVal
                )
            )
        }

    suspend fun deleteWirdGoal(id: String) = withContext(Dispatchers.IO) {
        userDataDao.deleteWirdGoal(id)
    }

    // Tadabbur
    fun getTadabburNotes(): Flow<List<TadabburNoteEntity>> = userDataDao.getTadabburNotes()

    suspend fun saveTadabburNote(id: String?, text: String, surah: Int?, ayah: Int?) =
        withContext(Dispatchers.IO) {
            val noteId = id ?: ("note_" + UUID.randomUUID().toString().take(8))
            userDataDao.insertTadabburNote(
                TadabburNoteEntity(
                    id = noteId,
                    text = text.trim(),
                    surahNumber = surah,
                    ayahNumber = ayah
                )
            )
        }

    suspend fun deleteTadabburNote(id: String) = withContext(Dispatchers.IO) {
        userDataDao.deleteTadabburNote(id)
    }

    // Calendar Notes
    fun getCalendarNotes(): Flow<List<CalendarNoteEntity>> = userDataDao.getCalendarNotes()

    suspend fun addCalendarNote(date: String, text: String) = withContext(Dispatchers.IO) {
        val id = "cal_" + UUID.randomUUID().toString().take(8)
        userDataDao.insertCalendarNote(
            CalendarNoteEntity(
                id = id,
                date = date,
                text = text.trim()
            )
        )
    }

    suspend fun deleteCalendarNote(id: String) = withContext(Dispatchers.IO) {
        userDataDao.deleteCalendarNote(id)
    }

    // Bookmarks & Favorites
    fun getBookmarks(): Flow<List<BookmarkEntity>> = userDataDao.getBookmarks()

    suspend fun toggleBookmark(surahNumber: Int, ayahNumber: Int, isBookmarked: Boolean) =
        withContext(Dispatchers.IO) {
            val id = "${surahNumber}_$ayahNumber"
            if (isBookmarked) {
                userDataDao.deleteBookmark(id)
            } else {
                userDataDao.addBookmark(
                    BookmarkEntity(
                        id = id,
                        surahNumber = surahNumber,
                        ayahNumber = ayahNumber
                    )
                )
            }
        }

    fun getFavorites(): Flow<List<FavoriteEntity>> = userDataDao.getFavorites()

    suspend fun toggleFavorite(type: String, itemId: String, isFav: Boolean) =
        withContext(Dispatchers.IO) {
            val id = "${type}_$itemId"
            if (isFav) {
                userDataDao.deleteFavorite(id)
            } else {
                userDataDao.addFavorite(
                    FavoriteEntity(
                        id = id,
                        type = type,
                        itemId = itemId
                    )
                )
            }
        }

    // Last Read & Preferences
    fun observePreference(key: String): Flow<String?> = userDataDao.observePreference(key)

    suspend fun getPreference(key: String): String? = withContext(Dispatchers.IO) {
        userDataDao.getPreference(key)
    }

    suspend fun setPreference(key: String, value: String) = withContext(Dispatchers.IO) {
        userDataDao.setPreference(PreferenceEntity(key, value))
    }

    suspend fun saveLastRead(surahNumber: Int, ayahNumber: Int) = withContext(Dispatchers.IO) {
        userDataDao.setPreference(PreferenceEntity("last_read_surah", surahNumber.toString()))
        userDataDao.setPreference(PreferenceEntity("last_read_ayah", ayahNumber.toString()))
    }

    // Backup & Restore
    suspend fun exportBackupJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("app", "rafiq-muslim-pro")
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())

        val data = JSONObject()

        // Notes
        val notes = userDataDao.getTadabburNotes().firstOrNull() ?: emptyList()
        val notesArray = JSONArray()
        notes.forEach { n ->
            val obj = JSONObject()
            obj.put("id", n.id)
            obj.put("text", n.text)
            n.surahNumber?.let { obj.put("surah", it) }
            n.ayahNumber?.let { obj.put("ayah", it) }
            obj.put("ts", n.timestamp)
            notesArray.put(obj)
        }
        data.put("tadabbur", notesArray)

        // Bookmarks
        val bookmarks = userDataDao.getBookmarks().firstOrNull() ?: emptyList()
        val bmArray = JSONArray()
        bookmarks.forEach { bm ->
            val obj = JSONObject()
            obj.put("surah", bm.surahNumber)
            obj.put("ayah", bm.ayahNumber)
            bmArray.put(obj)
        }
        data.put("bookmarks", bmArray)

        // Favorites
        val favs = userDataDao.getFavorites().firstOrNull() ?: emptyList()
        val favArray = JSONArray()
        favs.forEach { f ->
            val obj = JSONObject()
            obj.put("type", f.type)
            obj.put("itemId", f.itemId)
            favArray.put(obj)
        }
        data.put("favorites", favArray)

        root.put("data", data)
        root.toString(2)
    }

    suspend fun importBackupJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (!root.has("data")) return@withContext false
            val data = root.getJSONObject("data")

            if (data.has("tadabbur")) {
                val notesArray = data.getJSONArray("tadabbur")
                for (i in 0 until notesArray.length()) {
                    val o = notesArray.getJSONObject(i)
                    userDataDao.insertTadabburNote(
                        TadabburNoteEntity(
                            id = o.optString("id", UUID.randomUUID().toString()),
                            text = o.getString("text"),
                            surahNumber = if (o.has("surah")) o.getInt("surah") else null,
                            ayahNumber = if (o.has("ayah")) o.getInt("ayah") else null,
                            timestamp = o.optLong("ts", System.currentTimeMillis())
                        )
                    )
                }
            }

            if (data.has("bookmarks")) {
                val bmArray = data.getJSONArray("bookmarks")
                for (i in 0 until bmArray.length()) {
                    val o = bmArray.getJSONObject(i)
                    val s = o.getInt("surah")
                    val a = o.getInt("ayah")
                    userDataDao.addBookmark(BookmarkEntity("${s}_$a", s, a))
                }
            }

            if (data.has("favorites")) {
                val favArray = data.getJSONArray("favorites")
                for (i in 0 until favArray.length()) {
                    val o = favArray.getJSONObject(i)
                    val t = o.getString("type")
                    val item = o.getString("itemId")
                    userDataDao.addFavorite(FavoriteEntity("${t}_$item", t, item))
                }
            }

            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun wipeAllUserData() = withContext(Dispatchers.IO) {
        userDataDao.clearTasbeehLogs()
        userDataDao.clearTadabbur()
        userDataDao.clearWirdGoals()
        userDataDao.clearWirdLogs()
        userDataDao.clearCalendarNotes()
        userDataDao.clearBookmarks()
        userDataDao.clearFavorites()
        userDataDao.clearAllAdhkarProgress()
        userDataDao.clearPreferences()
        // Re-seed default goals
        DefaultIslamicData.DEFAULT_WIRD_GOALS.forEach {
            userDataDao.insertWirdGoal(it)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: RafiqRepository? = null

        fun getRepository(context: Context): RafiqRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getDatabase(context)
                val repo = RafiqRepository(db)
                INSTANCE = repo
                repo
            }
        }
    }
}
