package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DefaultIslamicData
import com.example.ui.theme.*
import com.example.ui.viewmodel.RafiqViewModel
import com.example.utils.ArabicUtils
import java.util.Date

@Composable
fun HomeScreen(
    viewModel: RafiqViewModel,
    onNavigateToQuran: (surah: Int?, ayah: Int?) -> Unit,
    onNavigateToAdhkarCategory: (categoryKey: String) -> Unit,
    onNavigateToTasbeeh: () -> Unit,
    onNavigateToWird: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToTadabbur: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val lastSurah by viewModel.lastReadSurah.collectAsStateWithLifecycle()
    val lastAyah by viewModel.lastReadAyah.collectAsStateWithLifecycle()
    val surahs by viewModel.surahs.collectAsStateWithLifecycle()
    val tasbeehDhikr by viewModel.currentTasbeehDhikr.collectAsStateWithLifecycle()
    val tasbeehCount by viewModel.tasbeehCount.collectAsStateWithLifecycle()
    val tasbeehTarget by viewModel.tasbeehTarget.collectAsStateWithLifecycle()
    val wirdGoals by viewModel.wirdGoals.collectAsStateWithLifecycle()
    val wirdLogs by viewModel.todayWirdLogs.collectAsStateWithLifecycle()
    val hijriOffset by viewModel.hijriOffset.collectAsStateWithLifecycle()
    val todayProgress by viewModel.todayAdhkarProgress.collectAsStateWithLifecycle()

    val hijriDate = remember(hijriOffset) {
        ArabicUtils.getHijriDate(Date(), hijriOffset)
    }
    val occasion = remember(hijriDate) {
        ArabicUtils.getIslamicOccasion(hijriDate.month, hijriDate.day)
    }

    val lastSurahName = remember(lastSurah, surahs) {
        surahs.find { it.number == lastSurah }?.name
            ?: DefaultIslamicData.ALL_SURAHS.find { it.number == lastSurah }?.name
    }

    val morningDone = todayProgress.count { it.dhikrId.startsWith("m") && it.currentCount > 0 }
    val eveningDone = todayProgress.count { it.dhikrId.startsWith("e") && it.currentCount > 0 }

    val completedGoals = wirdGoals.count { goal ->
        val log = wirdLogs.find { it.goalId == goal.id }
        (log?.progress ?: 0) >= goal.target
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_hero_card"),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "☪",
                                    fontSize = 26.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "رفيق المسلم",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = hijriDate.formatted,
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = ArabicUtils.formatGregorian(Date()),
                            fontFamily = AmiriFontFamily,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (occasion != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "✨ $occasion",
                                    fontFamily = AmiriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Last Read Quran Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_quran_card")
                    .clickable {
                        onNavigateToQuran(lastSurah ?: 1, lastAyah ?: 1)
                    },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = IslamicGold.copy(alpha = 0.15f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = "المصحف",
                                tint = IslamicGoldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "متابعة القرآن الكريم",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (lastSurah != null && lastAyah != null && lastSurahName != null) {
                            Text(
                                text = "آخر موضع: سورة $lastSurahName — آية ${ArabicUtils.toArabicDigits(lastAyah!!)}",
                                fontFamily = AmiriFontFamily,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = "اضغط للبدء في قراءة المصحف الشريف",
                                fontFamily = AmiriFontFamily,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "فتح",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Quick Adhkar Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onNavigateToAdhkarCategory("morning") },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .testTag("home_morning_adhkar_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🌅 أذكار الصباح",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (morningDone > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = ArabicUtils.toArabicDigits(morningDone),
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { onNavigateToAdhkarCategory("evening") },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .testTag("home_evening_adhkar_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🌙 أذكار المساء",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (eveningDone > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = ArabicUtils.toArabicDigits(eveningDone),
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Tasbeeh Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_tasbeeh_card")
                    .clickable { onNavigateToTasbeeh() },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "المسبحة",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "المسبحة الإلكترونية",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$tasbeehDhikr: ${ArabicUtils.toArabicDigits(tasbeehCount)} / ${ArabicUtils.toArabicDigits(tasbeehTarget)}",
                            fontFamily = AmiriFontFamily,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    FilledTonalButton(
                        onClick = { viewModel.tapTasbeeh() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("home_quick_tasbeeh_btn")
                    ) {
                        Text(
                            text = "سبّح",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Daily Wird Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_wird_card")
                    .clickable { onNavigateToWird() },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = IslamicSuccessGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "الورد",
                                    tint = IslamicSuccessGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "وردي اليومي",
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val text = if (wirdGoals.isNotEmpty()) {
                                "أكملت ${ArabicUtils.toArabicDigits(completedGoals)} من ${ArabicUtils.toArabicDigits(wirdGoals.size)} أهداف"
                            } else {
                                "لم تضف أهدافًا بعد، اضغط للإضافة"
                            }
                            Text(
                                text = text,
                                fontFamily = AmiriFontFamily,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "فتح",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (wirdGoals.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        val progressRatio = if (wirdGoals.isNotEmpty()) completedGoals.toFloat() / wirdGoals.size else 0f
                        LinearProgressIndicator(
                            progress = { progressRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = IslamicSuccessGreen,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }

        // Quick Navigation Grid
        item {
            Text(
                text = "الخدمات السريعة",
                fontFamily = AmiriFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickTile(
                    title = "التقويم الهجري",
                    icon = "📅",
                    modifier = Modifier.weight(1f),
                    testTag = "home_tile_calendar",
                    onClick = onNavigateToCalendar
                )
                QuickTile(
                    title = "البحث الشامل",
                    icon = "🔎",
                    modifier = Modifier.weight(1f),
                    testTag = "home_tile_search",
                    onClick = onNavigateToSearch
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickTile(
                    title = "وردي اليومي",
                    icon = "✅",
                    modifier = Modifier.weight(1f),
                    testTag = "home_tile_wird",
                    onClick = onNavigateToWird
                )
                QuickTile(
                    title = "دفتر التدبر",
                    icon = "📝",
                    modifier = Modifier.weight(1f),
                    testTag = "home_tile_tadabbur",
                    onClick = onNavigateToTadabbur
                )
            }
        }

        // Settings Button
        item {
            OutlinedButton(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("home_settings_btn"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "الإعدادات"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "الإعدادات والنسخ الاحتياطي",
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun QuickTile(
    title: String,
    icon: String,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontFamily = AmiriFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
