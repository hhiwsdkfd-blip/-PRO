package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DefaultIslamicData
import com.example.ui.theme.*
import com.example.ui.viewmodel.RafiqViewModel
import com.example.utils.ArabicUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TadabburScreen(
    initialSurah: Int? = null,
    initialAyah: Int? = null,
    viewModel: RafiqViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val notes by viewModel.tadabburNotes.collectAsStateWithLifecycle()
    val surahs by viewModel.surahs.collectAsStateWithLifecycle()

    var noteText by remember { mutableStateOf("") }
    var selectedSurahNumber by remember { mutableStateOf(initialSurah) }
    var ayahInput by remember { mutableStateOf(initialAyah?.toString() ?: "") }
    var editingNoteId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showSurahPicker by remember { mutableStateOf(false) }

    val filteredNotes = remember(notes, searchQuery) {
        if (searchQuery.isBlank()) notes
        else {
            val q = ArabicUtils.normalizeArabic(searchQuery)
            notes.filter { ArabicUtils.normalizeArabic(it.text).contains(q) }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Disclaimer Note
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "💡 ملاحظاتك هنا شخصية لتسجيل خواطرك وتدبرك، وليست تفسيراً شرعياً معتمداً.",
                    fontFamily = AmiriFontFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Add / Edit Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (editingNoteId != null) "تعديل التدبر" else "إضافة تدبر أو خاطرة جديدة",
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = { Text("اكتب تدبرك أو ما أثّر في قلبك من معانٍ...", fontFamily = AmiriFontFamily) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Surah & Ayah link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { showSurahPicker = true },
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            val surahTitle = if (selectedSurahNumber != null) {
                                DefaultIslamicData.ALL_SURAHS.find { it.number == selectedSurahNumber }?.name
                                    ?: "سورة $selectedSurahNumber"
                            } else {
                                "بدون سورة"
                            }
                            Text(surahTitle, fontFamily = AmiriFontFamily)
                        }

                        DropdownMenu(
                            expanded = showSurahPicker,
                            onDismissRequest = { showSurahPicker = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("بدون سورة", fontFamily = AmiriFontFamily) },
                                onClick = {
                                    selectedSurahNumber = null
                                    showSurahPicker = false
                                }
                            )
                            DefaultIslamicData.ALL_SURAHS.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text("${ArabicUtils.toArabicDigits(s.number)}. ${s.name}", fontFamily = AmiriFontFamily) },
                                    onClick = {
                                        selectedSurahNumber = s.number
                                        showSurahPicker = false
                                    }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = ayahInput,
                            onValueChange = { ayahInput = it.filter { ch -> ch.isDigit() } },
                            placeholder = { Text("الآية", fontFamily = AmiriFontFamily) },
                            singleLine = true,
                            modifier = Modifier.weight(0.8f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                if (noteText.isNotBlank()) {
                                    viewModel.saveTadabburNote(
                                        id = editingNoteId,
                                        text = noteText.trim(),
                                        surah = selectedSurahNumber,
                                        ayah = ayahInput.toIntOrNull()
                                    )
                                    noteText = ""
                                    selectedSurahNumber = null
                                    ayahInput = ""
                                    editingNoteId = null
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (editingNoteId != null) "حفظ التعديل" else "إضافة إلى دفتري",
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (editingNoteId != null) {
                            OutlinedButton(
                                onClick = {
                                    editingNoteId = null
                                    noteText = ""
                                    selectedSurahNumber = null
                                    ayahInput = ""
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("إلغاء", fontFamily = AmiriFontFamily)
                            }
                        }
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("ابحث في ملاحظات التدبر...", fontFamily = AmiriFontFamily) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
        }

        // Notes List
        if (filteredNotes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد تدبرات مسجلة بعد",
                        fontFamily = AmiriFontFamily,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredNotes, key = { it.id }) { note ->
                val sName = if (note.surahNumber != null) {
                    DefaultIslamicData.ALL_SURAHS.find { it.number == note.surahNumber }?.name
                } else null

                val dateStr = remember(note.timestamp) {
                    val sdf = SimpleDateFormat("d MMMM yyyy", Locale("ar"))
                    sdf.format(Date(note.timestamp))
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = note.text,
                            fontFamily = AmiriFontFamily,
                            fontSize = 17.sp,
                            lineHeight = 26.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                if (sName != null) {
                                    val ayahPart = if (note.ayahNumber != null) " — آية ${ArabicUtils.toArabicDigits(note.ayahNumber)}" else ""
                                    Text(
                                        text = "سورة $sName$ayahPart",
                                        fontFamily = AmiriFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = dateStr,
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Share
                            IconButton(onClick = {
                                val sText = "${note.text}\n" + (if (sName != null) "[سورة $sName]" else "")
                                val intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, sText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(intent, "مشاركة التدبر"))
                            }) {
                                Icon(Icons.Default.Share, contentDescription = "مشاركة", modifier = Modifier.size(20.dp))
                            }

                            // Edit
                            IconButton(onClick = {
                                editingNoteId = note.id
                                noteText = note.text
                                selectedSurahNumber = note.surahNumber
                                ayahInput = note.ayahNumber?.toString() ?: ""
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "تعديل", modifier = Modifier.size(20.dp))
                            }

                            // Delete
                            IconButton(onClick = { viewModel.deleteTadabburNote(note.id) }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "حذف",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
