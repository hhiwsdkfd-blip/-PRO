package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
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
import com.example.model.DailyWirdGoalEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.RafiqViewModel
import com.example.utils.ArabicUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyWirdScreen(
    viewModel: RafiqViewModel,
    onNavigateBack: () -> Unit
) {
    val goals by viewModel.wirdGoals.collectAsStateWithLifecycle()
    val todayLogs by viewModel.todayWirdLogs.collectAsStateWithLifecycle()
    val todayAdhkarProgress by viewModel.todayAdhkarProgress.collectAsStateWithLifecycle()
    val tasbeehLogs by viewModel.tasbeehLogs.collectAsStateWithLifecycle()
    val tasbeehCount by viewModel.tasbeehCount.collectAsStateWithLifecycle()

    var newGoalTitle by remember { mutableStateOf("") }
    var newGoalTarget by remember { mutableStateOf("1") }
    var newGoalKind by remember { mutableStateOf("manual") }

    val todayIso = viewModel.todayIso

    // Calculate actual progress based on goal type
    val getGoalProgress: (DailyWirdGoalEntity) -> Int = { goal ->
        when (goal.kind) {
            "adhkar" -> {
                // Count completed adhkar today
                todayAdhkarProgress.count { it.currentCount > 0 }
            }
            "tasbeeh" -> {
                val pastToday = tasbeehLogs.filter { it.date == todayIso }.sumOf { it.count }
                pastToday + tasbeehCount
            }
            else -> {
                todayLogs.find { it.goalId == goal.id }?.progress ?: 0
            }
        }
    }

    // Last 7 days stats
    val last7Days = remember {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        for (i in 0 until 7) {
            list.add(sdf.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        list
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Goals List
        if (goals.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🌱", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ابدأ بوضع أهدافك الإيمانية اليومية",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "مثال: قراءة جزء، صلاة الضحى، مائة استغفار...",
                            fontFamily = AmiriFontFamily,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(goals, key = { it.id }) { goal ->
                val currentProgress = getGoalProgress(goal)
                val isCompleted = currentProgress >= goal.target
                val progressRatio = (currentProgress.toFloat() / goal.target).coerceIn(0f, 1f)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCompleted) IslamicSuccessGreen.copy(alpha = 0.08f)
                        else MaterialTheme.colorScheme.surface
                    ),
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = goal.title,
                                    fontFamily = AmiriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                val kindLabel = when (goal.kind) {
                                    "adhkar" -> "مربوط تلقائياً بالأذكار"
                                    "tasbeeh" -> "مربوط تلقائياً بالتسبيح"
                                    else -> "عداد يدوي"
                                }
                                Text(
                                    text = kindLabel,
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(onClick = { viewModel.deleteWirdGoal(goal.id) }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "حذف الهدف",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { progressRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (isCompleted) IslamicSuccessGreen else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${ArabicUtils.toArabicDigits(currentProgress)} / ${ArabicUtils.toArabicDigits(goal.target)}" +
                                        if (isCompleted) "  (اكتمل ✓)" else "",
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isCompleted) IslamicSuccessGreen else MaterialTheme.colorScheme.primary
                            )

                            if (goal.kind == "manual") {
                                Row {
                                    OutlinedIconButton(
                                        onClick = { viewModel.updateWirdProgress(goal.id, currentProgress, -1) },
                                        enabled = currentProgress > 0,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    FilledIconButton(
                                        onClick = { viewModel.updateWirdProgress(goal.id, currentProgress, 1) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Goal Card
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
                        text = "إضافة هدف جديد",
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newGoalTitle,
                        onValueChange = { newGoalTitle = it },
                        placeholder = { Text("عنوان الهدف (مثال: قراءة سورة الملك)", fontFamily = AmiriFontFamily) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = newGoalTarget,
                            onValueChange = { newGoalTarget = it.filter { ch -> ch.isDigit() } },
                            label = { Text("العدد المستهدف", fontFamily = AmiriFontFamily) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Kind chips
                        Column(modifier = Modifier.weight(1f)) {
                            Text("النوع:", fontFamily = AmiriFontFamily, fontSize = 12.sp)
                            Row {
                                FilterChip(
                                    selected = newGoalKind == "manual",
                                    onClick = { newGoalKind = "manual" },
                                    label = { Text("يدوي", fontFamily = AmiriFontFamily, fontSize = 12.sp) }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                FilterChip(
                                    selected = newGoalKind == "tasbeeh",
                                    onClick = { newGoalKind = "tasbeeh" },
                                    label = { Text("تسبيح", fontFamily = AmiriFontFamily, fontSize = 12.sp) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val targetInt = newGoalTarget.toIntOrNull() ?: 1
                            if (newGoalTitle.isNotBlank() && targetInt > 0) {
                                viewModel.addWirdGoal(newGoalTitle.trim(), targetInt, newGoalKind)
                                newGoalTitle = ""
                                newGoalTarget = "1"
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إضافة الهدف", fontFamily = AmiriFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
