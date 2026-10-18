package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.RafiqViewModel
import com.example.utils.ArabicUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: RafiqViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val currentTheme by viewModel.appTheme.collectAsStateWithLifecycle()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()
    val fontSize by viewModel.quranFontSize.collectAsStateWithLifecycle()
    val statusMsg by viewModel.downloadStatusMsg.collectAsStateWithLifecycle()
    val isDownloading by viewModel.isDownloadingQuran.collectAsStateWithLifecycle()

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "الإعدادات والنسخ الاحتياطي",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
        ) {
            // Appearance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "المظهر ونمط العرض",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("auto" to "تلقائي", "light" to "نهاري", "dark" to "ليلي").forEach { (key, label) ->
                                val isSelected = currentTheme == key
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.updateTheme(key) },
                                    label = { Text(label, fontFamily = AmiriFontFamily) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Vibration Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "الاهتزاز والتفاعل اللمسي",
                                    fontFamily = AmiriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "نبضات اهتزاز خفيفة عند الضغط على المسبحة والأذكار",
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = vibrationEnabled,
                                onCheckedChange = { viewModel.toggleVibration(it) },
                                modifier = Modifier.testTag("vibration_toggle")
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Font Size Slider
                        Text(
                            text = "حجم خط الآيات والأذكار: ${ArabicUtils.toArabicDigits(fontSize)}",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Slider(
                            value = fontSize.toFloat(),
                            onValueChange = { viewModel.updateFontSize(it.toInt()) },
                            valueRange = 18f..48f,
                            steps = 15,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Offline Content Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "المحتوى والعمل بدون إنترنت",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "يحتوي التطبيق على الأذكار الأساسية وسور مختارة. يمكنك تحميل المصحف الشريف كاملاً ليعمل دائمًا بدون إنترنت.",
                            fontFamily = AmiriFontFamily,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.downloadFullQuran() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isDownloading,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isDownloading) "جارٍ تنزيل المصحف..." else "تنزيل المصحف كاملًا (١١٤ سورة)",
                                fontFamily = AmiriFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { viewModel.downloadAdhkarFromNet() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تحديث أذكار حصن المسلم",
                                fontFamily = AmiriFontFamily
                            )
                        }
                    }
                }
            }

            // Backup & Restore Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "النسخ الاحتياطي ونقل البيانات",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "يشمل النسخ الاحتياطي: ملاحظات التدبر، العلامات المرجعية، المفضلة، وسجل الأذكار والتسبيح.",
                            fontFamily = AmiriFontFamily,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val backupJson = viewModel.exportBackup()
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("رفيق المسلم - نسخة احتياطية", backupJson)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "تم نسخ بيانات النسخة الاحتياطية للحافظة بنجاح!", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGold),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("نسخ النسخة الاحتياطية كنص", fontFamily = AmiriFontFamily, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { showImportDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("استيراد من نص نسخة سابقة", fontFamily = AmiriFontFamily)
                        }
                    }
                }
            }

            // Danger Zone Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "إعادة الضبط وحذف البيانات",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "مسح جميع الملاحظات والعلامات وسجل التسبيح والبدء من جديد.",
                            fontFamily = AmiriFontFamily,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("حذف جميع بياناتي", fontFamily = AmiriFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Privacy Assurance Note
            item {
                Text(
                    text = "🔒 الخصوصية التامة: يعمل التطبيق بالكامل على جهازك دون أي تتبع، ولا يتم رفع أو مشاركة أي بيانات شخصية مع أي خوادم خارجية.",
                    fontFamily = AmiriFontFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text("تأكيد حذف جميع البيانات؟", fontFamily = AmiriFontFamily, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "هل أنت متأكد من رغبتك في حذف جميع الملاحظات والعلامات وسجلات الأذكار؟ لا يمكن التراجع عن هذا الإجراء.",
                    fontFamily = AmiriFontFamily
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.wipeAllData {
                            Toast.makeText(context, "تم مسح جميع البيانات بنجاح", Toast.LENGTH_SHORT).show()
                        }
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("نعم، احذف نهائياً", fontFamily = AmiriFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("إلغاء", fontFamily = AmiriFontFamily)
                }
            }
        )
    }

    // Import Backup Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = {
                Text("استيراد نسخة احتياطية", fontFamily = AmiriFontFamily, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("الصق نص النسخة الاحتياطية أدناه:", fontFamily = AmiriFontFamily)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importJsonText.isNotBlank()) {
                            coroutineScope.launch {
                                val success = viewModel.importBackup(importJsonText.trim())
                                if (success) {
                                    Toast.makeText(context, "تم استيراد البيانات بنجاح!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "فشل الاستيراد: تأكد من صحة نص النسخة", Toast.LENGTH_LONG).show()
                                }
                                showImportDialog = false
                                importJsonText = ""
                            }
                        }
                    }
                ) {
                    Text("استيراد", fontFamily = AmiriFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("إلغاء", fontFamily = AmiriFontFamily)
                }
            }
        )
    }
}
