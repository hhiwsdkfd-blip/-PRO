package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DefaultIslamicData
import com.example.model.AdhkarItemEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.RafiqViewModel
import com.example.utils.ArabicUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdhkarCategoryScreen(
    categoryKey: String,
    viewModel: RafiqViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val categoryMeta = ADHKAR_CATEGORIES.find { it.key == categoryKey }
    val categoryTitle = categoryMeta?.title ?: "الأذكار"

    val adhkarFlow = remember(categoryKey) {
        viewModel.getAdhkarForCategory(categoryKey)
    }
    val adhkarList by adhkarFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val todayProgress by viewModel.todayAdhkarProgress.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }

    val displayList = remember(adhkarList, searchQuery) {
        val base = if (adhkarList.isNotEmpty()) {
            adhkarList
        } else {
            DefaultIslamicData.INITIAL_ADHKAR.filter { it.category == categoryKey }
        }

        if (searchQuery.isEmpty()) {
            base
        } else {
            val q = ArabicUtils.normalizeArabic(searchQuery)
            base.filter {
                ArabicUtils.normalizeArabic(it.text).contains(q) ||
                        ArabicUtils.normalizeArabic(it.title).contains(q)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = categoryTitle,
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
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .testTag("adhkar_search_field"),
                placeholder = {
                    Text("بحث في أذكار هذا القسم...", fontFamily = AmiriFontFamily)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد أذكار في هذا القسم",
                        fontFamily = AmiriFontFamily,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(displayList, key = { it.id }) { item ->
                        val progress = todayProgress.find { it.dhikrId == item.id }
                        val currentCount = progress?.currentCount ?: 0
                        val isDone = currentCount >= item.repeat
                        val isFav = favorites.any { it.type == "dhikr" && it.itemId == item.id }

                        DhikrCard(
                            item = item,
                            currentCount = currentCount,
                            isDone = isDone,
                            isFav = isFav,
                            onIncrement = {
                                viewModel.incrementAdhkar(item.id, currentCount, item.repeat)
                            },
                            onToggleFav = {
                                viewModel.toggleFavorite("dhikr", item.id, isFav)
                            },
                            onShare = {
                                val shareText = "${item.text}\n\n[${item.title} - تكرار: ${item.repeat} مرات]\n${item.reference}"
                                val intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(intent, "مشاركة الذكر"))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DhikrCard(
    item: AdhkarItemEntity,
    currentCount: Int,
    isDone: Boolean,
    isFav: Boolean,
    onIncrement: () -> Unit,
    onToggleFav: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dhikr_card_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDone) 0.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title and Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.title.isNotEmpty()) {
                    Text(
                        text = item.title,
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                IconButton(onClick = onToggleFav, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (isFav) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "المفضلة",
                        tint = if (isFav) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onShare, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "مشاركة",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dhikr text in Amiri
            Text(
                text = item.text,
                fontFamily = AmiriFontFamily,
                fontSize = 20.sp,
                lineHeight = 32.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            if (item.reference.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• ${item.reference}",
                    fontFamily = AmiriFontFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Count & Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDone) IslamicSuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${ArabicUtils.toArabicDigits(currentCount)} / ${ArabicUtils.toArabicDigits(item.repeat)}",
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isDone) IslamicSuccessGreen else MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Button(
                    onClick = onIncrement,
                    enabled = !isDone,
                    shape = RoundedCornerShape(12.dp),
                    colors = if (isDone) ButtonDefaults.buttonColors(containerColor = IslamicSuccessGreen)
                    else ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (isDone) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("اكتمل ✓", fontFamily = AmiriFontFamily, fontWeight = FontWeight.Bold)
                    } else {
                        Text("أكملت مرة", fontFamily = AmiriFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
