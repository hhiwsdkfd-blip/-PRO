package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DefaultIslamicData
import com.example.model.AdhkarItemEntity
import com.example.model.AyahEntity
import com.example.model.SurahEntity
import com.example.model.TadabburNoteEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.RafiqViewModel
import com.example.utils.ArabicUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: RafiqViewModel,
    onNavigateBack: () -> Unit,
    onOpenSurah: (surahNumber: Int, ayah: Int) -> Unit,
    onOpenAdhkarCategory: (categoryKey: String) -> Unit,
    onOpenTadabbur: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val surahs by viewModel.surahs.collectAsStateWithLifecycle()
    val notes by viewModel.tadabburNotes.collectAsStateWithLifecycle()

    val normQuery = remember(query) {
        ArabicUtils.normalizeArabic(query.trim())
    }

    // Matching Surahs
    val matchedSurahs = remember(normQuery, surahs) {
        if (normQuery.length < 2) emptyList()
        else {
            val list = surahs.ifEmpty { DefaultIslamicData.getInitialSurahs() }
            list.filter { ArabicUtils.normalizeArabic(it.name).contains(normQuery) }
        }
    }

    // Matching Adhkar
    val matchedAdhkar = remember(normQuery) {
        if (normQuery.length < 2) emptyList()
        else {
            DefaultIslamicData.INITIAL_ADHKAR.filter {
                ArabicUtils.normalizeArabic(it.text).contains(normQuery) ||
                        ArabicUtils.normalizeArabic(it.title).contains(normQuery)
            }
        }
    }

    // Matching Tadabbur notes
    val matchedNotes = remember(normQuery, notes) {
        if (normQuery.length < 2) emptyList()
        else {
            notes.filter { ArabicUtils.normalizeArabic(it.text).contains(normQuery) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "البحث الشامل",
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("global_search_input"),
                placeholder = {
                    Text("ابحث في السور، الآيات، الأذكار، والملاحظات...", fontFamily = AmiriFontFamily)
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            if (normQuery.length < 2) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔍", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "اكتب حرفين على الأقل للبدء",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "البحث يعمل دون إنترنت ويتجاهل علامات التشكيل والهمزات.",
                            fontFamily = AmiriFontFamily,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                val totalResults = matchedSurahs.size + matchedAdhkar.size + matchedNotes.size

                if (totalResults == 0) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لم يتم العثور على نتائج مطابقة لـ «$query»",
                            fontFamily = AmiriFontFamily,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 96.dp)
                    ) {
                        // Matching Surahs Section
                        if (matchedSurahs.isNotEmpty()) {
                            item {
                                Text(
                                    text = "السور القرانية (${ArabicUtils.toArabicDigits(matchedSurahs.size)}):",
                                    fontFamily = AmiriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            items(matchedSurahs) { surah ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onOpenSurah(surah.number, 1) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "سورة ${surah.name}",
                                            fontFamily = AmiriFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "${ArabicUtils.toArabicDigits(surah.ayahCount)} آية",
                                            fontFamily = AmiriFontFamily,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // Matching Adhkar Section
                        if (matchedAdhkar.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "الأذكار والأدعية (${ArabicUtils.toArabicDigits(matchedAdhkar.size)}):",
                                    fontFamily = AmiriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            items(matchedAdhkar) { dhikr ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onOpenAdhkarCategory(dhikr.category) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = dhikr.text,
                                            fontFamily = AmiriFontFamily,
                                            fontSize = 16.sp,
                                            maxLines = 3
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        val catLabel = ADHKAR_CATEGORIES.find { it.key == dhikr.category }?.title ?: ""
                                        Text(
                                            text = "$catLabel • تكرار: ${ArabicUtils.toArabicDigits(dhikr.repeat)}",
                                            fontFamily = AmiriFontFamily,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // Matching Notes Section
                        if (matchedNotes.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "ملاحظات التدبر (${ArabicUtils.toArabicDigits(matchedNotes.size)}):",
                                    fontFamily = AmiriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            items(matchedNotes) { note ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onOpenTadabbur() },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = note.text,
                                            fontFamily = AmiriFontFamily,
                                            fontSize = 15.sp,
                                            maxLines = 2
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
}
