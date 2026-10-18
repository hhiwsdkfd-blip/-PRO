package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.RafiqViewModel
import com.example.utils.ArabicUtils

private val TASBEEH_PRESETS = listOf(
    "سُبْحَانَ اللَّهِ",
    "الْحَمْدُ لِلَّهِ",
    "اللَّهُ أَكْبَرُ",
    "لَا إِلَهَ إِلَّا اللَّهُ",
    "أَسْتَغْفِرُ اللَّهَ",
    "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
    "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ"
)

@Composable
fun TasbeehScreen(viewModel: RafiqViewModel) {
    val currentDhikr by viewModel.currentTasbeehDhikr.collectAsStateWithLifecycle()
    val count by viewModel.tasbeehCount.collectAsStateWithLifecycle()
    val target by viewModel.tasbeehTarget.collectAsStateWithLifecycle()
    val logs by viewModel.tasbeehLogs.collectAsStateWithLifecycle()

    var customDhikrInput by remember { mutableStateOf("") }
    var customTargetInput by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }

    val todayTotal = remember(logs, count) {
        val today = ArabicUtils.getTodayIso()
        val historySum = logs.filter { it.date == today }.sumOf { it.count }
        historySum + count
    }

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(),
        label = "tasbeeh_scale"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Presets Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(TASBEEH_PRESETS) { preset ->
                    val isSelected = currentDhikr == preset
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setTasbeehDhikr(preset) },
                        label = {
                            Text(
                                text = preset,
                                fontFamily = AmiriFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 15.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Custom Dhikr Field
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customDhikrInput,
                    onValueChange = { customDhikrInput = it },
                    placeholder = { Text("أدخل ذكراً مخصصاً...", fontFamily = AmiriFontFamily) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (customDhikrInput.isNotBlank()) {
                            viewModel.setTasbeehDhikr(customDhikrInput.trim())
                            customDhikrInput = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("اعتماد", fontFamily = AmiriFontFamily, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Big Interactive Tasbeeh Bead Counter
        item {
            Spacer(modifier = Modifier.height(8.dp))
            val progress = (count.toFloat() / target).coerceIn(0f, 1f)
            val isTargetReached = count >= target

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(260.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                if (isTargetReached) IslamicGold.copy(alpha = 0.25f)
                                else MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, radius = 130.dp),
                        onClick = {
                            isPressed = true
                            viewModel.tapTasbeeh()
                            isPressed = false
                        }
                    )
                    .testTag("tasbeeh_counter_button")
            ) {
                // Circular Progress
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    strokeWidth = 10.dp,
                    color = if (isTargetReached) IslamicGold else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = ArabicUtils.toArabicDigits(count),
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 54.sp,
                        color = if (isTargetReached) IslamicGoldDark else MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currentDhikr,
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (isTargetReached) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "اكتمل الهدف ✓",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = IslamicSuccessGreen
                        )
                    }
                }
            }
        }

        // Targets selector & Reset button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(33, 100).forEach { t ->
                    FilterChip(
                        selected = target == t,
                        onClick = { viewModel.setTasbeehTarget(t) },
                        label = {
                            Text(
                                text = "الهدف ${ArabicUtils.toArabicDigits(t)}",
                                fontFamily = AmiriFontFamily,
                                fontWeight = if (target == t) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedButton(
                    onClick = { showResetDialog = true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "إعادة ضبط", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إعادة ضبط", fontFamily = AmiriFontFamily)
                }
            }
        }

        // Custom target row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customTargetInput,
                    onValueChange = { customTargetInput = it.filter { ch -> ch.isDigit() } },
                    placeholder = { Text("أدخل رقماً لهدف مخصص...", fontFamily = AmiriFontFamily) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val n = customTargetInput.toIntOrNull()
                        if (n != null && n > 0) {
                            viewModel.setTasbeehTarget(n)
                            customTargetInput = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("تطبيق", fontFamily = AmiriFontFamily, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Today's Total Summary
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مجموع تسبيحات اليوم:",
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "${ArabicUtils.toArabicDigits(todayTotal)} تسبيحة",
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // History Log
        item {
            if (logs.isNotEmpty()) {
                Text(
                    text = "سجل التسبيح السابق",
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }

        items(logs.take(10), key = { it.id }) { log ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = log.dhikr,
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = log.date,
                            fontFamily = AmiriFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "${ArabicUtils.toArabicDigits(log.count)} تسبيحة",
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("إعادة تعيين العداد؟", fontFamily = AmiriFontFamily, fontWeight = FontWeight.Bold) },
            text = { Text("سيتم حفظ العدد الحالي في السجل والبدء من الصفر.", fontFamily = AmiriFontFamily) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetTasbeeh()
                        showResetDialog = false
                    }
                ) {
                    Text("نعم، إعادة ضبط", fontFamily = AmiriFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("إلغاء", fontFamily = AmiriFontFamily)
                }
            }
        )
    }
}
