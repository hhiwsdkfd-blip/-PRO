package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DefaultIslamicData
import com.example.data.RafiqRepository
import com.example.model.*
import com.example.utils.ArabicUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class RafiqViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RafiqRepository.getRepository(application)

    // Current date
    val todayIso: String = ArabicUtils.getTodayIso()

    // Quran Data
    val surahs: StateFlow<List<SurahEntity>> = repository.getAllSurahs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.getBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<FavoriteEntity>> = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Last Read Position
    private val _lastReadSurah = MutableStateFlow<Int?>(null)
    val lastReadSurah: StateFlow<Int?> = _lastReadSurah.asStateFlow()

    private val _lastReadAyah = MutableStateFlow<Int?>(null)
    val lastReadAyah: StateFlow<Int?> = _lastReadAyah.asStateFlow()

    // Download Quran Progress
    private val _isDownloadingQuran = MutableStateFlow(false)
    val isDownloadingQuran: StateFlow<Boolean> = _isDownloadingQuran.asStateFlow()

    private val _downloadQuranProgress = MutableStateFlow("")
    val downloadQuranProgress: StateFlow<String> = _downloadQuranProgress.asStateFlow()

    private val _downloadStatusMsg = MutableStateFlow<String?>(null)
    val downloadStatusMsg: StateFlow<String?> = _downloadStatusMsg.asStateFlow()

    // Tasbeeh State
    private val _currentTasbeehDhikr = MutableStateFlow("سُبْحَانَ اللَّهِ")
    val currentTasbeehDhikr: StateFlow<String> = _currentTasbeehDhikr.asStateFlow()

    private val _tasbeehCount = MutableStateFlow(0)
    val tasbeehCount: StateFlow<Int> = _tasbeehCount.asStateFlow()

    private val _tasbeehTarget = MutableStateFlow(33)
    val tasbeehTarget: StateFlow<Int> = _tasbeehTarget.asStateFlow()

    val tasbeehLogs: StateFlow<List<TasbeehLogEntity>> = repository.getTasbeehLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Adhkar Progress for today
    val todayAdhkarProgress: StateFlow<List<AdhkarProgressEntity>> =
        repository.getAdhkarProgress(todayIso)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Daily Wird
    val wirdGoals: StateFlow<List<DailyWirdGoalEntity>> = repository.getWirdGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayWirdLogs: StateFlow<List<DailyWirdLogEntity>> = repository.getWirdLogsForDate(todayIso)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Tadabbur Notes
    val tadabburNotes: StateFlow<List<TadabburNoteEntity>> = repository.getTadabburNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calendar Notes
    val calendarNotes: StateFlow<List<CalendarNoteEntity>> = repository.getCalendarNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Settings
    private val _quranFontSize = MutableStateFlow(26)
    val quranFontSize: StateFlow<Int> = _quranFontSize.asStateFlow()

    private val _appTheme = MutableStateFlow("auto") // "auto", "light", "dark"
    val appTheme: StateFlow<String> = _appTheme.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(true)
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    private val _hijriOffset = MutableStateFlow(0)
    val hijriOffset: StateFlow<Int> = _hijriOffset.asStateFlow()

    // Global Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfNeeded()
            loadSettings()
            loadLastRead()
        }
    }

    private suspend fun loadSettings() {
        repository.getPreference("font_size")?.toIntOrNull()?.let { _quranFontSize.value = it }
        repository.getPreference("app_theme")?.let { _appTheme.value = it }
        repository.getPreference("vibration")?.let { _vibrationEnabled.value = it.toBoolean() }
        repository.getPreference("hijri_offset")?.toIntOrNull()?.let { _hijriOffset.value = it }
    }

    private suspend fun loadLastRead() {
        val s = repository.getPreference("last_read_surah")?.toIntOrNull()
        val a = repository.getPreference("last_read_ayah")?.toIntOrNull()
        if (s != null && a != null) {
            _lastReadSurah.value = s
            _lastReadAyah.value = a
        }
    }

    fun setLastRead(surah: Int, ayah: Int) {
        _lastReadSurah.value = surah
        _lastReadAyah.value = ayah
        viewModelScope.launch {
            repository.saveLastRead(surah, ayah)
        }
    }

    // Quran Methods
    fun getAyahsForSurah(surahNumber: Int): Flow<List<AyahEntity>> =
        repository.getAyahsForSurah(surahNumber)

    fun toggleBookmark(surahNumber: Int, ayahNumber: Int, currentBookmarked: Boolean) {
        viewModelScope.launch {
            repository.toggleBookmark(surahNumber, ayahNumber, currentBookmarked)
        }
    }

    fun toggleFavorite(type: String, itemId: String, currentFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(type, itemId, currentFav)
        }
    }

    fun downloadFullQuran() {
        if (_isDownloadingQuran.value) return
        _isDownloadingQuran.value = true
        _downloadStatusMsg.value = "بدء تنزيل المصحف الشريف..."
        viewModelScope.launch {
            val res = repository.downloadFullQuran { cur, total ->
                _downloadQuranProgress.value = "$cur / $total سورة"
            }
            _isDownloadingQuran.value = false
            _downloadStatusMsg.value = res.message
        }
    }

    fun downloadAdhkarFromNet() {
        viewModelScope.launch {
            _downloadStatusMsg.value = "جارٍ تحميل أذكار حصن المسلم..."
            val res = repository.downloadAdhkar()
            _downloadStatusMsg.value = res.message
        }
    }

    fun clearStatusMsg() {
        _downloadStatusMsg.value = null
    }

    // Tasbeeh Methods
    fun tapTasbeeh() {
        val newCount = _tasbeehCount.value + 1
        _tasbeehCount.value = newCount
        triggerVibration(isTarget = (newCount == _tasbeehTarget.value))
    }

    fun resetTasbeeh() {
        val c = _tasbeehCount.value
        if (c > 0) {
            viewModelScope.launch {
                repository.logTasbeeh(_currentTasbeehDhikr.value, c, _tasbeehTarget.value)
            }
        }
        _tasbeehCount.value = 0
    }

    fun setTasbeehDhikr(dhikr: String) {
        resetTasbeeh()
        _currentTasbeehDhikr.value = dhikr
    }

    fun setTasbeehTarget(target: Int) {
        _tasbeehTarget.value = target
    }

    private fun triggerVibration(isTarget: Boolean) {
        if (!_vibrationEnabled.value) return
        try {
            val context = getApplication<Application>()
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = if (isTarget) {
                        VibrationEffect.createWaveform(longArrayOf(0, 70, 50, 70), -1)
                    } else {
                        VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE)
                    }
                    vibrator.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(if (isTarget) 120 else 25)
                }
            }
        } catch (_: Exception) {}
    }

    // Adhkar Methods
    fun getAdhkarForCategory(category: String): Flow<List<AdhkarItemEntity>> =
        repository.getAdhkarByCategory(category)

    fun incrementAdhkar(dhikrId: String, currentCount: Int, maxRepeat: Int) {
        viewModelScope.launch {
            repository.incrementDhikrCount(todayIso, dhikrId, currentCount, maxRepeat)
            triggerVibration(isTarget = (currentCount + 1 >= maxRepeat))
        }
    }

    // Wird Goals
    fun addWirdGoal(title: String, target: Int, kind: String) {
        viewModelScope.launch {
            repository.addWirdGoal(title, target, kind)
        }
    }

    fun updateWirdProgress(goalId: String, currentVal: Int, delta: Int) {
        viewModelScope.launch {
            repository.updateWirdProgress(todayIso, goalId, currentVal, delta)
        }
    }

    fun deleteWirdGoal(id: String) {
        viewModelScope.launch {
            repository.deleteWirdGoal(id)
        }
    }

    // Tadabbur
    fun saveTadabburNote(id: String?, text: String, surah: Int?, ayah: Int?) {
        viewModelScope.launch {
            repository.saveTadabburNote(id, text, surah, ayah)
        }
    }

    fun deleteTadabburNote(id: String) {
        viewModelScope.launch {
            repository.deleteTadabburNote(id)
        }
    }

    // Calendar
    fun addCalendarNote(date: String, text: String) {
        viewModelScope.launch {
            repository.addCalendarNote(date, text)
        }
    }

    fun deleteCalendarNote(id: String) {
        viewModelScope.launch {
            repository.deleteCalendarNote(id)
        }
    }

    fun setHijriOffset(delta: Int) {
        val newVal = (_hijriOffset.value + delta).coerceIn(-2, 2)
        _hijriOffset.value = newVal
        viewModelScope.launch {
            repository.setPreference("hijri_offset", newVal.toString())
        }
    }

    // Settings
    fun updateFontSize(newSize: Int) {
        _quranFontSize.value = newSize.coerceIn(18, 52)
        viewModelScope.launch {
            repository.setPreference("font_size", _quranFontSize.value.toString())
        }
    }

    fun updateTheme(theme: String) {
        _appTheme.value = theme
        viewModelScope.launch {
            repository.setPreference("app_theme", theme)
        }
    }

    fun toggleVibration(enabled: Boolean) {
        _vibrationEnabled.value = enabled
        viewModelScope.launch {
            repository.setPreference("vibration", enabled.toString())
        }
    }

    // Search
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Backup & Restore
    suspend fun exportBackup(): String = repository.exportBackupJson()

    suspend fun importBackup(json: String): Boolean = repository.importBackupJson(json)

    fun wipeAllData(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.wipeAllUserData()
            _tasbeehCount.value = 0
            _lastReadSurah.value = null
            _lastReadAyah.value = null
            onDone()
        }
    }
}
