package com.memo.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memo.app.data.CheckItem
import com.memo.app.data.Memo
import com.memo.app.data.MemoWithChecks
import com.memo.app.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoEditorScreen(
    viewModel: MemoViewModel,
    memoId: Long?,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val isNew = memoId == null || memoId == 0L
    val isDark = isSystemInDarkTheme()
    val palette = ThemeManager.currentPalette(isDark)
    val currentStyle by ThemeManager.style

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isChecklist by remember { mutableStateOf(false) }
    var autoDelete by remember { mutableStateOf(false) }
    var autoDeleteTime by remember { mutableStateOf(0L) }
    var isPinned by remember { mutableStateOf(false) }
    var checkItems by remember { mutableStateOf(listOf<CheckItem>()) }
    var existingMemo by remember { mutableStateOf<Memo?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var showAutoDeletePicker by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(memoId) {
        if (memoId != null && memoId != 0L) {
            viewModel.getMemoById(memoId).collect { mc ->
                mc?.let {
                    existingMemo = it.memo; title = it.memo.title; content = it.memo.content
                    isChecklist = it.memo.isChecklist; autoDelete = it.memo.autoDelete
                    autoDeleteTime = it.memo.autoDeleteTime; isPinned = it.memo.isPinned
                    checkItems = it.checkItems.sortedBy { item -> item.order }
                }
            }
        }
    }

    fun saveAndBack() {
        scope.launch {
            if (title.isNotBlank() || content.isNotBlank() || checkItems.isNotEmpty()) {
                if (existingMemo != null) {
                    val u = existingMemo!!.copy(title=title, content=content, isChecklist=isChecklist,
                        autoDelete=autoDelete, autoDeleteTime=autoDeleteTime, isPinned=isPinned, updatedAt=System.currentTimeMillis())
                    viewModel.updateMemo(u)
                    checkItems.forEach { viewModel.updateCheckItem(it) }
                } else {
                    val newId = viewModel.createMemo(title, content, isChecklist, autoDelete, autoDeleteTime)
                    checkItems.forEachIndexed { i, item -> viewModel.insertCheckItem(item.copy(memoId = newId, order = i)) }
                    if (isPinned) { kotlinx.coroutines.delay(100); viewModel.allMemos.value.find { it.memo.id == newId }?.memo?.let { viewModel.togglePin(it) } }
                }
            }
            onBack()
        }
    }

    val paperBg = when (currentStyle) {
        AppStyle.PAPER -> Brush.verticalGradient(listOf(Color(0xFFFAF3E8), palette.surface, Color(0xFFFDF5E6)))
        AppStyle.GLASS -> Brush.verticalGradient(listOf(palette.backgroundAlt.copy(alpha=0.5f), palette.surface.copy(alpha=0.6f), palette.backgroundAlt.copy(alpha=0.5f)))
        AppStyle.ANIME -> Brush.verticalGradient(listOf(palette.backgroundAlt, palette.surface, palette.backgroundAlt))
    }

    Box(modifier = Modifier.fillMaxSize().background(paperBg)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ====== Top Toolbar ======
            Surface(color = palette.topBar, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Spacer(modifier = Modifier.fillMaxWidth().statusBarsPadding())
                    Row(
                        modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { saveAndBack() }, modifier = Modifier.size(38.dp)) {
                            Icon(Icons.Default.ArrowBack, "返回", tint = palette.topBarIcon, modifier = Modifier.size(22.dp))
                        }
                        IconButton(onClick = {}, modifier = Modifier.size(38.dp)) {
                            Icon(Icons.Default.Undo, "撤销", tint = palette.topBarIcon.copy(alpha=0.7f), modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = {}, modifier = Modifier.size(38.dp)) {
                            Icon(Icons.Default.Redo, "重做", tint = palette.topBarIcon.copy(alpha=0.7f), modifier = Modifier.size(20.dp))
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = palette.accent.copy(alpha=0.2f), modifier = Modifier.clickable { }) {
                            Text("AI", color = palette.accent, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                        IconButton(onClick = {}, modifier = Modifier.size(38.dp)) {
                            Icon(Icons.Default.Image, "插入图片", tint = palette.topBarIcon.copy(alpha=0.7f), modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = { saveAndBack() }, modifier = Modifier.size(38.dp)) {
                            Icon(Icons.Default.Check, "保存", tint = palette.accent, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }

            // ====== Header ======
            Surface(color = palette.backgroundAlt.copy(alpha = if(currentStyle==AppStyle.GLASS) 0.3f else 0.6f), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(palette.accent.copy(alpha=0.12f)).clickable{}.padding(horizontal=8.dp, vertical=4.dp)) {
                            val tagLabel = if (currentStyle == AppStyle.ANIME) "全部便签~" else "全部便签"
                            Text(tagLabel, fontSize=12.sp, color=palette.accentDark)
                            Icon(Icons.Default.ArrowDropDown, null, tint=palette.accentDark, modifier=Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { isPinned = !isPinned; existingMemo?.let { viewModel.togglePin(it) } }, modifier = Modifier.size(30.dp)) {
                            Icon(if(isPinned) Icons.Default.Star else Icons.Outlined.Star, "收藏",
                                tint = if(isPinned) palette.accent else palette.textSecondary, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val dateFmt = SimpleDateFormat("yyyy年M月d日 HH:mm", Locale.CHINA)
                        val dateText = dateFmt.format(Date(existingMemo?.updatedAt ?: System.currentTimeMillis()))
                        val charCount = title.length + content.length
                        Text("$dateText | $charCount 字", style = MaterialTheme.typography.bodySmall, color = palette.textSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = if(isChecklist) palette.accent.copy(alpha=0.15f) else palette.accent.copy(alpha=0.15f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text(if(isChecklist) "清单" else "TXT", fontSize=10.sp, color=palette.accentDark, fontWeight=FontWeight.Bold)
                                if(!isChecklist) Icon(Icons.Default.ArrowDropDown, null, tint=palette.accentDark, modifier=Modifier.size(12.dp))
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Box {
                            IconButton(onClick = { showMenu = true }, modifier = Modifier.size(30.dp)) {
                                Icon(Icons.Default.MoreVert, "更多", tint = palette.textSecondary, modifier = Modifier.size(18.dp))
                            }
                            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                DropdownMenuItem(
                                    text = { Row(verticalAlignment=Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckBox,null,modifier=Modifier.size(20.dp)); Spacer(Modifier.width(12.dp))
                                        val toggleLabel = if (isChecklist) "切换为普通备忘" else "切换为待办清单"
                                        Text(toggleLabel)
                                    }},
                                    onClick = {
                                        if(!isChecklist && content.isNotBlank()) { checkItems = content.split("\n").filter{it.isNotBlank()}.mapIndexed{i,l->CheckItem(text=l.trim(),order=i)}; content="" }
                                        else if(isChecklist && checkItems.isNotEmpty()) { content = checkItems.joinToString("\n"){it.text}; checkItems = emptyList() }
                                        isChecklist = !isChecklist; showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Row(verticalAlignment=Alignment.CenterVertically) {
                                        Icon(Icons.Default.Timer, null, modifier=Modifier.size(20.dp), tint=if(autoDelete) palette.error else Color.Unspecified)
                                        Spacer(Modifier.width(12.dp))
                                        Column { val delLabel = if (autoDelete) "关闭自动删除" else "自动删除设置"; Text(delLabel); Text("自定义删除时间",style=MaterialTheme.typography.labelSmall,color=palette.textSecondary) }
                                    }},
                                    onClick = { showAutoDeletePicker = !showAutoDeletePicker; showMenu = false }
                                )
                                if(existingMemo != null) {
                                    Divider()
                                    DropdownMenuItem(
                                        text = { Row(verticalAlignment=Alignment.CenterVertically) {
                                            Icon(Icons.Default.Delete,null,tint=palette.error,modifier=Modifier.size(20.dp)); Spacer(Modifier.width(12.dp)); Text("删除",color=palette.error)
                                        }},
                                        onClick = { viewModel.deleteMemo(existingMemo!!); showMenu=false; onBack() }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ====== Auto-delete indicator ======
            if (autoDelete) {
                Surface(shape = RoundedCornerShape(6.dp), color = palette.error.copy(alpha=0.08f),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
                    Row(modifier = Modifier.padding(horizontal=12.dp, vertical=6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, null, tint = palette.error, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        val timeText = if (autoDeleteTime > 0) {
                            "将在 ${SimpleDateFormat("M月d日 HH:mm", Locale.CHINA).format(Date(autoDeleteTime))} 自动删除"
                        } else { "此备忘录将在明天0点自动删除" }
                        Text(timeText, style = MaterialTheme.typography.bodySmall, color = palette.error, fontSize = 12.sp)
                    }
                }
            }

            // ====== Auto-delete picker panel ======
            if (showAutoDeletePicker) {
                AutoDeletePickerPanel(
                    palette = palette,
                    autoDelete = autoDelete,
                    autoDeleteTime = autoDeleteTime,
                    onSetTomorrow = {
                        val t = Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_YEAR,1); set(Calendar.HOUR_OF_DAY,0); set(Calendar.MINUTE,0); set(Calendar.SECOND,0); set(Calendar.MILLISECOND,0)
                        }.timeInMillis
                        autoDelete = true; autoDeleteTime = t
                        existingMemo?.let { viewModel.setAutoDelete(it, true, t) }
                        showAutoDeletePicker = false
                    },
                    onSetCustom = { hours ->
                        val t = System.currentTimeMillis() + hours * 3600000L
                        autoDelete = true; autoDeleteTime = t
                        existingMemo?.let { viewModel.setAutoDelete(it, true, t) }
                        showAutoDeletePicker = false
                    },
                    onDisable = {
                        autoDelete = false; autoDeleteTime = 0L
                        existingMemo?.let { viewModel.setAutoDelete(it, false, 0L) }
                        showAutoDeletePicker = false
                    }
                )
            }

            // ====== Content Area (Lined Paper) ======
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // Draw lines for paper effect
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val lineSpacingPx = 36.dp.toPx()
                    var y = lineSpacingPx * 2
                    while (y < size.height) {
                        drawLine(palette.line.copy(alpha = if(currentStyle==AppStyle.GLASS) 0.2f else 0.35f),
                            Offset(16.dp.toPx(), y), Offset(size.width - 16.dp.toPx(), y), strokeWidth = 0.8f)
                        y += lineSpacingPx
                    }
                }
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                    BasicTextField(
                        value = title, onValueChange = { title = it },
                        textStyle = TextStyle(fontSize=24.sp, fontWeight=FontWeight.Bold, color=palette.text, lineHeight=32.sp),
                        cursorBrush = SolidColor(palette.accent),
                        decorationBox = { inner ->
                            Box(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
                                if(title.isEmpty()) {
                                    val titleHint = if (currentStyle == AppStyle.ANIME) "写个标题吧~" else "标题"
                                    Text(titleHint, style=TextStyle(fontSize=24.sp, fontWeight=FontWeight.Bold, color=palette.placeholder.copy(alpha=0.5f)))
                                }
                                inner()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                    )
                    Divider(color = palette.line.copy(alpha=0.5f), thickness = 1.dp, modifier = Modifier.padding(bottom = 12.dp))
                    if (isChecklist) {
                        ChecklistContent(
                            items = checkItems, onItemsChanged = { checkItems = it },
                            onAddItem = { text -> checkItems = checkItems + CheckItem(text=text, order=checkItems.size, memoId=existingMemo?.id?:0) },
                            onToggleItem = { i -> checkItems = checkItems.toMutableList().also { it[i] = it[i].copy(isChecked = !it[i].isChecked) } },
                            onDeleteItem = { i -> val item = checkItems[i]; if(item.id!=0L) viewModel.deleteCheckItem(item); checkItems = checkItems.toMutableList().also { it.removeAt(i) } },
                            onUpdateItem = { i, text -> checkItems = checkItems.toMutableList().also { it[i] = it[i].copy(text = text) } }
                        )
                    } else {
                        BasicTextField(
                            value = content, onValueChange = { content = it },
                            textStyle = TextStyle(fontSize=16.sp, lineHeight=36.sp, color=palette.text),
                            cursorBrush = SolidColor(palette.accent),
                            decorationBox = { inner ->
                                Box {
                                    if(content.isEmpty()) {
                                        val contentHint = if (currentStyle == AppStyle.ANIME) "写点什么吧~" else "开始输入..."
                                        Text(contentHint, style=TextStyle(fontSize=16.sp, lineHeight=36.sp, color=palette.placeholder.copy(alpha=0.5f)))
                                    }
                                    inner()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        )
                    }
                }
            }

            // ====== Bottom Formatting Toolbar ======
            Surface(color = palette.backgroundAlt.copy(alpha = if(currentStyle==AppStyle.GLASS) 0.5f else 0.9f),
                shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp).navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
                ) {
                    FmtButton("H", null, "标题", false, palette) {}
                    FmtButton(null, Icons.Default.FormatAlignCenter, "居中", false, palette) {}
                    FmtButton(null, Icons.Default.FormatListBulleted, "列表", false, palette) {}
                    FmtButton("B", null, "粗体", true, palette) {}
                    FmtButton("\u201C", null, "引用", false, palette) {}
                    FmtButton(null, Icons.Default.CheckBox, "待办", false, palette) {
                        if(!isChecklist && content.isNotBlank()) { checkItems = content.split("\n").filter{it.isNotBlank()}.mapIndexed{i,l->CheckItem(text=l.trim(),order=i)}; content="" }
                        isChecklist = !isChecklist
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) { if(isNew) try { focusRequester.requestFocus() } catch(_:Exception){} }
}

// ========== Auto-delete picker ==========
@Composable
fun AutoDeletePickerPanel(
    palette: ThemePalette, autoDelete: Boolean, autoDeleteTime: Long,
    onSetTomorrow: () -> Unit, onSetCustom: (hours: Int) -> Unit, onDisable: () -> Unit
) {
    Surface(color = palette.backgroundAlt.copy(alpha = 0.9f), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("自动删除设置", fontWeight = FontWeight.Bold, color = palette.text, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(10.dp))

            // Quick: tomorrow midnight
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable { onSetTomorrow() }.padding(vertical = 8.dp)) {
                Icon(Icons.Default.Today, null, tint = palette.accent, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Column { Text("明天0点删除", color = palette.text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text("推荐 · 最常用", color = palette.textSecondary, fontSize = 11.sp) }
            }

            // Custom time options
            val customOptions = listOf(
                1 to "1小时后", 6 to "6小时后", 12 to "12小时后",
                24 to "1天后", 72 to "3天后", 168 to "7天后"
            )
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                customOptions.take(3).forEach { (h, label) ->
                    OutlinedButton(
                        onClick = { onSetCustom(h) }, modifier = Modifier.weight(1f).height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = palette.text),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) { Text(label, fontSize = 11.sp) }
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                customOptions.drop(3).forEach { (h, label) ->
                    OutlinedButton(
                        onClick = { onSetCustom(h) }, modifier = Modifier.weight(1f).height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = palette.text),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) { Text(label, fontSize = 11.sp) }
                }
            }

            if (autoDelete) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onDisable, colors = ButtonDefaults.textButtonColors(contentColor = palette.error)) {
                    Text("关闭自动删除", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun FmtButton(text: String?, icon: androidx.compose.ui.graphics.vector.ImageVector?, label: String, isBold: Boolean, palette: ThemePalette, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clip(RoundedCornerShape(6.dp)).clickable(onClick = onClick).padding(horizontal = 8.dp, vertical = 4.dp)) {
        if (text != null) Text(text, fontSize = 18.sp, fontWeight = if(isBold) FontWeight.Black else FontWeight.Bold, color = palette.text)
        else if (icon != null) Icon(icon, label, tint = palette.text, modifier = Modifier.size(20.dp))
        Text(label, fontSize = 9.sp, color = palette.textSecondary, modifier = Modifier.padding(top = 1.dp))
    }
}

@Composable
fun ChecklistContent(
    items: List<CheckItem>, onItemsChanged: (List<CheckItem>) -> Unit,
    onAddItem: (String) -> Unit, onToggleItem: (Int) -> Unit, onDeleteItem: (Int) -> Unit, onUpdateItem: (Int, String) -> Unit
) {
    var newItemText by remember { mutableStateOf("") }
    val isDark = isSystemInDarkTheme()
    val palette = ThemeManager.currentPalette(isDark)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        val unchecked = items.mapIndexed { i, item -> i to item }.filter { !it.second.isChecked }
        val checked = items.mapIndexed { i, item -> i to item }.filter { it.second.isChecked }
        items(unchecked.size) { li ->
            val (oi, item) = unchecked[li]
            ChecklistItemRow(item, { onToggleItem(oi) }, { onDeleteItem(oi) }, { t -> onUpdateItem(oi, t) }, palette)
        }
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, null, tint = palette.textSecondary, modifier = Modifier.size(22.dp)); Spacer(Modifier.width(12.dp))
                BasicTextField(value = newItemText, onValueChange = { newItemText = it },
                    textStyle = TextStyle(fontSize=16.sp, lineHeight=30.sp, color=palette.text), cursorBrush = SolidColor(palette.accent),
                    decorationBox = { inner -> Box { if(newItemText.isEmpty()) Text("新建待办事项", style=TextStyle(fontSize=16.sp, lineHeight=30.sp, color=palette.placeholder.copy(alpha=0.5f))); inner() } },
                    modifier = Modifier.weight(1f),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { if(newItemText.isNotBlank()) { onAddItem(newItemText.trim()); newItemText = "" } }))
            }
        }
        if (checked.isNotEmpty()) {
            item { Spacer(Modifier.height(16.dp)); Text("已完成 ${checked.size}", style=MaterialTheme.typography.labelMedium, color=palette.textSecondary, modifier=Modifier.padding(start=34.dp, bottom=4.dp)) }
            items(checked.size) { li -> val (oi, item) = checked[li]; ChecklistItemRow(item, { onToggleItem(oi) }, { onDeleteItem(oi) }, { t -> onUpdateItem(oi, t) }, palette, true) }
        }
        item { Spacer(modifier = Modifier.height(200.dp)) }
    }
}

@Composable
fun ChecklistItemRow(item: CheckItem, onToggle: () -> Unit, onDelete: () -> Unit, onUpdate: (String) -> Unit, palette: ThemePalette, dimmed: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(22.dp).clip(CircleShape).clickable(onClick = onToggle).background(if(item.isChecked) palette.accent else Color.Transparent), contentAlignment = Alignment.Center) {
            if(item.isChecked) Icon(Icons.Default.Check, null, tint = palette.text, modifier = Modifier.size(16.dp))
            else Box(Modifier.size(20.dp).clip(CircleShape).background(Color.Transparent)) { Box(Modifier.fillMaxSize().padding(1.dp).clip(CircleShape).background(palette.line.copy(alpha=0.4f))) }
        }
        Spacer(Modifier.width(12.dp))
        BasicTextField(value = item.text, onValueChange = onUpdate,
            textStyle = TextStyle(fontSize=16.sp, lineHeight=30.sp, color=palette.text.copy(alpha=if(dimmed)0.4f else 1f), textDecoration=if(item.isChecked) TextDecoration.LineThrough else TextDecoration.None),
            cursorBrush = SolidColor(palette.accent), modifier = Modifier.weight(1f), singleLine = false)
    }
}
