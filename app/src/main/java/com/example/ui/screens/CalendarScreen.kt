package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.CalendarNoteEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.RafiqViewModel
import com.example.utils.ArabicUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: RafiqViewModel,
    onNavigateBack: () -> Unit
) {
    val hijriOffset by viewModel.hijriOffset.collectAsStateWithLifecycle()
    val notes by viewModel.calendarNotes.collectAsStateWithLifecycle()

    val currentNow = remember { Date() }
    val initialHijri = remember(hijriOffset) { ArabicUtils.getHijriDate(currentNow, hijriOffset) }

    var selectedHijriYear by remember { mutableIntStateOf(initialHijri.year) }
    var selectedHijriMonth by remember { mutableIntStateOf(initialHijri.month) }
    var selectedIsoDate by remember { mutableStateOf(ArabicUtils.getTodayIso()) }
    var newNoteText by remember { mutableStateOf("") }

    val daysInHijriMonth = 30 // standard lunar month approximation

    val notesForSelectedDay = remember(notes, selectedIsoDate) {
        notes.filter { it.date == selectedIsoDate }
    }

    // Navigation month forward/backward
    val changeMonth: (Int) -> Unit = { delta ->
        var m = selectedHijriMonth + delta
        var y = selectedHijriYear
        if (m < 1) {
            m = 12
            y -= 1
        } else if (m > 12) {
            m = 1
            y += 1
        }
        selectedHijriMonth = m
        selectedHijriYear = y
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Month Navigation Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { changeMonth(1) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "الشهر التالي")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${ArabicUtils.HIJRI_MONTHS.getOrElse(selectedHijriMonth - 1) { "" }} ${ArabicUtils.toArabicDigits(selectedHijriYear)} هـ",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = { changeMonth(-1) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "الشهر السابق")
                    }
                }
            }
        }

        // Days Grid
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Weekday headers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ArabicUtils.WEEK_DAYS.forEach { dayName ->
                            Text(
                                text = dayName.take(3),
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 30 days grid (6 rows of 5 or 7 columns)
                    val daysList = (1..30).toList()
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        daysList.chunked(7).forEach { week ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                week.forEach { dayNum ->
                                    val isCurrentDay = dayNum == initialHijri.day &&
                                            selectedHijriMonth == initialHijri.month &&
                                            selectedHijriYear == initialHijri.year

                                    val occ = ArabicUtils.getIslamicOccasion(selectedHijriMonth, dayNum)

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = when {
                                            isCurrentDay -> MaterialTheme.colorScheme.primary
                                            occ != null -> MaterialTheme.colorScheme.secondaryContainer
                                            else -> Color.Transparent
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .padding(2.dp)
                                            .clickable {
                                                // Estimate Gregorian date for this Hijri day
                                                val diffDays = (dayNum - initialHijri.day) +
                                                        (selectedHijriMonth - initialHijri.month) * 30
                                                val c = Calendar.getInstance()
                                                c.time = currentNow
                                                c.add(Calendar.DAY_OF_YEAR, diffDays)
                                                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                                                selectedIsoDate = sdf.format(c.time)
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = ArabicUtils.toArabicDigits(dayNum),
                                                fontFamily = AmiriFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = when {
                                                    isCurrentDay -> Color.White
                                                    occ != null -> MaterialTheme.colorScheme.onSecondaryContainer
                                                    else -> MaterialTheme.colorScheme.onSurface
                                                }
                                            )
                                        }
                                    }
                                }
                                // Fill remaining spaces in last row
                                if (week.size < 7) {
                                    for (empty in 0 until (7 - week.size)) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected Day Details & Notes
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
                        text = "تفاصيل وملاحظات التاريخ:",
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تاريخ اليوم المحدد: $selectedIsoDate",
                        fontFamily = AmiriFontFamily,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Existing Notes
                    if (notesForSelectedDay.isEmpty()) {
                        Text(
                            text = "لا توجد ملاحظات مسجلة لهذا التاريخ.",
                            fontFamily = AmiriFontFamily,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        notesForSelectedDay.forEach { note ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "• ${note.text}",
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 15.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { viewModel.deleteCalendarNote(note.id) }) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "حذف الملاحظة",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Add note row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newNoteText,
                            onValueChange = { newNoteText = it },
                            placeholder = { Text("أضف تذكيراً أو ملاحظة...", fontFamily = AmiriFontFamily) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newNoteText.isNotBlank()) {
                                    viewModel.addCalendarNote(selectedIsoDate, newNoteText.trim())
                                    newNoteText = ""
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("إضافة", fontFamily = AmiriFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Hijri Offset Adjuster Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "تعديل تقويم أم القرى (مزامنة الرؤية الشرعية):",
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedIconButton(
                            onClick = { viewModel.setHijriOffset(-1) },
                            enabled = hijriOffset > -2
                        ) {
                            Text("-", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        val sign = if (hijriOffset > 0) "+" else ""
                        Text(
                            text = "$sign${ArabicUtils.toArabicDigits(hijriOffset)} يوم",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        OutlinedIconButton(
                            onClick = { viewModel.setHijriOffset(1) },
                            enabled = hijriOffset < 2
                        ) {
                            Text("+", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "الحساب الفلكي تقريبي، ويمكنك تقديم أو تأخير التاريخ بيوم أو يومين لمطابقة إعلان دار الإفتاء ورؤية الهلال المحلية.",
                        fontFamily = AmiriFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
