package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DefaultIslamicData
import com.example.model.BookmarkEntity
import com.example.model.SurahEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.RafiqViewModel
import com.example.utils.ArabicUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    viewModel: RafiqViewModel,
    onOpenSurah: (surahNumber: Int, ayah: Int) -> Unit
) {
    val surahs by viewModel.surahs.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val isDownloading by viewModel.isDownloadingQuran.collectAsStateWithLifecycle()
    val downloadProgress by viewModel.downloadQuranProgress.collectAsStateWithLifecycle()
    val statusMsg by viewModel.downloadStatusMsg.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("السور", "الأجزاء", "المفضلة", "العلامات")

    var searchQuery by remember { mutableStateOf("") }

    val filteredSurahs = remember(surahs, searchQuery) {
        val q = ArabicUtils.normalizeArabic(searchQuery)
        if (q.isEmpty()) {
            surahs.ifEmpty { DefaultIslamicData.getInitialSurahs() }
        } else {
            (surahs.ifEmpty { DefaultIslamicData.getInitialSurahs() }).filter {
                ArabicUtils.normalizeArabic(it.name).contains(q) ||
                        it.englishName.contains(searchQuery, ignoreCase = true) ||
                        it.number.toString() == searchQuery
            }
        }
    }

    val favoriteSurahNumbers = remember(favorites) {
        favorites.filter { it.type == "surah" }.mapNotNull { it.itemId.toIntOrNull() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Tab Selector
        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier.testTag("quran_tab_$index"),
                    text = {
                        Text(
                            text = title,
                            fontFamily = AmiriFontFamily,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    }
                )
            }
        }

        // Download Notice Banner if downloading
        AnimatedVisibility(visible = isDownloading || statusMsg != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isDownloading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "جارٍ تنزيل المصحف كاملاً: $downloadProgress",
                            fontFamily = AmiriFontFamily,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else if (statusMsg != null) {
                        Text(
                            text = statusMsg!!,
                            fontFamily = AmiriFontFamily,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        IconButton(onClick = { viewModel.clearStatusMsg() }) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق")
                        }
                    }
                }
            }
        }

        // Tab 0: Surahs
        if (selectedTab == 0) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .testTag("quran_search_field"),
                placeholder = {
                    Text("ابحث باسم السورة أو رقمها...", fontFamily = AmiriFontFamily)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "بحث")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "مسح")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                // One-click full Quran download button if not downloaded
                item {
                    OutlinedButton(
                        onClick = { viewModel.downloadFullQuran() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quran_download_full_btn"),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isDownloading
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = "تحميل")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isDownloading) "جارٍ التحميل..." else "تحميل المصحف كاملًا ليعمل بدون إنترنت",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                items(filteredSurahs, key = { it.number }) { surah ->
                    SurahListItem(
                        surah = surah,
                        isFavorite = favoriteSurahNumbers.contains(surah.number),
                        onToggleFavorite = {
                            viewModel.toggleFavorite("surah", surah.number.toString(), favoriteSurahNumbers.contains(surah.number))
                        },
                        onClick = { onOpenSurah(surah.number, 1) }
                    )
                }
            }
        }

        // Tab 1: Juz (30 parts)
        if (selectedTab == 1) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                val juzList = List(30) { it + 1 }
                items(juzList) { juzNum ->
                    val startingSurah = DefaultIslamicData.ALL_SURAHS.find { it.juzStart == juzNum }
                        ?: DefaultIslamicData.ALL_SURAHS.first()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenSurah(startingSurah.number, 1) },
                        shape = RoundedCornerShape(14.dp),
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
                                color = IslamicEmerald.copy(alpha = 0.12f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = ArabicUtils.toArabicDigits(juzNum),
                                        fontFamily = AmiriFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = IslamicEmerald
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "الجزء ${ArabicUtils.toArabicDigits(juzNum)}",
                                    fontFamily = AmiriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "يبدأ بسورة ${startingSurah.name}",
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
                    }
                }
            }
        }

        // Tab 2: Favorites
        if (selectedTab == 2) {
            val favSurahs = surahs.filter { favoriteSurahNumbers.contains(it.number) }
            if (favSurahs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "⭐", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا توجد سور في المفضلة بعد",
                            fontFamily = AmiriFontFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "اضغط على رمز النجمة بجانب أي سورة لإضافتها للمفضلة",
                            fontFamily = AmiriFontFamily,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(favSurahs, key = { it.number }) { surah ->
                        SurahListItem(
                            surah = surah,
                            isFavorite = true,
                            onToggleFavorite = {
                                viewModel.toggleFavorite("surah", surah.number.toString(), true)
                            },
                            onClick = { onOpenSurah(surah.number, 1) }
                        )
                    }
                }
            }
        }

        // Tab 3: Bookmarks
        if (selectedTab == 3) {
            if (bookmarks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔖", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا توجد علامات مرجعية محفوظة",
                            fontFamily = AmiriFontFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "أثناء قراءة أي سورة، اضغط على الآية ثم اختر علامة مرجعية",
                            fontFamily = AmiriFontFamily,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(bookmarks, key = { it.id }) { bm ->
                        val sName = DefaultIslamicData.ALL_SURAHS.find { it.number == bm.surahNumber }?.name ?: ""
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenSurah(bm.surahNumber, bm.ayahNumber) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "علامة",
                                    tint = IslamicGold
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "سورة $sName",
                                        fontFamily = AmiriFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "الآية ${ArabicUtils.toArabicDigits(bm.ayahNumber)}",
                                        fontFamily = AmiriFontFamily,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { viewModel.toggleBookmark(bm.surahNumber, bm.ayahNumber, true) }) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "حذف",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SurahListItem(
    surah: SurahEntity,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("surah_item_${surah.number}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Surah Number
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = ArabicUtils.toArabicDigits(surah.number),
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Surah Name & Metadata
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = surah.name,
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = surah.revelationType,
                            fontFamily = AmiriFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "${ArabicUtils.toArabicDigits(surah.ayahCount)} آية • الجزء ${ArabicUtils.toArabicDigits(surah.juzStart)}",
                    fontFamily = AmiriFontFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Favorite button
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.testTag("surah_fav_btn_${surah.number}")
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "المفضلة",
                    tint = if (isFavorite) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
