package com.example.data

import com.example.model.AdhkarItemEntity
import com.example.model.AyahEntity
import com.example.model.SurahEntity
import com.example.utils.ArabicUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object QuranNetworkDownloader {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val QURAN_API_URL = "https://api.alquran.cloud/v1/quran/quran-uthmani"
    private const val ADHKAR_URL_PRIMARY = "https://raw.githubusercontent.com/rn0x/Adhkar-json/main/adhkar.json"
    private const val ADHKAR_URL_BACKUP = "https://cdn.jsdelivr.net/gh/rn0x/Adhkar-json@main/adhkar.json"

    data class DownloadResult(
        val success: Boolean,
        val message: String,
        val surahs: List<SurahEntity> = emptyList(),
        val ayahs: List<AyahEntity> = emptyList()
    )

    data class AdhkarDownloadResult(
        val success: Boolean,
        val message: String,
        val items: List<AdhkarItemEntity> = emptyList()
    )

    suspend fun downloadFullQuran(
        onProgress: (Int, Int) -> Unit = { _, _ -> }
    ): DownloadResult = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(QURAN_API_URL).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext DownloadResult(false, "تعذر الاتصال بالخادم: رمز الخطأ ${response.code}")
            }

            val body = response.body?.string() ?: return@withContext DownloadResult(false, "رد الخادم فارغ")
            val root = JSONObject(body)
            val data = root.optJSONObject("data") ?: return@withContext DownloadResult(false, "بنية الاستجابة غير صحيحة")
            val surahsArray = data.optJSONArray("surahs") ?: return@withContext DownloadResult(false, "قائمة السور مفقودة")

            val surahsList = mutableListOf<SurahEntity>()
            val ayahsList = mutableListOf<AyahEntity>()

            val bismillahText = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"

            for (i in 0 until surahsArray.length()) {
                val surahObj = surahsArray.getJSONObject(i)
                val number = surahObj.getInt("number")
                val englishName = surahObj.optString("englishName", "")
                val name = DefaultIslamicData.ALL_SURAHS.find { it.number == number }?.name
                    ?: surahObj.optString("name", "")
                val revType = if (surahObj.optString("revelationType") == "Meccan") "مكية" else "مدنية"

                val ayahsArray = surahObj.getJSONArray("ayahs")
                var firstJuz = 1

                for (j in 0 until ayahsArray.length()) {
                    val ayahObj = ayahsArray.getJSONObject(j)
                    val numberInSurah = ayahObj.getInt("numberInSurah")
                    var text = ayahObj.getString("text").trim()
                    val juz = ayahObj.optInt("juz", 1)

                    if (j == 0) {
                        firstJuz = juz
                        // If Surah is not Al-Fatihah (1) or At-Tawbah (9) and starts with Bismillah, strip it for clean reading
                        if (number != 1 && number != 9 && text.startsWith(bismillahText)) {
                            text = text.substring(bismillahText.length).trim()
                        }
                    }

                    ayahsList.add(
                        AyahEntity(
                            id = "${number}_$numberInSurah",
                            surahNumber = number,
                            numberInSurah = numberInSurah,
                            text = text,
                            juz = juz
                        )
                    )
                }

                surahsList.add(
                    SurahEntity(
                        number = number,
                        name = name,
                        englishName = englishName,
                        ayahCount = ayahsArray.length(),
                        revelationType = revType,
                        juzStart = firstJuz,
                        hasBasmala = number != 9
                    )
                )

                onProgress(i + 1, surahsArray.length())
            }

            DownloadResult(
                success = true,
                message = "تم تحميل المصحف الشريف كاملًا بنجاح (١١٤ سورة - ٦٢٣٦ آية)",
                surahs = surahsList,
                ayahs = ayahsList
            )
        } catch (e: Exception) {
            DownloadResult(false, "حدث خطأ أثناء تحميل المصحف: ${e.localizedMessage ?: "تحقق من اتصال الإنترنت"}")
        }
    }

    suspend fun downloadAdhkar(): AdhkarDownloadResult = withContext(Dispatchers.IO) {
        val urls = listOf(ADHKAR_URL_PRIMARY, ADHKAR_URL_BACKUP)
        for (url in urls) {
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: continue
                    val array = JSONArray(body)
                    val items = mutableListOf<AdhkarItemEntity>()
                    var orderIdx = 1

                    for (i in 0 until array.length()) {
                        val catObj = array.getJSONObject(i)
                        val catTitle = catObj.optString("category", "")
                        val catKey = mapCategoryKey(catTitle)
                        val dhikrArray = catObj.optJSONArray("array") ?: continue

                        for (j in 0 until dhikrArray.length()) {
                            val it = dhikrArray.getJSONObject(j)
                            val text = it.optString("text", "").trim()
                            if (text.isEmpty()) continue
                            val repeat = maxOf(1, it.optString("count", "1").filter { it.isDigit() }.toIntOrNull() ?: 1)

                            items.add(
                                AdhkarItemEntity(
                                    id = "dhikr_${catObj.opt("id")}_${it.opt("id")}_$orderIdx",
                                    category = catKey,
                                    title = catTitle,
                                    text = text,
                                    repeat = repeat,
                                    reference = "حصن المسلم",
                                    source = "مشروع Adhkar-json",
                                    orderIndex = orderIdx++
                                )
                            )
                        }
                    }

                    if (items.isNotEmpty()) {
                        return@withContext AdhkarDownloadResult(
                            success = true,
                            message = "تم تحميل ${items.size} ذِكرًا من حصن المسلم بنجاح",
                            items = items
                        )
                    }
                }
            } catch (_: Exception) {
                // Try backup URL
            }
        }
        AdhkarDownloadResult(false, "تعذر تحميل الأذكار عبر الإنترنت، تم الإبقاء على الأذكار الأساسية المحفوظة.")
    }

    private fun mapCategoryKey(categoryTitle: String): String {
        val norm = ArabicUtils.normalizeArabic(categoryTitle)
        return when {
            norm.contains("الصباح") && norm.contains("المساء") -> "morning"
            norm.contains("الصباح") -> "morning"
            norm.contains("المساء") -> "evening"
            norm.contains("النوم") -> "sleep"
            norm.contains("الاستيقاظ") -> "wake"
            norm.contains("الصلاه") || norm.contains("الصلاة") -> "after_prayer"
            else -> "misc"
        }
    }
}
