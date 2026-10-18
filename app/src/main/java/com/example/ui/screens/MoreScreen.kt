package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmiriFontFamily

data class MoreOption(
    val title: String,
    val subtitle: String,
    val icon: String,
    val testTag: String,
    val onClick: () -> Unit
)

@Composable
fun MoreScreen(
    onNavigateToCalendar: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToWird: () -> Unit,
    onNavigateToTadabbur: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val options = listOf(
        MoreOption(
            title = "التقويم الهجري",
            subtitle = "تقويم أم القرى، تحري الهلال والمناسبات الإسلامية",
            icon = "📅",
            testTag = "more_calendar_item",
            onClick = onNavigateToCalendar
        ),
        MoreOption(
            title = "البحث الشامل",
            subtitle = "البحث في أسماء السور والآيات والأذكار والخواطر",
            icon = "🔎",
            testTag = "more_search_item",
            onClick = onNavigateToSearch
        ),
        MoreOption(
            title = "وردي اليومي",
            subtitle = "متابعة أهدافك الإيمانية اليومية وسجل الإنجاز",
            icon = "✅",
            testTag = "more_wird_item",
            onClick = onNavigateToWird
        ),
        MoreOption(
            title = "دفتر التدبر",
            subtitle = "تسجيل خواطرك وتأملاتك المرتبطة بآيات القرآن",
            icon = "📝",
            testTag = "more_tadabbur_item",
            onClick = onNavigateToTadabbur
        ),
        MoreOption(
            title = "الإعدادات والنسخ الاحتياطي",
            subtitle = "تخصيص المظهر وحجم الخط وتنزيل المحتوى بدون إنترنت",
            icon = "⚙️",
            testTag = "more_settings_item",
            onClick = onNavigateToSettings
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "☪", fontSize = 24.sp, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "رفيق المسلم PRO",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "تطبيق إسلامي شامل يعمل بدون إنترنت مع حفظ تام للخصوصية",
                            fontFamily = AmiriFontFamily,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        items(options.size) { idx ->
            val opt = options[idx]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(opt.testTag)
                    .clickable(onClick = opt.onClick),
                shape = RoundedCornerShape(16.dp),
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
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = opt.icon, fontSize = 20.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = opt.title,
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = opt.subtitle,
                            fontFamily = AmiriFontFamily,
                            fontSize = 13.sp,
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
