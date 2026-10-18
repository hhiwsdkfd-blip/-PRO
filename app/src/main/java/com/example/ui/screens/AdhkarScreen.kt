package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DefaultIslamicData
import com.example.ui.theme.*
import com.example.ui.viewmodel.RafiqViewModel
import com.example.utils.ArabicUtils

data class CategoryMeta(
    val key: String,
    val title: String,
    val icon: String,
    val description: String
)

val ADHKAR_CATEGORIES = listOf(
    CategoryMeta("morning", "أذكار الصباح", "🌅", "أذكار الصباح وحفظ اليوم"),
    CategoryMeta("evening", "أذكار المساء", "🌙", "أذكار المساء وحفظ الليلة"),
    CategoryMeta("after_prayer", "أذكار بعد الصلاة", "🕌", "الأذكار المسنونة دبر الصلوات المكتوبة"),
    CategoryMeta("sleep", "أذكار النوم", "🛌", "أذكار النوم والاستعداد للراحة"),
    CategoryMeta("wake", "أذكار الاستيقاظ", "☀️", "أذكار الاستيقاظ والحمد لله"),
    CategoryMeta("misc", "أذكار وأدعية متنوعة", "🤲", "أدعية المسجد والسفر والكرب وغيرها")
)

@Composable
fun AdhkarScreen(
    viewModel: RafiqViewModel,
    onSelectCategory: (categoryKey: String) -> Unit
) {
    val todayProgress by viewModel.todayAdhkarProgress.collectAsStateWithLifecycle()
    val statusMsg by viewModel.downloadStatusMsg.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        if (statusMsg != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = statusMsg!!,
                            fontFamily = AmiriFontFamily,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.clearStatusMsg() }) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق")
                        }
                    }
                }
            }
        }

        items(ADHKAR_CATEGORIES, key = { it.key }) { cat ->
            val completedCount = todayProgress.count { it.dhikrId.startsWith(cat.key.take(1)) && it.currentCount > 0 }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("adhkar_cat_${cat.key}")
                    .clickable { onSelectCategory(cat.key) },
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
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(50.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = cat.icon, fontSize = 24.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = cat.title,
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = cat.description,
                            fontFamily = AmiriFontFamily,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (completedCount > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "أكملت اليوم: ${ArabicUtils.toArabicDigits(completedCount)} أذكار ✓",
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = IslamicSuccessGreen
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

        item {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { viewModel.downloadAdhkarFromNet() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.CloudDownload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تحديث أذكار حصن المسلم من الإنترنت",
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
