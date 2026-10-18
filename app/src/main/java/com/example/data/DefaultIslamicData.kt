package com.example.data

import com.example.model.AdhkarItemEntity
import com.example.model.AyahEntity
import com.example.model.DailyWirdGoalEntity
import com.example.model.SurahEntity

object DefaultIslamicData {

    data class SurahMeta(
        val number: Int,
        val name: String,
        val englishName: String,
        val ayahCount: Int,
        val revelationType: String,
        val juzStart: Int,
        val hasBasmala: Boolean = true
    )

    val ALL_SURAHS: List<SurahMeta> = listOf(
        SurahMeta(1, "الفاتحة", "Al-Fatihah", 7, "مكية", 1, true),
        SurahMeta(2, "البقرة", "Al-Baqarah", 286, "مدنية", 1, true),
        SurahMeta(3, "آل عمران", "Aal-E-Imran", 200, "مدنية", 3, true),
        SurahMeta(4, "النساء", "An-Nisa", 176, "مدنية", 4, true),
        SurahMeta(5, "المائدة", "Al-Ma'idah", 120, "مدنية", 6, true),
        SurahMeta(6, "الأنعام", "Al-An'am", 165, "مكية", 7, true),
        SurahMeta(7, "الأعراف", "Al-A'raf", 206, "مكية", 8, true),
        SurahMeta(8, "الأنفال", "Al-Anfal", 75, "مدنية", 9, true),
        SurahMeta(9, "التوبة", "At-Tawbah", 129, "مدنية", 10, false),
        SurahMeta(10, "يونس", "Yunus", 109, "مكية", 11, true),
        SurahMeta(11, "هود", "Hud", 123, "مكية", 11, true),
        SurahMeta(12, "يوسف", "Yusuf", 111, "مكية", 12, true),
        SurahMeta(13, "الرعد", "Ar-Ra'd", 43, "مدنية", 13, true),
        SurahMeta(14, "إبراهيم", "Ibrahim", 52, "مكية", 13, true),
        SurahMeta(15, "الحجر", "Al-Hijr", 99, "مكية", 14, true),
        SurahMeta(16, "النحل", "An-Nahl", 128, "مكية", 14, true),
        SurahMeta(17, "الإسراء", "Al-Isra", 111, "مكية", 15, true),
        SurahMeta(18, "الكهف", "Al-Kahf", 110, "مكية", 15, true),
        SurahMeta(19, "مريم", "Maryam", 98, "مكية", 16, true),
        SurahMeta(20, "طه", "Ta-Ha", 135, "مكية", 16, true),
        SurahMeta(21, "الأنبياء", "Al-Anbiya", 112, "مكية", 17, true),
        SurahMeta(22, "الحج", "Al-Hajj", 78, "مدنية", 17, true),
        SurahMeta(23, "المؤمنون", "Al-Mu'minun", 118, "مكية", 18, true),
        SurahMeta(24, "النور", "An-Nur", 64, "مدنية", 18, true),
        SurahMeta(25, "الفرقان", "Al-Furqan", 77, "مكية", 18, true),
        SurahMeta(26, "الشعراء", "Ash-Shu'ara", 227, "مكية", 19, true),
        SurahMeta(27, "النمل", "An-Naml", 93, "مكية", 19, true),
        SurahMeta(28, "القصص", "Al-Qasas", 88, "مكية", 20, true),
        SurahMeta(29, "العنكبوت", "Al-Ankabut", 69, "مكية", 20, true),
        SurahMeta(30, "الروم", "Ar-Rum", 60, "مكية", 21, true),
        SurahMeta(31, "لقمان", "Luqman", 34, "مكية", 21, true),
        SurahMeta(32, "السجدة", "As-Sajdah", 30, "مكية", 21, true),
        SurahMeta(33, "الأحزاب", "Al-Ahzab", 73, "مدنية", 21, true),
        SurahMeta(34, "سبأ", "Saba", 54, "مكية", 22, true),
        SurahMeta(35, "فاطر", "Fatir", 45, "مكية", 22, true),
        SurahMeta(36, "يس", "Ya-Sin", 83, "مكية", 22, true),
        SurahMeta(37, "الصافات", "As-Saffat", 182, "مكية", 23, true),
        SurahMeta(38, "ص", "Sad", 88, "مكية", 23, true),
        SurahMeta(39, "الزمر", "Az-Zumar", 75, "مكية", 23, true),
        SurahMeta(40, "غافر", "Ghafir", 85, "مكية", 24, true),
        SurahMeta(41, "فصلت", "Fussilat", 54, "مكية", 24, true),
        SurahMeta(42, "الشورى", "Ash-Shura", 53, "مكية", 25, true),
        SurahMeta(43, "الزخرف", "Az-Zukhruf", 89, "مكية", 25, true),
        SurahMeta(44, "الدخان", "Ad-Dukhan", 59, "مكية", 25, true),
        SurahMeta(45, "الجاثية", "Al-Jathiyah", 37, "مكية", 25, true),
        SurahMeta(46, "الأحقاف", "Al-Ahqaf", 35, "مكية", 26, true),
        SurahMeta(47, "محمد", "Muhammad", 38, "مدنية", 26, true),
        SurahMeta(48, "الفتح", "Al-Fath", 29, "مدنية", 26, true),
        SurahMeta(49, "الحجرات", "Al-Hujurat", 18, "مدنية", 26, true),
        SurahMeta(50, "ق", "Qaf", 45, "مكية", 26, true),
        SurahMeta(51, "الذاريات", "Adh-Dhariyat", 60, "مكية", 26, true),
        SurahMeta(52, "الطور", "At-Tur", 49, "مكية", 27, true),
        SurahMeta(53, "النجم", "An-Najm", 62, "مكية", 27, true),
        SurahMeta(54, "القمر", "Al-Qamar", 55, "مكية", 27, true),
        SurahMeta(55, "الرحمن", "Ar-Rahman", 78, "مدنية", 27, true),
        SurahMeta(56, "الواقعة", "Al-Waqi'ah", 96, "مكية", 27, true),
        SurahMeta(57, "الحديد", "Al-Hadid", 29, "مدنية", 27, true),
        SurahMeta(58, "المجادلة", "Al-Mujadila", 22, "مدنية", 28, true),
        SurahMeta(59, "الحشر", "Al-Hashr", 24, "مدنية", 28, true),
        SurahMeta(60, "الممتحنة", "Al-Mumtahanah", 13, "مدنية", 28, true),
        SurahMeta(61, "الصف", "As-Saff", 14, "مدنية", 28, true),
        SurahMeta(62, "الجمعة", "Al-Jumu'ah", 11, "مدنية", 28, true),
        SurahMeta(63, "المنافقون", "Al-Munafiqun", 11, "مدنية", 28, true),
        SurahMeta(64, "التغابن", "At-Taghabun", 18, "مدنية", 28, true),
        SurahMeta(65, "الطلاق", "At-Talaq", 12, "مدنية", 28, true),
        SurahMeta(66, "التحريم", "At-Tahrim", 12, "مدنية", 28, true),
        SurahMeta(67, "الملك", "Al-Mulk", 30, "مكية", 29, true),
        SurahMeta(68, "القلم", "Al-Qalam", 52, "مكية", 29, true),
        SurahMeta(69, "الحاقة", "Al-Haqqah", 52, "مكية", 29, true),
        SurahMeta(70, "المعارج", "Al-Ma'arij", 44, "مكية", 29, true),
        SurahMeta(71, "نوح", "Nuh", 28, "مكية", 29, true),
        SurahMeta(72, "الجن", "Al-Jinn", 28, "مكية", 29, true),
        SurahMeta(73, "المزمل", "Al-Muzzammil", 20, "مكية", 29, true),
        SurahMeta(74, "المدثر", "Al-Muddathir", 56, "مكية", 29, true),
        SurahMeta(75, "القيامة", "Al-Qiyamah", 40, "مكية", 29, true),
        SurahMeta(76, "الإنسان", "Al-Insan", 31, "مدنية", 29, true),
        SurahMeta(77, "المرسلات", "Al-Mursalat", 50, "مكية", 29, true),
        SurahMeta(78, "النبأ", "An-Naba", 40, "مكية", 30, true),
        SurahMeta(79, "النازعات", "An-Nazi'at", 46, "مكية", 30, true),
        SurahMeta(80, "عبس", "Abasa", 42, "مكية", 30, true),
        SurahMeta(81, "التكوير", "At-Takwir", 29, "مكية", 30, true),
        SurahMeta(82, "الانفطار", "Al-Infitar", 19, "مكية", 30, true),
        SurahMeta(83, "المطففين", "Al-Mutaffifin", 36, "مكية", 30, true),
        SurahMeta(84, "الانشقاق", "Al-Inshiqaq", 25, "مكية", 30, true),
        SurahMeta(85, "البروج", "Al-Buruj", 22, "مكية", 30, true),
        SurahMeta(86, "الطارق", "At-Tariq", 17, "مكية", 30, true),
        SurahMeta(87, "الأعلى", "Al-A'la", 19, "مكية", 30, true),
        SurahMeta(88, "الغاشية", "Al-Ghashiyah", 26, "مكية", 30, true),
        SurahMeta(89, "الفجر", "Al-Fajr", 30, "مكية", 30, true),
        SurahMeta(90, "البلد", "Al-Balad", 20, "مكية", 30, true),
        SurahMeta(91, "الشمس", "Ash-Shams", 15, "مكية", 30, true),
        SurahMeta(92, "الليل", "Al-Layl", 21, "مكية", 30, true),
        SurahMeta(93, "الضحى", "Ad-Duha", 11, "مكية", 30, true),
        SurahMeta(94, "الشرح", "Ash-Sharh", 8, "مكية", 30, true),
        SurahMeta(95, "التين", "At-Tin", 8, "مكية", 30, true),
        SurahMeta(96, "العلق", "Al-Alaq", 19, "مكية", 30, true),
        SurahMeta(97, "القدر", "Al-Qadr", 5, "مكية", 30, true),
        SurahMeta(98, "البينة", "Al-Bayyinah", 8, "مدنية", 30, true),
        SurahMeta(99, "الزلزلة", "Az-Zalzalah", 8, "مدنية", 30, true),
        SurahMeta(100, "العاديات", "Al-Adiyat", 11, "مكية", 30, true),
        SurahMeta(101, "القارعة", "Al-Qari'ah", 11, "مكية", 30, true),
        SurahMeta(102, "التكاثر", "At-Takathur", 8, "مكية", 30, true),
        SurahMeta(103, "العصر", "Al-Asr", 3, "مكية", 30, true),
        SurahMeta(104, "الهمزة", "Al-Humazah", 9, "مكية", 30, true),
        SurahMeta(105, "الفيل", "Al-Fil", 5, "مكية", 30, true),
        SurahMeta(106, "قريش", "Quraysh", 4, "مكية", 30, true),
        SurahMeta(107, "الماعون", "Al-Ma'un", 7, "مكية", 30, true),
        SurahMeta(108, "الكوثر", "Al-Kawthar", 3, "مكية", 30, true),
        SurahMeta(109, "الكافرون", "Al-Kafirun", 6, "مكية", 30, true),
        SurahMeta(110, "النصر", "An-Nasr", 3, "مدنية", 30, true),
        SurahMeta(111, "المسد", "Al-Masad", 5, "مكية", 30, true),
        SurahMeta(112, "الإخلاص", "Al-Ikhlas", 4, "مكية", 30, true),
        SurahMeta(113, "الفلق", "Al-Falaq", 5, "مكية", 30, true),
        SurahMeta(114, "الناس", "An-Nas", 6, "مكية", 30, true)
    )

    fun getInitialSurahs(): List<SurahEntity> {
        return ALL_SURAHS.map {
            SurahEntity(
                number = it.number,
                name = it.name,
                englishName = it.englishName,
                ayahCount = it.ayahCount,
                revelationType = it.revelationType,
                juzStart = it.juzStart,
                hasBasmala = it.hasBasmala
            )
        }
    }

    fun getInitialAyahs(): List<AyahEntity> {
        val list = mutableListOf<AyahEntity>()

        // Surah Al-Fatihah (1)
        val fatihah = listOf(
            "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
            "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
            "الرَّحْمَٰنِ الرَّحِيمِ",
            "مَالِكِ يَوْمِ الدِّينِ",
            "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ",
            "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ",
            "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ"
        )
        fatihah.forEachIndexed { i, text ->
            list.add(AyahEntity("1_${i + 1}", 1, i + 1, text, 1))
        }

        // Surah Al-Mulk (67)
        val mulk = listOf(
            "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ",
            "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا ۖ مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ ۖ فَارْجِعِ الْبَصَرَ هَلْ تَرَىٰ مِن فُطُورٍ",
            "ثُمَّ ارْجِعِ الْبَصَرَ كَرَّتَيْنِ يَنقَلِبْ إِلَيْكَ الْبَصَرُ خَاسِئًا وَهُوَ حَسِيرٌ",
            "وَلَقَدْ زَيَّنَّا السَّمَاءَ الدُّنْيَا بِمَصَابِيحَ وَجَعَلْنَاهَا رُجُومًا لِّلشَّيَاطِينِ ۖ وَأَعْتَدْنَا لَهُمْ عَذَابَ السَّعِيرِ",
            "وَلِلَّذِينَ كَفَرُوا بِرَبِّهِمْ عَذَابُ جَهَنَّمَ ۖ وَبِئْسَ الْمَصِيرُ",
            "إِذَا أُلْقُوا فِيهَا سَمِعُوا لَهَا شَهِيقًا وَهِيَ تَفُورُ",
            "تَكَادُ تَمَيَّزُ مِنَ الْغَيْظِ ۖ كُلَّمَا أُلْقِيَ فِيهَا فَوْجٌ سَأَلَهُمْ خَزَنَتُهَا أَلَمْ يَأْتِكُمْ نَذِيرٌ",
            "قَالُوا بَلَىٰ قَدْ جَاءَنَا نَذِيرٌ فَكَذَّبْنَا وَقُلْنَا مَا نَزَّلَ اللَّهُ مِن شَيْءٍ إِنْ أَنتُمْ إِلَّا فِي ضَلَالٍ كَبِيرٍ",
            "وَقَالُوا لَوْ كُنَّا نَسْمَعُ أَوْ نَعْقِلُ مَا كُنَّا فِي أَصْحَابِ السَّعِيرِ",
            "فَاعْتَرَفُوا بِذَنبِهِمْ فَسُحْقًا لِّأَصْحَابِ السَّعِيرِ",
            "إِنَّ الَّذِينَ يَخْشَوْنَ رَبَّهُمَّ بِالْغَيْبِ لَهُم مَّغْفِرَةٌ وَأَجْرٌ كَبِيرٌ",
            "وَأَسِرُّوا قَوْلَكُمْ أَوِ اجْهَرُوا بِهِ ۖ إِنَّهُ عَلِيمٌ بِذَاتِ الصُّدُورِ",
            "أَلَا يَعْلَمُ مَنْ خَلَقَ وَهُوَ اللَّطِيفُ الْخَبِيرُ",
            "هُوَ الَّذِي جَعَلَ لَكُمُ الْأَرْضَ ذَلُولًا فَامْشُوا فِي مَنَاكِبِهَا وَكُلُوا مِن رِّزْقِهِ ۖ وَإِلَيْهِ النُّشُورُ"
        )
        mulk.forEachIndexed { i, text ->
            list.add(AyahEntity("67_${i + 1}", 67, i + 1, text, 29))
        }

        // Surah Al-Ikhlas (112)
        val ikhlas = listOf(
            "قُلْ هُوَ اللَّهُ أَحَدٌ",
            "اللَّهُ الصَّمَدُ",
            "لَمْ يَلِدْ وَلَمْ يُولَدْ",
            "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ"
        )
        ikhlas.forEachIndexed { i, text ->
            list.add(AyahEntity("112_${i + 1}", 112, i + 1, text, 30))
        }

        // Surah Al-Falaq (113)
        val falaq = listOf(
            "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ",
            "مِن شَرِّ مَا خَلَقَ",
            "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ",
            "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ",
            "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ"
        )
        falaq.forEachIndexed { i, text ->
            list.add(AyahEntity("113_${i + 1}", 113, i + 1, text, 30))
        }

        // Surah An-Nas (114)
        val nas = listOf(
            "قُلْ أَعُوذُ بِرَبِّ النَّاسِ",
            "مَلِكِ النَّاسِ",
            "إِلَٰهِ النَّاسِ",
            "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ",
            "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ",
            "مِنَ الْجِنَّةِ وَالنَّاسِ"
        )
        nas.forEachIndexed { i, text ->
            list.add(AyahEntity("114_${i + 1}", 114, i + 1, text, 30))
        }

        return list
    }

    val INITIAL_ADHKAR: List<AdhkarItemEntity> = listOf(
        // Morning Adhkar
        AdhkarItemEntity(
            id = "m1",
            category = "morning",
            title = "آية الكرسي",
            text = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
            repeat = 1,
            reference = "من قالها حين يصبح أُجير من الجن حتى يمسي",
            source = "رواه الحاكم والنسائي",
            orderIndex = 1
        ),
        AdhkarItemEntity(
            id = "m2",
            category = "morning",
            title = "المعوذات والإخلاص",
            text = "قُلْ هُوَ اللَّهُ أَحَدٌ ۞ قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۞ قُلْ أَعُوذُ بِرَبِّ النَّاسِ",
            repeat = 3,
            reference = "من قالها ثلاث مرات حين يصبح وحين يمسي كفته من كل شيء",
            source = "رواه أبو داود والترمذي",
            orderIndex = 2
        ),
        AdhkarItemEntity(
            id = "m3",
            category = "morning",
            title = "أصبحنا وأصبح الملك لله",
            text = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذَا الْيَوْمِ وَخَيْرَ مَا بَعْدَهُ، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذَا الْيَوْمِ وَشَرِّ مَا بَعْدَهُ، رَبِّ أَعُوذُ بِكَ مِنَ الْكَسَلِ وَسُوءِ الْكِبَرِ، رَبِّ أَعُوذُ بِكَ مِنْ عَذَابٍ فِي النَّارِ وَعَذَابٍ فِي الْقَبْرِ.",
            repeat = 1,
            reference = "دعاء الصباح النبوي الشريف",
            source = "رواه مسلم",
            orderIndex = 3
        ),
        AdhkarItemEntity(
            id = "m4",
            category = "morning",
            title = "سيد الاستغفار",
            text = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ.",
            repeat = 1,
            reference = "من قالها موقنًا بها حين يصبح فمات دخل الجنة",
            source = "رواه البخاري",
            orderIndex = 4
        ),
        AdhkarItemEntity(
            id = "m5",
            category = "morning",
            title = "الرضا بالله رباً",
            text = "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا.",
            repeat = 3,
            reference = "كان حقاً على الله أن يرضيه يوم القيامة",
            source = "رواه أحمد والترمذي",
            orderIndex = 5
        ),
        AdhkarItemEntity(
            id = "m6",
            category = "morning",
            title = "بسم الله الذي لا يضر مع اسمه شيء",
            text = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ.",
            repeat = 3,
            reference = "لم يضره شيء",
            source = "رواه الترمذي وأبو داود",
            orderIndex = 6
        ),
        AdhkarItemEntity(
            id = "m7",
            category = "morning",
            title = "يا حي يا قيوم برحمتك أستغيث",
            text = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ، وَلَا تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ.",
            repeat = 1,
            reference = "وصية النبي ﷺ لفاطمة رضي الله عنها",
            source = "رواه الحاكم والنسائي",
            orderIndex = 7
        ),

        // Evening Adhkar
        AdhkarItemEntity(
            id = "e1",
            category = "evening",
            title = "أمسينا وأمسى الملك لله",
            text = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذِهِ اللَّيْلَةِ وَخَيْرَ مَا بَعْدَهَا، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذِهِ اللَّيْلَةِ وَشَرِّ مَا بَعْدَهَا.",
            repeat = 1,
            reference = "دعاء المساء النبوي الشريف",
            source = "رواه مسلم",
            orderIndex = 1
        ),
        AdhkarItemEntity(
            id = "e2",
            category = "evening",
            title = "سيد الاستغفار",
            text = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عهدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ.",
            repeat = 1,
            reference = "من قالها حين يمسي فمات دخل الجنة",
            source = "رواه البخاري",
            orderIndex = 2
        ),
        AdhkarItemEntity(
            id = "e3",
            category = "evening",
            title = "الاستعاذة بكلمات الله التامات",
            text = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ.",
            repeat = 3,
            reference = "لم تضره حمة أو سوء تلك الليلة",
            source = "رواه مسلم",
            orderIndex = 3
        ),

        // After Prayer Adhkar
        AdhkarItemEntity(
            id = "ap1",
            category = "after_prayer",
            title = "الاستغفار والسلام",
            text = "أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ. اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ.",
            repeat = 1,
            reference = "يقال دبر كل صلاة مكتوبة",
            source = "رواه مسلم",
            orderIndex = 1
        ),
        AdhkarItemEntity(
            id = "ap2",
            category = "after_prayer",
            title = "التسبيح والتحميد والتكبير",
            text = "سُبْحَانَ اللَّهِ (٣٣)، الْحَمْدُ لِلَّهِ (٣٣)، اللَّهُ أَكْبَرُ (٣٣)، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.",
            repeat = 1,
            reference = "غُفرت خطاياه وإن كانت مثل زبد البحر",
            source = "رواه مسلم",
            orderIndex = 2
        ),

        // Sleep Adhkar
        AdhkarItemEntity(
            id = "sl1",
            category = "sleep",
            title = "باسمك ربي وضعت جنبي",
            text = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ.",
            repeat = 1,
            reference = "دعاء النوم",
            source = "رواه البخاري ومسلم",
            orderIndex = 1
        ),
        AdhkarItemEntity(
            id = "sl2",
            category = "sleep",
            title = "آخر آيتين من سورة البقرة",
            text = "آمَنَ الرَّسُولُ بِمَا أُنزِلَ إِلَيْهِ مِن رَّبِّهِ وَالْمُؤْمِنُونَ ۚ كُلٌّ آمَنَ بِاللَّهِ وَمَلَائِكَتِهِ وَكُتُبِهِ وَرُسُلِهِ لَا نُفَرِّقُ بَيْنَ أَحَدٍ مِّن رُّسُلِهِ ۚ وَقَالُوا سَمِعْنَا وَأَطَعْنَا ۖ غُفْرَانَكَ رَبَّنَا وَإِلَيْكَ الْمَصِيرُ ۞ لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا ۚ لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْ ۗ رَبَّنَا لَا تُؤَاخِذْنَا إِن نَّسِينَا أَوْ أَخْطَأْنَا ۚ رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَا إِصْرًا كَمَا حَمَلْتَهُ عَلَى الَّذِينَ مِن قَبْلِنَا ۚ رَبَّنَا وَلَا تُحَمِّلْنَا مَا لَا طَاقَةَ لَنَا بِهِ ۖ وَاعْفُ عَنَّا وَاغْفِرْ لَنَا وَارْحَمْنَا ۚ أَنتَ مَوْلَانَا فَانصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ.",
            repeat = 1,
            reference = "من قرأ بهما في ليلة كفتاه",
            source = "رواه البخاري ومسلم",
            orderIndex = 2
        ),

        // Wake Adhkar
        AdhkarItemEntity(
            id = "wk1",
            category = "wake",
            title = "الحمد لله الذي أحيانا",
            text = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ.",
            repeat = 1,
            reference = "دعاء الاستيقاظ",
            source = "رواه البخاري ومسلم",
            orderIndex = 1
        ),

        // Misc Adhkar
        AdhkarItemEntity(
            id = "ms1",
            category = "misc",
            title = "دعاء دخول المسجد",
            text = "بِسْمِ اللَّهِ، وَالصَّلَاةُ وَالسَّلَامُ عَلَى رَسُولِ اللَّهِ، اللَّهُمَّ افْتَحْ لِي أَبْوَابَ رَحْمَتِكَ.",
            repeat = 1,
            reference = "يقال عند الدخول بالقدم اليمنى",
            source = "رواه مسلم وأبو داود",
            orderIndex = 1
        ),
        AdhkarItemEntity(
            id = "ms2",
            category = "misc",
            title = "دعاء الخروج من المسجد",
            text = "بِسْمِ اللَّهِ، وَالصَّلَاةُ وَالسَّلَامُ عَلَى رَسُولِ اللَّهِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ مِنْ فَضْلِكَ.",
            repeat = 1,
            reference = "يقال عند الخروج بالقدم اليسرى",
            source = "رواه مسلم",
            orderIndex = 2
        ),
        AdhkarItemEntity(
            id = "ms3",
            category = "misc",
            title = "دعاء الركوب والسفر",
            text = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ.",
            repeat = 1,
            reference = "سورة الزخرف",
            source = "رواه مسلم",
            orderIndex = 3
        )
    )

    val DEFAULT_WIRD_GOALS = listOf(
        DailyWirdGoalEntity("g_quran", "قراءة ورد من القرآن", 1, "manual", 1),
        DailyWirdGoalEntity("g_morning", "أذكار الصباح", 1, "adhkar", 2),
        DailyWirdGoalEntity("g_evening", "أذكار المساء", 1, "adhkar", 3),
        DailyWirdGoalEntity("g_tasbeeh", "مائة تسبيحة", 100, "tasbeeh", 4)
    )
}
