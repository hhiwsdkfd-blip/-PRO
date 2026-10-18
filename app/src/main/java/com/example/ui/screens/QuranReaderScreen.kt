package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DefaultIslamicData
import com.example.model.AyahEntity
import com.example.model.SurahEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.RafiqViewModel
import com.example.utils.ArabicUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
    surahNumber: Int,
    targetAyah: Int = 1,
    viewModel: RafiqViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToTadabburWithAyah: (surah: Int, ayah: Int) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var currentSurahNumber by remember { mutableIntStateOf(surahNumber) }
    val surahs by viewModel.surahs.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val fontSize by viewModel.quranFontSize.collectAsStateWithLifecycle()

    var isDistractionFree by remember { mutableStateOf(false) }
    var selectedAyahForSheet by remember { mutableStateOf<AyahEntity?>(null) }
    var showJumpDialog by remember { mutableStateOf(false) }
    var showSurahDropdown by remember { mutableStateOf(false) }

    val currentSurah = remember(currentSurahNumber, surahs) {
        surahs.find { it.number == currentSurahNumber }
            ?: DefaultIslamicData.getInitialSurahs().find { it.number == currentSurahNumber }
    }

    val ayahsFlow = remember(currentSurahNumber) {
        viewModel.getAyahsForSurah(currentSurahNumber)
    }
    val ayahs by ayahsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    val isCurrentSurahFavorite = remember(favorites, currentSurahNumber) {
        favorites.any { it.type == "surah" && it.itemId == currentSurahNumber.toString() }
    }

    // Scroll to target ayah on first load
    LaunchedEffect(currentSurahNumber, targetAyah, ayahs) {
        if (ayahs.isNotEmpty() && targetAyah > 1) {
            val idx = ayahs.indexOfFirst { it.numberInSurah == targetAyah }
            if (idx >= 0) {
                listState.scrollToItem(idx)
            }
        }
    }

    // Update last read when user scrolls
    LaunchedEffect(listState.firstVisibleItemIndex, ayahs) {
        if (ayahs.isNotEmpty() && listState.firstVisibleItemIndex in ayahs.indices) {
            val visibleAyah = ayahs[listState.firstVisibleItemIndex]
            viewModel.setLastRead(currentSurahNumber, visibleAyah.numberInSurah)
        }
    }

    Scaffold(
        topBar = {
            if (!isDistractionFree) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showSurahDropdown = true }
                        ) {
                            Text(
                                text = "سورة ${currentSurah?.name ?: ""}",
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "اختيار سورة"
                            )
                        }

                        DropdownMenu(
                            expanded = showSurahDropdown,
                            onDismissRequest = { showSurahDropdown = false }
                        ) {
                            (surahs.ifEmpty { DefaultIslamicData.getInitialSurahs() }).forEach { s ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${ArabicUtils.toArabicDigits(s.number)}. ${s.name}",
                                            fontFamily = AmiriFontFamily
                                        )
                                    },
                                    onClick = {
                                        currentSurahNumber = s.number
                                        showSurahDropdown = false
                                        coroutineScope.launch { listState.scrollToItem(0) }
                                    }
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                        }
                    },
                    actions = {
                        // Font Size down
                        IconButton(onClick = { viewModel.updateFontSize(fontSize - 2) }) {
                            Text("أ-", fontFamily = AmiriFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        // Font Size up
                        IconButton(onClick = { viewModel.updateFontSize(fontSize + 2) }) {
                            Text("أ+", fontFamily = AmiriFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        // Jump to Ayah
                        IconButton(onClick = { showJumpDialog = true }) {
                            Icon(Icons.Default.FormatListNumbered, contentDescription = "انتقال لآية")
                        }
                        // Favorite
                        IconButton(onClick = {
                            viewModel.toggleFavorite("surah", currentSurahNumber.toString(), isCurrentSurahFavorite)
                        }) {
                            Icon(
                                imageVector = if (isCurrentSurahFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "المفضلة",
                                tint = if (isCurrentSurahFavorite) IslamicGold else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        // Fullscreen
                        IconButton(onClick = { isDistractionFree = true }) {
                            Icon(Icons.Default.Fullscreen, contentDescription = "وضع القراءة الكامل")
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (ayahs.isEmpty()) {
                // If this surah ayahs aren't in database yet
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(text = "📖", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "نص سورة ${currentSurah?.name ?: ""} غير متوفر محليًا بعد",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "يمكنك تحميل المصحف الشريف كاملًا (١١٤ سورة) بضغطة زر ليعمل دائمًا بدون إنترنت.",
                            fontFamily = AmiriFontFamily,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.downloadFullQuran() },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تحميل المصحف الآن", fontFamily = AmiriFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
                ) {
                    // Surah Header
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "سُورَةُ ${currentSurah?.name ?: ""}",
                                    fontFamily = AmiriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${currentSurah?.revelationType ?: ""} • ${ArabicUtils.toArabicDigits(currentSurah?.ayahCount ?: 0)} آية • الجزء ${ArabicUtils.toArabicDigits(currentSurah?.juzStart ?: 1)}",
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (currentSurahNumber != 9 && currentSurahNumber != 1) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                        fontFamily = AmiriFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (fontSize - 2).coerceAtLeast(18).sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Ayahs List
                    items(ayahs, key = { it.id }) { ayah ->
                        val isBookmarked = bookmarks.any { it.surahNumber == currentSurahNumber && it.ayahNumber == ayah.numberInSurah }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedAyahForSheet = ayah },
                            color = if (isBookmarked) IslamicGold.copy(alpha = 0.12f) else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                if (isBookmarked) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = "علامة",
                                        tint = IslamicGold,
                                        modifier = Modifier.padding(top = 8.dp, end = 6.dp)
                                    )
                                }

                                Text(
                                    text = "${ayah.text} ﴿${ArabicUtils.toArabicDigits(ayah.numberInSurah)}﴾",
                                    fontFamily = AmiriFontFamily,
                                    fontSize = fontSize.sp,
                                    lineHeight = (fontSize * 1.8).sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            // Floating exit button when in Distraction-Free mode
            if (isDistractionFree) {
                FloatingActionButton(
                    onClick = { isDistractionFree = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(44.dp),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Icon(Icons.Default.FullscreenExit, contentDescription = "إنهاء وضع ملء الشاشة")
                }
            }
        }
    }

    // Jump to Ayah Dialog
    if (showJumpDialog) {
        var ayahInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showJumpDialog = false },
            title = {
                Text("الانتقال إلى آية", fontFamily = AmiriFontFamily, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        "أدخل رقم الآية (من ١ إلى ${ArabicUtils.toArabicDigits(currentSurah?.ayahCount ?: 0)}):",
                        fontFamily = AmiriFontFamily
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = ayahInput,
                        onValueChange = { ayahInput = it.filter { ch -> ch.isDigit() } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = ayahInput.toIntOrNull()
                        if (num != null && num in 1..(currentSurah?.ayahCount ?: 0)) {
                            val idx = ayahs.indexOfFirst { it.numberInSurah == num }
                            if (idx >= 0) {
                                coroutineScope.launch { listState.scrollToItem(idx + 1) }
                            }
                        }
                        showJumpDialog = false
                    }
                ) {
                    Text("انتقال", fontFamily = AmiriFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showJumpDialog = false }) {
                    Text("إلغاء", fontFamily = AmiriFontFamily)
                }
            }
        )
    }

    // Ayah Details Bottom Sheet
    selectedAyahForSheet?.let { ayah ->
        val isBm = bookmarks.any { it.surahNumber == currentSurahNumber && it.ayahNumber == ayah.numberInSurah }
        ModalBottomSheet(
            onDismissRequest = { selectedAyahForSheet = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "سورة ${currentSurah?.name ?: ""} — الآية ${ArabicUtils.toArabicDigits(ayah.numberInSurah)}",
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = ayah.text,
                    fontFamily = AmiriFontFamily,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.toggleBookmark(currentSurahNumber, ayah.numberInSurah, isBm)
                            selectedAyahForSheet = null
                        },
                        modifier = Modifier.weight(1f),
                        colors = if (isBm) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        else ButtonDefaults.buttonColors(containerColor = IslamicGold)
                    ) {
                        Icon(
                            imageVector = if (isBm) Icons.Default.BookmarkRemove else Icons.Default.BookmarkAdd,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBm) "إزالة العلامة" else "حفظ كعلامة", fontFamily = AmiriFontFamily)
                    }

                    FilledTonalButton(
                        onClick = {
                            val shareText = "${ayah.text}\n[سورة ${currentSurah?.name}: آية ${ayah.numberInSurah}]"
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "مشاركة الآية"))
                            selectedAyahForSheet = null
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مشاركة", fontFamily = AmiriFontFamily)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        onNavigateToTadabburWithAyah(currentSurahNumber, ayah.numberInSurah)
                        selectedAyahForSheet = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Create, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تدبر هذه الآية في دفتري", fontFamily = AmiriFontFamily)
                }
            }
        }
    }
}
