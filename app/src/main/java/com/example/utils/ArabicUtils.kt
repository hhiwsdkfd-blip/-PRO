package com.example.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object ArabicUtils {

    val HIJRI_MONTHS = listOf(
        "محرم", "صفر", "ربيع الأول", "ربيع الآخر",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
    )

    val WEEK_DAYS = listOf(
        "الأحد", "الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت"
    )

    fun toArabicDigits(number: Number): String {
        val s = number.toString()
        val builder = java.lang.StringBuilder()
        for (ch in s) {
            when (ch) {
                '0' -> builder.append('٠')
                '1' -> builder.append('١')
                '2' -> builder.append('٢')
                '3' -> builder.append('٣')
                '4' -> builder.append('٤')
                '5' -> builder.append('٥')
                '6' -> builder.append('٦')
                '7' -> builder.append('٧')
                '8' -> builder.append('٨')
                '9' -> builder.append('٩')
                else -> builder.append(ch)
            }
        }
        return builder.toString()
    }

    fun normalizeArabic(text: String): String {
        return text
            .replace(Regex("[\\u0610-\\u061A\\u064B-\\u065F\\u0670\\u06D6-\\u06ED\\u0640]"), "")
            .replace(Regex("[أإآٱ]"), "ا")
            .replace('ى', 'ي')
            .replace('ة', 'ه')
            .trim()
            .lowercase(Locale.ROOT)
    }

    fun getTodayIso(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    fun formatGregorian(date: Date = Date()): String {
        val sdf = SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar"))
        return sdf.format(date)
    }

    data class HijriDate(val year: Int, val month: Int, val day: Int) {
        val monthName: String
            get() = if (month in 1..12) HIJRI_MONTHS[month - 1] else ""

        val formatted: String
            get() = "${toArabicDigits(day)} $monthName ${toArabicDigits(year)} هـ"
    }

    // Algorithmic Umm Al-Qura / Islamic lunar calendar calculation
    fun getHijriDate(date: Date = Date(), dayOffset: Int = 0): HijriDate {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_YEAR, dayOffset)

        var y = cal.get(Calendar.YEAR)
        var m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)

        if (m < 3) {
            y -= 1
            m += 12
        }

        val a = y / 100
        val b = 2 - a + a / 4
        val jd = (365.25 * (y + 4716)).toLong() + (30.6001 * (m + 1)).toLong() + d + b - 1524

        // Julian Day to Hijri conversion
        val l = jd - 1948440 + 10632
        val n = ((l - 1) / 10631).toInt()
        val lPrime = l - 10631 * n + 354
        val j = (((10985 - lPrime) / 5316).toInt()) * (((50 * lPrime) / 17719).toInt()) +
                ((lPrime / 5670).toInt()) * (((43 * lPrime) / 15238).toInt())
        val lDoublePrime = lPrime - (((30 - j) / 15).toInt()) * (((17719 * j) / 50).toInt()) -
                ((j / 16).toInt()) * (((15238 * j) / 43).toInt()) + 29
        val hMonth = ((24 * lDoublePrime) / 709).toInt()
        val hDay = (lDoublePrime - ((709 * hMonth) / 24).toInt()).toInt()
        val hYear = (30 * n + j - 30).toInt()

        return HijriDate(
            year = maxOf(1400, hYear),
            month = minOf(12, maxOf(1, hMonth)),
            day = minOf(30, maxOf(1, hDay))
        )
    }

    fun getIslamicOccasion(month: Int, day: Int): String? {
        return when {
            month == 1 && day == 1 -> "رأس السنة الهجرية"
            month == 1 && day == 9 -> "تاسوعاء"
            month == 1 && day == 10 -> "يوم عاشوراء"
            month == 3 && day == 12 -> "المولد النبوي الشريف"
            month == 7 && day == 27 -> "الإسراء والمعراج"
            month == 8 && day == 15 -> "ليلة النصف من شعبان"
            month == 9 && day == 1 -> "بداية شهر رمضان المبارك"
            month == 9 && day in 21..30 && day % 2 == 1 -> "إحدى ليالي العشر الأواخر (تحروا ليلة القدر)"
            month == 10 && day == 1 -> "عيد الفطر المبارك (اليوم الأول)"
            month == 10 && day in 2..3 -> "أيام عيد الفطر المبارك"
            month == 12 && day == 8 -> "يوم التروية"
            month == 12 && day == 9 -> "يوم عرفة"
            month == 12 && day == 10 -> "عيد الأضحى المبارك"
            month == 12 && day in 11..13 -> "أيام التشريق"
            else -> null
        }
    }
}
