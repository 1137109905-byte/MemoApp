package com.memo.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memo.app.data.MemoWithChecks
import com.memo.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoListScreen(
    viewModel: MemoViewModel,
    onNavigateToEditor: (Long?) -> Unit
) {
    val memos by viewModel.filteredMemos.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isDark = isSystemInDarkTheme()
    val palette = ThemeManager.currentPalette(isDark)
    val currentStyle by ThemeManager.style
    val currentPreset by ThemeManager.preset

    var isSearching by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<MemoWithChecks?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    val bgBrush = when (currentStyle) {
        AppStyle.PAPER -> Brush.verticalGradient(
            colors = listOf(
                palette.background.copy(alpha = 0.92f),
                palette.background,
                palette.backgroundAlt
            )
        )
        AppStyle.GLASS -> Brush.verticalGradient(
            colors = listOf(
                palette.backgroundAlt,
                palette.background,
                palette.backgroundAlt.copy(alpha = 0.95f)
            )
        )
        AppStyle.ANIME -> Brush.verticalGradient(
            colors = listOf(
                palette.backgroundAlt,
                palette.background,
                palette.background
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(bgBrush)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ====== Top Bar ======
            Surface(
                color = palette.topBar,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Spacer(modifier = Modifier.fillMaxWidth().statusBarsPadding())
                    Row(
                        modifier = Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSearching) {
                            IconButton(onClick = { isSearching = false; viewModel.setSearchQuery("") }, modifier = Modifier.size(40.dp)) {
                                Icon(Icons.Default.ArrowBack, "返回", tint = palette.topBarIcon, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            TextField(
                                value = searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder = { Text("搜索便签...", color = palette.topBarIcon.copy(alpha = 0.5f)) },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = palette.topBarIcon, unfocusedTextColor = palette.topBarIcon,
                                    cursorColor = palette.accent,
                                    focusedContainerColor = palette.topBarSurface, unfocusedContainerColor = palette.topBarSurface,
                                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                                ),
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                        } else {
                            // Settings
                            IconButton(onClick = { showSettings = true }, modifier = Modifier.size(40.dp)) {
                                Icon(Icons.Default.Settings, "设置", tint = palette.topBarIcon, modifier = Modifier.size(22.dp))
                            }
                            // Title
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val barTitle = if (currentStyle == AppStyle.ANIME) "✧ 便签本 ✧" else "全部便签"
                                    Text(barTitle, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Medium)
                                    Icon(Icons.Default.ArrowDropDown, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                            // Search
                            IconButton(onClick = { isSearching = true }, modifier = Modifier.size(40.dp)) {
                                Icon(Icons.Default.Search, "搜索", tint = palette.topBarIcon, modifier = Modifier.size(20.dp))
                            }
                            // New note
                            IconButton(onClick = { onNavigateToEditor(null) }, modifier = Modifier.size(40.dp)) {
                                Icon(Icons.Default.EditNote, "新建便签", tint = palette.topBarIcon, modifier = Modifier.size(22.dp))
                            }
                        }
                    }
                }
            }

            // ====== Content ======
            if (memos.isEmpty() && !isSearching) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Stylized notepad + pencil
                        Canvas(modifier = Modifier.size(120.dp)) {
                            val w = size.width; val h = size.height
                            drawRoundRect(palette.placeholder, Offset(w*0.15f, h*0.1f), Size(w*0.55f, h*0.7f), CornerRadius(8f,8f), style = Stroke(width = 3f))
                            for (i in 1..4) { val y = h*0.1f + (h*0.7f)*i/5f; drawLine(palette.placeholder.copy(alpha=0.5f), Offset(w*0.22f,y), Offset(w*0.62f,y), strokeWidth = 1.5f) }
                            drawLine(palette.placeholder, Offset(w*0.55f,h*0.15f), Offset(w*0.85f,h*0.65f), strokeWidth = 4f, cap = StrokeCap.Round)
                            val tip = Path().apply { moveTo(w*0.85f,h*0.65f); lineTo(w*0.88f,h*0.72f); lineTo(w*0.82f,h*0.68f); close() }
                            drawPath(tip, palette.placeholder)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        val emptyText = if (currentStyle == AppStyle.ANIME) "还没有便签呢~" else "没有任何便签"
                        Text(emptyText,
                            style = MaterialTheme.typography.titleLarge, color = palette.placeholder, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("点击右上角按钮新建便签", style = MaterialTheme.typography.bodyMedium, color = palette.placeholder.copy(alpha = 0.7f), fontSize = 13.sp)
                    }
                }
            } else {
                val pinned = memos.filter { it.memo.isPinned }
                val unpinned = memos.filter { !it.memo.isPinned }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (pinned.isNotEmpty()) {
                        item { Text("已置顶", style = MaterialTheme.typography.labelMedium, color = palette.textSecondary, modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 2.dp)) }
                        items(pinned, key = { it.memo.id }) { m ->
                            ThemedMemoCard(m, palette, currentStyle, onClick = { onNavigateToEditor(m.memo.id) }, onLongClick = { showDeleteDialog = m })
                        }
                        if (unpinned.isNotEmpty()) {
                            item { Text("便签", style = MaterialTheme.typography.labelMedium, color = palette.textSecondary, modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 2.dp)) }
                        }
                    }
                    items(unpinned, key = { it.memo.id }) { m ->
                        ThemedMemoCard(m, palette, currentStyle, onClick = { onNavigateToEditor(m.memo.id) }, onLongClick = { showDeleteDialog = m })
                    }
                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }
        }
    }

    // Settings sheet
    if (showSettings) {
        ThemeSettingsSheet(palette = palette, onDismiss = { showSettings = false })
    }

    // Delete dialog
    showDeleteDialog?.let { m ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("删除备忘录") },
            text = { Text("确定要删除「${m.memo.title.ifBlank { "无标题" }}」吗？") },
            confirmButton = { TextButton(onClick = { viewModel.deleteMemo(m.memo); showDeleteDialog = null }, colors = ButtonDefaults.textButtonColors(contentColor = palette.error)) { Text("删除") } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = null }) { Text("取消") } },
            containerColor = palette.surface
        )
    }
}

@Composable
fun ThemedMemoCard(memoWithChecks: MemoWithChecks, palette: ThemePalette, style: AppStyle, onClick: () -> Unit, onLongClick: () -> Unit) {
    val memo = memoWithChecks.memo
    val dateStr = remember(memo.updatedAt) { SimpleDateFormat("M月d日 HH:mm", Locale.CHINA).format(Date(memo.updatedAt)) }
    val previewText = if (memo.isChecklist && memoWithChecks.checkItems.isNotEmpty()) {
        val items = memoWithChecks.checkItems.take(3).joinToString("\n") { item ->
            val mark = if (item.isChecked) "☑" else "☐"
            "$mark ${item.text}"
        }
        if (memoWithChecks.checkItems.size > 3) "$items\n+${memoWithChecks.checkItems.size-3}" else items
    } else { memo.content.take(100) }

    val cardShape = if (style == AppStyle.ANIME) RoundedCornerShape(16.dp) else RoundedCornerShape(10.dp)
    val cardBg = when (style) {
        AppStyle.GLASS -> palette.surface.copy(alpha = palette.glassAlpha)
        AppStyle.ANIME -> palette.surface.copy(alpha = 0.9f)
        else -> palette.surface.copy(alpha = 0.85f)
    }
    val borderMod = if (style == AppStyle.GLASS) Modifier.border(1.dp, palette.line.copy(alpha = 0.3f), cardShape) else Modifier

    Surface(
        modifier = Modifier.fillMaxWidth().then(borderMod).clickable(onClick = onClick),
        shape = cardShape, color = cardBg, shadowElevation = palette.cardElevation.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (memo.isPinned) { Icon(Icons.Default.PushPin, null, tint = palette.accent, modifier = Modifier.size(14.dp)); Spacer(Modifier.width(4.dp)) }
                if (memo.autoDelete) { Icon(Icons.Default.Timer, null, tint = palette.error, modifier = Modifier.size(14.dp)); Spacer(Modifier.width(4.dp)) }
                val titleText = memo.title.ifBlank { if (style == AppStyle.ANIME) "新建便签~" else "新建备忘录" }
                Text(titleText, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold, color = palette.text, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(dateStr, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary, fontSize = 12.sp)
                if (previewText.isNotBlank()) {
                    Text("  ")
                    Text(previewText.replace("\n"," "), style = MaterialTheme.typography.bodySmall,
                        color = palette.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f), fontSize = 12.sp)
                }
            }
        }
    }
}

// ========== Theme Settings Sheet ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsSheet(palette: ThemePalette, onDismiss: () -> Unit) {
    val currentStyle by ThemeManager.style
    val currentPreset by ThemeManager.preset

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = palette.surface,
        title = { Text("主题设置", color = palette.text, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Style selection
                Text("风格", color = palette.textSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppStyle.entries.forEach { s ->
                        val selected = s == currentStyle
                        FilterChip(
                            selected = selected, onClick = { ThemeManager.setStyle(s) },
                            label = { Text(s.label, fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = palette.accent, selectedLabelColor = Color.White,
                                containerColor = palette.backgroundAlt, labelColor = palette.text
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Color preset selection
                Text("配色", color = palette.textSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ColorPreset.entries.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            row.forEach { p ->
                                val selected = p == currentPreset
                                val presetLight = buildPalette(AppStyle.PAPER, p, false)
                                Surface(
                                    modifier = Modifier.weight(1f).height(52.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) palette.accent.copy(alpha = 0.15f) else palette.backgroundAlt,
                                    border = if (selected) BorderStroke(2.dp, palette.accent) else null,
                                    onClick = { ThemeManager.setPreset(p) }
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp)) {
                                        // Color swatch
                                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(presetLight.accent))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("${p.emoji} ${p.label}", fontSize = 12.sp, color = palette.text, fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("完成", color = palette.accent) } }
    )
}
