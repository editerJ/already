@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.regretdiary.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity

@Composable
fun MindJournalApp() {
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val entries = remember { mutableStateListOf<Entry>() }
    var tab by rememberSaveable { mutableStateOf(TabItem.HOME) }
    var productState by remember { mutableStateOf(ProductState()) }
    var isUnlocked by remember { mutableStateOf(true) }
    var rewardAdUiState by remember {
        mutableStateOf(
            RewardAdUiState(
                statusMessage = loc(AppLanguage.KO, "광고 보상을 준비하는 중이에요.", "Preparing rewarded ads.", "リワード広告を準備しています。")
            )
        )
    }
    var situationInput by rememberSaveable { mutableStateOf("") }
    var feelingInput by rememberSaveable { mutableStateOf("") }
    var lessonInput by rememberSaveable { mutableStateOf("") }
    var editingEntryId by rememberSaveable { mutableStateOf<String?>(null) }
    var editorMessage by rememberSaveable { mutableStateOf("") }
    var deleteTargetId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        entries.clear()
        entries.addAll(loadEntries(context))
        productState = loadProductState(context)
        isUnlocked = !productState.lockEnabled
    }

    val language = productState.language
    val latestLanguage = rememberUpdatedState(language)
    val latestContext = rememberUpdatedState(context)
    val latestProductState = rememberUpdatedState(productState)
    val rewardAdsManager = remember(activity) {
        RewardAdsManager(
            activity = activity,
            getLanguage = { latestLanguage.value },
            onStateChanged = { state -> rewardAdUiState = state },
            onRewardEarned = { feature ->
                val next = latestProductState.value.withReward(feature)
                productState = next
                saveProductState(latestContext.value, next)
            }
        )
    }

    DisposableEffect(activity) {
        rewardAdsManager.start()
        onDispose { rewardAdsManager.end() }
    }

    val orderedEntries = if (productState.sortDescending) {
        entries.sortedByDescending { it.timestamp }
    } else {
        entries.sortedBy { it.timestamp }
    }
    val stats = computeJournalStats(entries, language)
    val insightReport = buildInsightReport(entries, language)
    val coachProfile = buildCoachProfile(entries, language)
    val trendReport = buildTrendReport(entries, language)
    val deleteTarget = entries.firstOrNull { it.id == deleteTargetId }

    fun persistEntries(nextEntries: List<Entry>) {
        entries.clear()
        entries.addAll(nextEntries)
        saveEntries(context, nextEntries)
    }

    fun resetEditor(message: String = "") {
        situationInput = ""
        feelingInput = ""
        lessonInput = ""
        editingEntryId = null
        editorMessage = message
    }

    fun beginEdit(entry: Entry) {
        editingEntryId = entry.id
        situationInput = entry.situation
        feelingInput = entry.feeling
        lessonInput = entry.lesson
        editorMessage = loc(language, "기록을 수정 중이에요. 저장하면 같은 자리에 반영됩니다.", "Editing an existing reflection. Saving will update it in place.")
        tab = TabItem.HOME
    }

    fun requestDelete(entry: Entry) {
        deleteTargetId = entry.id
    }

    if (!productState.onboardingDone) {
        OnboardingScreen(
            selectedLanguage = productState.language,
            isBiometricAvailable = deviceSecurityAvailable(context),
            onStart = { selectedLanguage, shouldEnableLock, selectedFont ->
                val next = productState.copy(
                    onboardingDone = true,
                    language = selectedLanguage,
                    lockEnabled = shouldEnableLock && deviceSecurityAvailable(context),
                    fontStyle = selectedFont
                )
                productState = next
                saveProductState(context, next)
                isUnlocked = true
            }
        )
        return
    }

    if (productState.lockEnabled && !isUnlocked) {
        LockScreen(
            language = language,
            isBiometricAvailable = deviceSecurityAvailable(context),
            onUnlock = {
                showAppUnlockPrompt(
                    activity = activity,
                    language = language,
                    onSuccess = { isUnlocked = true },
                    onFailure = { }
                )
            },
            onDisableLock = {
                val next = productState.copy(lockEnabled = false)
                productState = next
                saveProductState(context, next)
                isUnlocked = true
            }
        )
        return
    }

    MindJournalFontProvider(productState.fontStyle) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BackgroundTint.copy(alpha = 0.65f), Background)
                    )
                )
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    NavigationBar(containerColor = SurfaceCard) {
                        NavigationBarItem(
                            selected = tab == TabItem.HOME,
                            onClick = { tab = TabItem.HOME },
                            icon = { Icon(Icons.Outlined.AutoStories, null) },
                            label = { Text(loc(language, "홈", "Home")) }
                        )
                        NavigationBarItem(
                            selected = tab == TabItem.INSIGHTS,
                            onClick = { tab = TabItem.INSIGHTS },
                            icon = { Icon(Icons.Outlined.Insights, null) },
                            label = { Text(loc(language, "인사이트", "Insights")) }
                        )
                        NavigationBarItem(
                            selected = tab == TabItem.ARCHIVE,
                            onClick = { tab = TabItem.ARCHIVE },
                            icon = { Icon(Icons.Outlined.Inventory2, null) },
                            label = { Text(loc(language, "보관함", "Archive")) }
                        )
                        NavigationBarItem(
                            selected = tab == TabItem.SETTINGS,
                            onClick = { tab = TabItem.SETTINGS },
                            icon = { Icon(Icons.Outlined.Settings, null) },
                            label = { Text(loc(language, "설정", "Settings")) }
                        )
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Header(language = language, stats = stats)
                    Spacer(Modifier.height(14.dp))

                when (tab) {
                    TabItem.HOME -> HomeScreen(
                        language = language,
                        stats = stats,
                        entries = orderedEntries,
                        isDescending = productState.sortDescending,
                        productState = productState,
                        rewardAdUiState = rewardAdUiState,
                        situation = situationInput,
                        feeling = feelingInput,
                        lesson = lessonInput,
                        isEditing = editingEntryId != null,
                        editorMessage = editorMessage,
                        liveCoachHint = buildLiveCoachHint(situationInput, feelingInput, language),
                        onSituationChange = {
                            situationInput = it
                            if (editorMessage.isNotBlank()) editorMessage = ""
                        },
                        onFeelingChange = {
                            feelingInput = it
                            if (editorMessage.isNotBlank()) editorMessage = ""
                        },
                        onLessonChange = {
                            lessonInput = it
                            if (editorMessage.isNotBlank()) editorMessage = ""
                        },
                        onCancelEdit = { resetEditor() },
                        onToggleSort = {
                            val next = productState.copy(sortDescending = !productState.sortDescending)
                            productState = next
                            saveProductState(context, next)
                        },
                        onWatchAiAd = { rewardAdsManager.showRewardedAd(RewardFeature.AI_COACH) },
                        onWatchTrendAd = { rewardAdsManager.showRewardedAd(RewardFeature.TREND_REPORT) },
                        onWatchPdfAd = { rewardAdsManager.showRewardedAd(RewardFeature.PDF_EXPORT) },
                        onSave = {
                            val current = entries.firstOrNull { it.id == editingEntryId }
                            val nextEntry = createEntry(
                                situation = situationInput,
                                feeling = feelingInput,
                                lesson = lessonInput,
                                aiReflection = buildSingleEntryReflection(situationInput, feelingInput, language).reflection,
                                aiCoachEnabled = productState.aiCoachUnlocked,
                                language = language,
                                existingId = current?.id,
                                timestamp = current?.timestamp ?: System.currentTimeMillis()
                            )
                            val nextEntries = if (current == null) {
                                listOf(nextEntry) + entries
                            } else {
                                entries.map { if (it.id == current.id) nextEntry else it }
                            }
                            persistEntries(nextEntries.sortedByDescending { it.timestamp })
                            resetEditor(
                                if (current == null) {
                                    loc(language, "새 기록이 저장되었어요.", "A new reflection has been saved.")
                                } else {
                                    loc(language, "기록 수정이 저장되었어요.", "The reflection has been updated.")
                                }
                            )
                            tab = TabItem.HOME
                        },
                        onEdit = { beginEdit(it) },
                        onDelete = { requestDelete(it) }
                    )
                    TabItem.INSIGHTS -> InsightsScreen(
                        language = language,
                        stats = stats,
                        insightReport = insightReport,
                        coachProfile = coachProfile,
                        trendReport = trendReport,
                        productState = productState,
                        rewardAdUiState = rewardAdUiState,
                        onWatchAiAd = { rewardAdsManager.showRewardedAd(RewardFeature.AI_COACH) },
                        onWatchTrendAd = { rewardAdsManager.showRewardedAd(RewardFeature.TREND_REPORT) },
                        onWatchPdfAd = { rewardAdsManager.showRewardedAd(RewardFeature.PDF_EXPORT) },
                        onExportPdf = {
                            if (productState.pdfExportCredits <= 0) {
                                PdfExportResult(
                                    success = false,
                                    message = loc(
                                        language,
                                        "PDF를 내보내려면 먼저 광고를 보고 저장권을 획득해주세요.",
                                        "Watch an ad to earn a PDF export credit first.",
                                        "PDFを書き出すには、先に広告を見て保存権を獲得してください。"
                                    )
                                )
                            } else {
                                val result = exportInsightPdf(
                                    context = context,
                                    insightReport = insightReport,
                                    coachProfile = coachProfile,
                                    trendReport = trendReport,
                                    entries = orderedEntries,
                                    stats = stats,
                                    language = language
                                )
                                if (result.success) {
                                    val next = productState.consumePdfCredit()
                                    productState = next
                                    saveProductState(context, next)
                                }
                                result
                            }
                        }
                    )
                    TabItem.ARCHIVE -> ArchiveScreen(
                        language = language,
                        entries = orderedEntries,
                        onEdit = { beginEdit(it) },
                        onDelete = { requestDelete(it) }
                    )
                    TabItem.SETTINGS -> SettingsScreen(
                        language = language,
                        productState = productState,
                        rewardAdUiState = rewardAdUiState,
                        isBiometricAvailable = deviceSecurityAvailable(context),
                        onWatchAiAd = { rewardAdsManager.showRewardedAd(RewardFeature.AI_COACH) },
                        onWatchTrendAd = { rewardAdsManager.showRewardedAd(RewardFeature.TREND_REPORT) },
                        onWatchPdfAd = { rewardAdsManager.showRewardedAd(RewardFeature.PDF_EXPORT) },
                        onToggleLock = {
                            if (productState.lockEnabled) {
                                val next = productState.copy(lockEnabled = false)
                                productState = next
                                saveProductState(context, next)
                                isUnlocked = true
                            } else {
                                showAppUnlockPrompt(
                                    activity = activity,
                                    language = language,
                                    onSuccess = {
                                        val next = productState.copy(lockEnabled = true)
                                        productState = next
                                        saveProductState(context, next)
                                        isUnlocked = true
                                    },
                                    onFailure = { }
                                )
                            }
                        },
                        onShowTutorial = {
                            val next = productState.copy(onboardingDone = false)
                            productState = next
                            saveProductState(context, next)
                        },
                        onChangeLanguage = { nextLanguage ->
                            val next = productState.copy(language = nextLanguage)
                            productState = next
                            saveProductState(context, next)
                        },
                        onChangeFont = { nextFont ->
                            val next = productState.copy(fontStyle = nextFont)
                            productState = next
                            saveProductState(context, next)
                        }
                    )
                }
            }
        }

            if (deleteTarget != null) {
                AlertDialog(
                    onDismissRequest = { deleteTargetId = null },
                    title = { Text(loc(language, "기록 삭제", "Delete reflection")) },
                    text = { Text(loc(language, "이 기록을 삭제하면 보관함과 인사이트에서도 바로 사라집니다.", "Deleting this reflection removes it from Archive and Insights immediately.")) },
                    confirmButton = {
                        Button(
                            onClick = {
                                persistEntries(entries.filterNot { it.id == deleteTarget.id })
                                deleteTargetId = null
                                if (editingEntryId == deleteTarget.id) {
                                    resetEditor()
                                }
                            }
                        ) {
                            Text(loc(language, "삭제", "Delete"))
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { deleteTargetId = null }) {
                            Text(loc(language, "취소", "Cancel"))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun Header(language: AppLanguage, stats: JournalStats) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.74f)) {
                Text(
                    text = loc(language, "마음정리 일기", "Mind Journal"),
                    fontSize = 30.sp,
                    fontFamily = LocalMindJournalFont.current,
                    fontWeight = FontWeight.SemiBold,
                    color = BodyText
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = loc(language, "차분하게 돌아보고, 다음 행동만 남겨두는 기록장", "A calm place to reflect and leave yourself one clearer next action"),
                    color = SubText,
                    lineHeight = 21.sp
                )
            }
            PixelStickerCard(glyph = PixelArtGlyph.STAR)
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryPill(loc(language, "연속 ${stats.currentStreak}일", "${stats.currentStreak}-day streak"))
            SummaryPill(loc(language, "이번 달 ${stats.thisMonthEntries}개", "${stats.thisMonthEntries} this month"))
            SummaryPill(loc(language, "누적 ${stats.totalEntries}개", "${stats.totalEntries} total"))
        }
    }
}

@Composable
private fun HomeScreen(
    language: AppLanguage,
    stats: JournalStats,
    entries: List<Entry>,
    isDescending: Boolean,
    productState: ProductState,
    rewardAdUiState: RewardAdUiState,
    situation: String,
    feeling: String,
    lesson: String,
    isEditing: Boolean,
    editorMessage: String,
    liveCoachHint: String,
    onSituationChange: (String) -> Unit,
    onFeelingChange: (String) -> Unit,
    onLessonChange: (String) -> Unit,
    onCancelEdit: () -> Unit,
    onToggleSort: () -> Unit,
    onWatchAiAd: () -> Unit,
    onWatchTrendAd: () -> Unit,
    onWatchPdfAd: () -> Unit,
    onSave: () -> Unit,
    onEdit: (Entry) -> Unit,
    onDelete: (Entry) -> Unit
) {
    val liveDraft = remember(situation, feeling, language) {
        if (situation.isBlank() && feeling.isBlank()) null else buildSingleEntryReflection(situation, feeling, language)
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            AppCard(background = Quote) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditing) loc(language, "EDITING", "EDITING") else loc(language, "TODAY'S JOURNAL", "TODAY'S JOURNAL"),
                        color = Accent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    PixelArtIcon(glyph = PixelArtGlyph.JOURNAL, cellSize = 3.dp)
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = if (isEditing) {
                        loc(language, "기록을 차분하게 다듬고 다시 저장해보세요.", "Refine the reflection calmly and save it again.")
                    } else {
                        loc(language, "오늘, 후회되는 일이 있었나요?", "Did anything feel regretful today?", "今日は、後悔したことがありましたか？")
                    },
                    fontSize = 28.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (editorMessage.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(editorMessage, color = SubText, lineHeight = 21.sp)
                }
                Spacer(Modifier.height(18.dp))
                InputLabel(loc(language, "무슨 일이 있었나요?", "What happened?"))
                OutlinedTextField(
                    value = situation,
                    onValueChange = onSituationChange,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    shape = RoundedCornerShape(20.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
                Spacer(Modifier.height(14.dp))
                InputLabel(loc(language, "어떤 감정이 남았나요?", "What feelings stayed with you?"))
                OutlinedTextField(
                    value = feeling,
                    onValueChange = onFeelingChange,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(20.dp)
                )
                Spacer(Modifier.height(14.dp))
                InputLabel(loc(language, "다음에는 어떻게 해보고 싶나요?", "What would you like to try next time?"))
                OutlinedTextField(
                    value = lesson,
                    onValueChange = onLessonChange,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(20.dp)
                )
                if (liveDraft != null) {
                    Spacer(Modifier.height(14.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TagChip(liveDraft.tag, active = true)
                        NeutralChip(liveDraft.mentalState)
                    }
                    if (productState.aiCoachUnlocked && liveCoachHint.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        HighlightBlock(
                            label = loc(language, "AI 첨삭 가이드", "AI editing guide", "AI添削ガイド"),
                            body = liveCoachHint,
                            labelColor = Premium
                        )
                    }
                }
                Spacer(Modifier.height(18.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onSave,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = situation.isNotBlank()
                    ) {
                        Text(if (isEditing) loc(language, "수정 저장", "Save changes") else loc(language, "저장하고 회고 받기", "Save and reflect"))
                    }
                    if (isEditing) {
                        OutlinedButton(onClick = onCancelEdit, modifier = Modifier.fillMaxWidth()) {
                            Text(loc(language, "편집 취소", "Cancel edit"))
                        }
                    }
                }
            }
        }
        item {
            SectionTitle(
                eyebrow = "OVERVIEW",
                title = loc(language, "기록 흐름과 상태", "Reflection flow and status"),
                description = loc(language, "지금까지 얼마나 쌓였는지 확인하고, 아래에서 최근 기록을 정리된 순서로 볼 수 있어요.", "See how your journal is building up, then review recent reflections below in a clean order.")
            )
        }
        item { StatsRow(language = language, stats = stats) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SortChip(loc(language, "최신순", "Newest"), isDescending) { if (!isDescending) onToggleSort() }
                SortChip(loc(language, "오래된순", "Oldest"), !isDescending) { if (isDescending) onToggleSort() }
            }
        }
        if (entries.isEmpty()) {
            item {
                EmptyStateCard(
                    title = loc(language, "아직 저장된 기록이 없어요.", "No saved reflections yet."),
                    body = loc(language, "위에서 첫 번째 기록을 남기면 여기부터 차분하게 쌓이기 시작합니다.", "Once you save the first reflection above, this list will begin to fill in.")
                )
            }
        } else {
            items(entries, key = { it.id }) { entry ->
                EntryCard(
                    language = language,
                    entry = entry,
                    onEdit = { onEdit(entry) },
                    onDelete = { onDelete(entry) }
                )
            }
        }
        item {
            RewardAccessCard(
                language = language,
                productState = productState,
                rewardAdUiState = rewardAdUiState,
                onWatchAiAd = onWatchAiAd,
                onWatchTrendAd = onWatchTrendAd,
                onWatchPdfAd = onWatchPdfAd
            )
        }
    }
}

@Composable
private fun InsightsScreen(
    language: AppLanguage,
    stats: JournalStats,
    insightReport: InsightReport,
    coachProfile: CoachProfile,
    trendReport: TrendReport,
    productState: ProductState,
    rewardAdUiState: RewardAdUiState,
    onWatchAiAd: () -> Unit,
    onWatchTrendAd: () -> Unit,
    onWatchPdfAd: () -> Unit,
    onExportPdf: () -> PdfExportResult
) {
    var exportMessage by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionTitle(
            eyebrow = "INSIGHT",
            title = loc(language, "누적 흐름을 보는 화면", "Your cumulative reflection view"),
            description = loc(language, "기록이 쌓일수록 반복 패턴과 다음 행동 규칙이 더 또렷해집니다.", "As entries accumulate, recurring patterns and next-action rules become clearer.")
        )
        StatsRow(language = language, stats = stats)
        AppCard {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                PixelArtIcon(glyph = PixelArtGlyph.COMPASS, cellSize = 3.dp)
                Text(loc(language, "가장 자주 보이는 태그", "Most frequent tag"), color = Accent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(stats.topTag.ifBlank { loc(language, "아직 없음", "Not yet") }, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(stats.topMentalState.ifBlank { loc(language, "아직 없음", "Not yet") }, color = SubText, lineHeight = 21.sp)
        }
        InsightCard(loc(language, "반복 패턴", "Recurring pattern"), insightReport.pattern)
        InsightCard(loc(language, "놓치기 쉬운 감정과 욕구", "Underlying feelings and needs"), insightReport.need)
        InsightCard(loc(language, "다음 행동 제안", "Next action"), insightReport.action)
        AppCard(background = Quote) {
            Text(loc(language, "이번 주 문장", "This week's line"), color = Premium, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Spacer(Modifier.height(10.dp))
            Text(insightReport.compass, color = BodyText, fontSize = 24.sp, lineHeight = 32.sp, fontWeight = FontWeight.SemiBold)
        }

        if (productState.aiCoachUnlocked) {
            CoachProfileCard(language = language, coachProfile = coachProfile)
        } else {
            RewardLockedCard(
                language = language,
                title = loc(language, "AI 코치", "AI coach", "AIコーチ"),
                body = loc(language, "광고를 보면 AI 첨삭 지도, 글쓰기 성향, 우울 경향 참고, MBTI 추정 조언이 24시간 열립니다.", "Watch an ad to unlock AI editing guidance, writing style analysis, low-mood reference, and MBTI tendency coaching for 24 hours.", "広告を見ると、AI添削、文章傾向、抑うつ傾向の参考、MBTI推定コーチングが24時間開きます。"),
                preview = coachProfile.writingStyle,
                rewardAdUiState = rewardAdUiState,
                feature = RewardFeature.AI_COACH,
                onWatchAd = onWatchAiAd
            )
        }

        if (productState.trendUnlocked) {
            TrendOverviewCard(language = language, trendReport = trendReport)
        } else {
            RewardLockedCard(
                language = language,
                title = loc(language, "감정 그래프", "Emotion graphs", "感情グラフ"),
                body = loc(language, "광고를 보면 최근 1주일, 최근 1개월, 앞으로의 회복 흐름 그래프가 24시간 열립니다.", "Watch an ad to unlock the last 7 days, last month, and forward-looking recovery graphs for 24 hours.", "広告を見ると、直近1週間、直近1か月、これからの回復グラフが24時間開きます。"),
                preview = trendReport.summary,
                rewardAdUiState = rewardAdUiState,
                feature = RewardFeature.TREND_REPORT,
                onWatchAd = onWatchTrendAd
            )
        }

        PdfAccessCard(
            language = language,
            pdfCredits = productState.pdfExportCredits,
            rewardAdUiState = rewardAdUiState,
            onWatchPdfAd = onWatchPdfAd,
            onExportPdf = {
                val result = onExportPdf()
                exportMessage = result.message
            }
        )
        if (exportMessage.isNotBlank()) {
            Text(exportMessage, color = SubText, lineHeight = 20.sp)
        }
    }
}

@Composable
private fun ArchiveScreen(
    language: AppLanguage,
    entries: List<Entry>,
    onEdit: (Entry) -> Unit,
    onDelete: (Entry) -> Unit
) {
    var visibleMonth by rememberSaveable { mutableStateOf(entries.firstOrNull()?.dateKey?.take(7) ?: currentMonthKey()) }
    var selectedDate by rememberSaveable { mutableStateOf(entries.firstOrNull()?.dateKey ?: todayDateKey()) }
    var selectedEntryId by rememberSaveable { mutableStateOf<String?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedTag by rememberSaveable { mutableStateOf("") }

    val monthEntries = entries.filter { it.dateKey.startsWith(visibleMonth) }
    val availableTags = monthEntries.map { it.tag }.distinct()
    val filteredSelectedEntries = monthEntries
        .filter { it.dateKey == selectedDate }
        .filter { it.matchesArchiveFilter(searchQuery, selectedTag) }
    val selectedEntry = filteredSelectedEntries.firstOrNull { it.id == selectedEntryId } ?: filteredSelectedEntries.firstOrNull()
    val monthStats = computeJournalStats(monthEntries, language)

    LaunchedEffect(visibleMonth, entries.size) {
        if (monthEntries.isNotEmpty()) {
            if (monthEntries.none { it.dateKey == selectedDate }) {
                selectedDate = monthEntries.maxByOrNull { it.timestamp }?.dateKey ?: "$visibleMonth-01"
                selectedEntryId = null
            }
        } else {
            selectedDate = "$visibleMonth-01"
            selectedEntryId = null
        }
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionTitle(
            eyebrow = "ARCHIVE",
            title = loc(language, "달력으로 다시 보는 기록", "Calendar archive"),
            description = loc(language, "월을 이동하고, 날짜를 고른 뒤, 검색과 태그로 원하는 기록만 다시 꺼내볼 수 있어요.", "Move between months, choose a date, then narrow the archive with search and tag filters.")
        )
        AppCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { visibleMonth = shiftMonthKey(visibleMonth, -1) }) {
                    Icon(Icons.Outlined.ChevronLeft, null)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(formatMonthLabel(visibleMonth, language), fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                    Text(
                        loc(language, "${monthStats.thisMonthEntries}개 기록", "${monthStats.thisMonthEntries} entries"),
                        color = SubText,
                        fontSize = 12.sp
                    )
                }
                IconButton(onClick = { visibleMonth = shiftMonthKey(visibleMonth, 1) }) {
                    Icon(Icons.Outlined.ChevronRight, null)
                }
            }
            Spacer(Modifier.height(12.dp))
            CalendarMonthView(
                language = language,
                month = visibleMonth,
                entries = monthEntries,
                selectedDate = selectedDate,
                onSelectDate = {
                    selectedDate = it
                    selectedEntryId = null
                }
            )
        }
        AppCard {
            Text(loc(language, "보관함 필터", "Archive filters"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Outlined.Search, null) },
                label = { Text(loc(language, "제목, 상황, 감정 검색", "Search title, situation, or feelings")) },
                shape = RoundedCornerShape(20.dp)
            )
            if (availableTags.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SortChip(loc(language, "전체", "All"), selectedTag.isBlank()) { selectedTag = "" }
                    availableTags.forEach { tag ->
                        SortChip(tag, selectedTag == tag) { selectedTag = tag }
                    }
                }
            }
        }
        AppCard(background = Quote) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                PixelArtIcon(glyph = PixelArtGlyph.CALENDAR, cellSize = 3.dp)
                Text(formatDateLabel(selectedDate, language), fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                loc(language, "이 달의 활동일 ${monthStats.activeDays}일 · 가장 많은 태그 ${monthStats.topTag}", "${monthStats.activeDays} active days this month · top tag ${monthStats.topTag}"),
                color = SubText,
                lineHeight = 20.sp
            )
        }
        if (filteredSelectedEntries.isEmpty()) {
            EmptyStateCard(
                title = loc(language, "조건에 맞는 기록이 없어요.", "No reflections match these filters."),
                body = loc(language, "다른 날짜를 누르거나 검색어와 태그를 조정해보세요.", "Try another date or adjust the search and tag filters.")
            )
        } else {
            filteredSelectedEntries.forEach { entry ->
                CompactArchiveRow(
                    language = language,
                    entry = entry,
                    isSelected = selectedEntry?.id == entry.id,
                    onSelect = { selectedEntryId = entry.id },
                    onEdit = { onEdit(entry) },
                    onDelete = { onDelete(entry) }
                )
            }
        }
        if (selectedEntry != null) {
            EntryDetailCard(
                language = language,
                entry = selectedEntry,
                onEdit = { onEdit(selectedEntry) },
                onDelete = { onDelete(selectedEntry) }
            )
        }
    }
}

@Composable
private fun OnboardingScreen(
    selectedLanguage: AppLanguage,
    isBiometricAvailable: Boolean,
    onStart: (AppLanguage, Boolean, AppFontStyle) -> Unit
) {
    var language by rememberSaveable { mutableStateOf(selectedLanguage.name) }
    var wantsLock by rememberSaveable { mutableStateOf(false) }
    var fontStyle by rememberSaveable { mutableStateOf(AppFontStyle.SANS.name) }
    val appLanguage = AppLanguage.valueOf(language)
    val appFontStyle = AppFontStyle.valueOf(fontStyle)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(18.dp))
        Text(loc(appLanguage, "마음정리 일기", "Mind Journal"), fontSize = 32.sp, fontFamily = fontFamilyFor(appFontStyle), fontWeight = FontWeight.SemiBold)
        Text(loc(appLanguage, "처음 한 번만 간단히 맞춤 설정을 마치면 바로 기록을 시작할 수 있어요.", "A quick first-time setup and you're ready to start reflecting."), color = SubText, lineHeight = 22.sp)
        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Outlined.Translate, null, tint = Accent)
                Text(loc(appLanguage, "언어 선택", "Language"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(Modifier.height(12.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppLanguage.entries.forEach { option ->
                    SortChip(languageLabel(option), language == option.name) { language = option.name }
                }
            }
        }
        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PixelArtIcon(glyph = PixelArtGlyph.JOURNAL, cellSize = 3.dp)
                Text(loc(appLanguage, "폰트", "Font", "フォント"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                loc(appLanguage, "읽기 쉬운 무료 시스템 폰트 스타일 중 하나를 고를 수 있어요.", "Choose one readable free system font style.", "読みやすい無料システムフォント系から選べます。"),
                color = SubText,
                lineHeight = 21.sp
            )
            Spacer(Modifier.height(12.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppFontStyle.entries.forEach { option ->
                    SortChip(fontStyleLabel(option, appLanguage), appFontStyle == option) { fontStyle = option.name }
                }
            }
        }
        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Outlined.Lock, null, tint = Accent)
                Text(loc(appLanguage, "앱 잠금", "App lock"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                if (isBiometricAvailable) {
                    loc(appLanguage, "지문이나 얼굴 인식으로 일기를 보호할 수 있어요. 설정에서 언제든 다시 바꿀 수 있습니다.", "Protect your journal with fingerprint or face unlock. You can change this anytime in Settings.")
                } else {
                    loc(appLanguage, "이 기기에서는 생체인식이 없어 잠금은 나중에도 사용할 수 없어요.", "Biometrics are not available on this device, so app lock cannot be enabled later either.")
                },
                color = SubText,
                lineHeight = 21.sp
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SortChip(loc(appLanguage, "사용 안 함", "No"), !wantsLock) { wantsLock = false }
                SortChip(loc(appLanguage, "사용함", "Yes"), wantsLock) { if (isBiometricAvailable) wantsLock = true }
            }
        }
        AppCard(background = Quote) {
            Text(loc(appLanguage, "기본 흐름", "Core flow"), color = Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(Modifier.height(10.dp))
            Text(loc(appLanguage, "1. 홈에서 바로 기록하고", "1. Write directly from Home"), fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text(loc(appLanguage, "2. 인사이트에서 반복 패턴을 보고", "2. Review recurring patterns in Insights"), lineHeight = 21.sp)
            Spacer(Modifier.height(6.dp))
            Text(loc(appLanguage, "3. 보관함에서 날짜별로 다시 꺼내봅니다.", "3. Revisit any day from the Archive."), lineHeight = 21.sp)
        }
        Button(onClick = { onStart(appLanguage, wantsLock, appFontStyle) }, modifier = Modifier.fillMaxWidth()) {
            Text(loc(appLanguage, "바로 시작하기", "Start now"))
        }
    }
}

@Composable
private fun LockScreen(
    language: AppLanguage,
    isBiometricAvailable: Boolean,
    onUnlock: () -> Unit,
    onDisableLock: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        AppCard {
            Text(loc(language, "앱 잠금", "App lock"), color = Accent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(Modifier.height(12.dp))
            Text(loc(language, "기록을 열기 전에 잠금을 해제해주세요.", "Unlock before viewing your journal."), fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            Text(
                if (isBiometricAvailable) {
                    loc(language, "지문이나 얼굴 인식으로 바로 열 수 있어요.", "Use fingerprint or face unlock to continue.")
                } else {
                    loc(language, "이 기기에서는 생체인식을 사용할 수 없어서 잠금을 꺼야 앱을 열 수 있어요.", "Biometrics are not available, so you need to disable the lock to continue.")
                },
                color = SubText,
                lineHeight = 21.sp
            )
            Spacer(Modifier.height(18.dp))
            if (isBiometricAvailable) {
                Button(onClick = onUnlock, modifier = Modifier.fillMaxWidth()) {
                    Text(loc(language, "잠금 해제", "Unlock"))
                }
                Spacer(Modifier.height(10.dp))
            }
            OutlinedButton(onClick = onDisableLock, modifier = Modifier.fillMaxWidth()) {
                Text(loc(language, "잠금 끄고 열기", "Disable lock and open"))
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    language: AppLanguage,
    productState: ProductState,
    rewardAdUiState: RewardAdUiState,
    isBiometricAvailable: Boolean,
    onWatchAiAd: () -> Unit,
    onWatchTrendAd: () -> Unit,
    onWatchPdfAd: () -> Unit,
    onToggleLock: () -> Unit,
    onShowTutorial: () -> Unit,
    onChangeLanguage: (AppLanguage) -> Unit,
    onChangeFont: (AppFontStyle) -> Unit
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionTitle(
            eyebrow = "SETTINGS",
            title = loc(language, "차분하게 쓰기 위한 설정", "Settings for a calmer journal"),
            description = loc(language, "언어, 잠금, 안내를 다시 정리하고 광고로 여는 AI 기능 상태도 이곳에서 확인할 수 있어요.", "Review language, locking, guidance, and ad-unlocked AI status from one clean place.", "言語、ロック、ガイドを見直し、広告で開くAI機能の状態もここで確認できます。")
        )
        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Outlined.Translate, null, tint = Accent)
                Text(loc(language, "언어", "Language"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppLanguage.entries.forEach { option ->
                    SortChip(languageLabel(option), language == option) { onChangeLanguage(option) }
                }
            }
        }
        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PixelArtIcon(glyph = PixelArtGlyph.JOURNAL, cellSize = 3.dp)
                Text(loc(language, "폰트", "Font", "フォント"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                loc(
                    language,
                    "무료 시스템 폰트 계열을 골라 앱 전체 분위기를 바꿀 수 있어요.",
                    "Choose from free system font styles to change the feel of the whole app.",
                    "無料のシステムフォント系から選んで、アプリ全体の雰囲気を変えられます。"
                ),
                color = SubText,
                lineHeight = 21.sp
            )
            Spacer(Modifier.height(12.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppFontStyle.entries.forEach { option ->
                    SortChip(fontStyleLabel(option, language), productState.fontStyle == option) { onChangeFont(option) }
                }
            }
        }
        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Outlined.Shield, null, tint = Accent)
                Text(loc(language, "저장 보안", "Storage security"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                loc(language, "기록은 기기 안에만 저장되며, 가능한 경우 암호화된 저장소를 우선 사용합니다.", "Entries stay on-device, and encrypted storage is used whenever the device supports it."),
                color = SubText,
                lineHeight = 21.sp
            )
        }
        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Outlined.Lock, null, tint = Accent)
                Text(loc(language, "앱 잠금", "App lock"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                if (isBiometricAvailable) {
                    loc(language, "지문 또는 얼굴 인식으로 앱을 잠글 수 있어요.", "Use fingerprint or face unlock to protect the app.")
                } else {
                    loc(language, "현재 기기에서는 생체인식을 사용할 수 없어요.", "Biometrics are not available on this device.")
                },
                color = SubText,
                lineHeight = 21.sp
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onToggleLock,
                modifier = Modifier.fillMaxWidth(),
                enabled = isBiometricAvailable || productState.lockEnabled
            ) {
                Text(if (productState.lockEnabled) loc(language, "앱 잠금 끄기", "Turn off app lock") else loc(language, "앱 잠금 켜기", "Turn on app lock"))
            }
        }
        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Outlined.CalendarMonth, null, tint = Accent)
                Text(loc(language, "사용 안내", "Tutorial"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(Modifier.height(10.dp))
            Text(loc(language, "처음 안내를 다시 열어 흐름을 빠르게 복습할 수 있어요.", "Reopen the first-time guide to quickly revisit the flow."), color = SubText, lineHeight = 21.sp)
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = onShowTutorial, modifier = Modifier.fillMaxWidth()) {
                Text(loc(language, "튜토리얼 다시 보기", "View tutorial again"))
            }
        }
        RewardAccessCard(
            language = language,
            productState = productState,
            rewardAdUiState = rewardAdUiState,
            onWatchAiAd = onWatchAiAd,
            onWatchTrendAd = onWatchTrendAd,
            onWatchPdfAd = onWatchPdfAd
        )
    }
}

@Composable
private fun CalendarMonthView(
    language: AppLanguage,
    month: String,
    entries: List<Entry>,
    selectedDate: String,
    onSelectDate: (String) -> Unit
) {
    val parts = month.split("-")
    val year = parts.getOrNull(0)?.toIntOrNull() ?: currentDate().year
    val monthValue = parts.getOrNull(1)?.toIntOrNull() ?: currentDate().monthValue
    val firstDate = java.time.LocalDate.of(year, monthValue, 1)
    val offset = firstDate.dayOfWeek.value % 7
    val totalDays = firstDate.lengthOfMonth()
    val cells = buildList<String?> {
        repeat(offset) { add(null) }
        repeat(totalDays) { index ->
            add(String.format("%04d-%02d-%02d", year, monthValue, index + 1))
        }
        while (size % 7 != 0) add(null)
    }
    val entryCountByDate = entries.groupingBy { it.dateKey }.eachCount()

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            weekDayLabels(language).forEach { day ->
                Text(day, modifier = Modifier.width(38.dp), color = SubText, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                week.forEach { date ->
                    val selected = date == selectedDate
                    val count = date?.let { entryCountByDate[it] } ?: 0
                    Box(
                        modifier = Modifier
                            .width(38.dp)
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when {
                                    date == null -> Color.Transparent
                                    selected -> Accent
                                    count > 0 -> AccentSoft
                                    else -> Color.Transparent
                                }
                            )
                            .clickable(enabled = date != null) {
                                if (date != null) onSelectDate(date)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    date.takeLast(2).trimStart('0'),
                                    color = if (selected) Color.White else BodyText,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (count > 0) {
                                    Spacer(Modifier.height(3.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (selected) Color.White else Accent)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
private fun CompactArchiveRow(
    language: AppLanguage,
    entry: Entry,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    AppCard(
        background = if (isSelected) AccentSoft else SurfaceCard,
        modifier = Modifier.clickable(onClick = onSelect)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.fillMaxWidth(0.72f)) {
                Text(entry.title, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                Spacer(Modifier.height(4.dp))
                Text("${formatEntryTimestamp(entry.timestamp, language)} · ${entry.tag}", color = SubText, fontSize = 12.sp)
            }
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Outlined.Edit, null, tint = Accent) }
                IconButton(onClick = onDelete) { Icon(Icons.Outlined.DeleteOutline, null, tint = Danger) }
            }
        }
    }
}

@Composable
private fun EntryCard(
    language: AppLanguage,
    entry: Entry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    AppCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TagChip(entry.tag, true)
            Text(formatEntryTimestamp(entry.timestamp, language), color = SubText, fontSize = 12.sp)
        }
        Spacer(Modifier.height(14.dp))
        Text(entry.title, fontWeight = FontWeight.Bold, fontSize = 19.sp)
        Spacer(Modifier.height(10.dp))
        Text(entry.situation, color = SubText, lineHeight = 22.sp)
        Spacer(Modifier.height(12.dp))
        NeutralChip(entry.mentalState)
        Spacer(Modifier.height(14.dp))
        HighlightBlock(label = loc(language, "다음 행동", "Next action"), body = entry.lesson)
        Spacer(Modifier.height(12.dp))
        HighlightBlock(label = loc(language, "AI 회고", "AI reflection"), body = entry.aiReflection)
        Spacer(Modifier.height(14.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.Edit, null)
                Spacer(Modifier.width(6.dp))
                Text(loc(language, "수정", "Edit"))
            }
            OutlinedButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.DeleteOutline, null)
                Spacer(Modifier.width(6.dp))
                Text(loc(language, "삭제", "Delete"))
            }
        }
    }
}

@Composable
private fun EntryDetailCard(
    language: AppLanguage,
    entry: Entry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    AppCard {
        Text(entry.title, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(Modifier.height(8.dp))
        Text("${formatEntryTimestamp(entry.timestamp, language)} · ${entry.tag}", color = SubText, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        HighlightBlock(label = loc(language, "상황", "Situation"), body = entry.situation)
        Spacer(Modifier.height(10.dp))
        HighlightBlock(label = loc(language, "감정", "Feelings"), body = entry.feeling)
        Spacer(Modifier.height(10.dp))
        HighlightBlock(label = loc(language, "심리 상태", "Mental state"), body = entry.mentalState, labelColor = Premium)
        Spacer(Modifier.height(10.dp))
        HighlightBlock(label = loc(language, "다음 행동", "Next action"), body = entry.lesson)
        Spacer(Modifier.height(10.dp))
        HighlightBlock(label = loc(language, "AI 분석", "AI analysis"), body = entry.aiReflection)
        Spacer(Modifier.height(14.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
                Text(loc(language, "이 기록 수정", "Edit this entry"))
            }
            OutlinedButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
                Text(loc(language, "삭제", "Delete"))
            }
        }
    }
}

@Composable
private fun StatsRow(language: AppLanguage, stats: JournalStats) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        MetricCard(loc(language, "총 기록", "Total"), stats.totalEntries.toString())
        MetricCard(loc(language, "활동일", "Active days"), stats.activeDays.toString())
        MetricCard(loc(language, "연속", "Streak"), stats.currentStreak.toString())
        MetricCard(loc(language, "이번 달", "This month"), stats.thisMonthEntries.toString())
    }
}

@Composable
private fun MetricCard(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(label, color = SubText, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.SemiBold, fontSize = 22.sp)
        }
    }
}

@Composable
private fun SummaryPill(text: String) {
    Surface(shape = RoundedCornerShape(999.dp), color = SurfaceCard, border = BorderStroke(1.dp, Border)) {
        Text(text, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), fontSize = 13.sp, color = BodyText)
    }
}

@Composable
private fun InsightCard(title: String, body: String) {
    AppCard {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        Text(body, color = SubText, lineHeight = 22.sp)
    }
}

@Composable
private fun RewardAccessCard(
    language: AppLanguage,
    productState: ProductState,
    rewardAdUiState: RewardAdUiState,
    onWatchAiAd: () -> Unit,
    onWatchTrendAd: () -> Unit,
    onWatchPdfAd: () -> Unit
) {
    AppCard(background = Quote) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            PixelArtIcon(glyph = PixelArtGlyph.GEM, cellSize = 3.dp)
            Text("AD-UNLOCKED ACCESS", color = Premium, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            loc(language, "기록은 무료로 유지하고, 광고 보상으로 AI 코치와 그래프, PDF 저장권을 여는 구조예요.", "Writing stays free, and rewarded ads unlock AI coaching, graphs, and PDF export credits.", "記録は無料のまま、広告報酬でAIコーチ、グラフ、PDF保存権を開く構成です。"),
            fontWeight = FontWeight.SemiBold,
            lineHeight = 24.sp
        )
        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = Border)
        Spacer(Modifier.height(12.dp))
        RewardFeatureRow(
            language = language,
            title = loc(language, "AI 코치 24시간", "AI coach for 24 hours", "AIコーチ24時間"),
            detail = if (productState.aiCoachUnlocked) {
                remainingAccessLabel(language, productState.aiCoachUnlockedUntil)
            } else {
                loc(language, "글쓰기 첨삭, 우울감 참고, MBTI 추정 조언", "Editing guidance, low-mood reference, MBTI tendency coaching", "添削、抑うつ傾向の参考、MBTI推定コーチング")
            },
            buttonLabel = loc(language, "광고 보고 열기", "Watch ad", "広告で開く"),
            onClick = onWatchAiAd,
            enabled = !rewardAdUiState.isBusy(RewardFeature.AI_COACH)
        )
        Spacer(Modifier.height(10.dp))
        RewardFeatureRow(
            language = language,
            title = loc(language, "감정 그래프 24시간", "Emotion graphs for 24 hours", "感情グラフ24時間"),
            detail = if (productState.trendUnlocked) {
                remainingAccessLabel(language, productState.trendUnlockedUntil)
            } else {
                loc(language, "주간·월간 변화와 앞으로의 회복 흐름", "Weekly, monthly, and forward-looking recovery graphs", "週間・月間の変化とこれからの回復グラフ")
            },
            buttonLabel = loc(language, "광고 보고 열기", "Watch ad", "広告で開く"),
            onClick = onWatchTrendAd,
            enabled = !rewardAdUiState.isBusy(RewardFeature.TREND_REPORT)
        )
        Spacer(Modifier.height(10.dp))
        RewardFeatureRow(
            language = language,
            title = loc(language, "PDF 저장권", "PDF export credits", "PDF保存権"),
            detail = loc(language, "현재 ${productState.pdfExportCredits}회 사용 가능", "${productState.pdfExportCredits} exports available", "現在 ${productState.pdfExportCredits} 回使用可能"),
            buttonLabel = loc(language, "광고 보고 1회권 받기", "Earn 1 credit", "広告で1回分を獲得"),
            onClick = onWatchPdfAd,
            enabled = !rewardAdUiState.isBusy(RewardFeature.PDF_EXPORT)
        )
        if (rewardAdUiState.statusMessage.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(rewardAdUiState.statusMessage, color = SubText, lineHeight = 20.sp)
        }
    }
}

@Composable
private fun RewardLockedCard(
    language: AppLanguage,
    title: String,
    body: String,
    preview: String,
    rewardAdUiState: RewardAdUiState,
    feature: RewardFeature,
    onWatchAd: () -> Unit
) {
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Outlined.StarOutline, null, tint = Premium)
            Text(title.uppercase(), color = Premium, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
        Spacer(Modifier.height(12.dp))
        Text(body, lineHeight = 22.sp)
        Spacer(Modifier.height(14.dp))
        HighlightBlock(label = loc(language, "미리보기", "Preview"), body = preview)
        if (rewardAdUiState.statusMessage.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(rewardAdUiState.statusMessage, color = SubText, lineHeight = 20.sp)
        }
        Spacer(Modifier.height(14.dp))
        Button(onClick = onWatchAd, modifier = Modifier.fillMaxWidth(), enabled = !rewardAdUiState.isBusy(feature)) {
            Text(loc(language, "광고 보고 열기", "Watch ad to unlock", "広告を見て開く"))
        }
    }
}

@Composable
private fun RewardFeatureRow(
    language: AppLanguage,
    title: String,
    detail: String,
    buttonLabel: String,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(detail, color = SubText, lineHeight = 19.sp)
        }
        Spacer(Modifier.width(12.dp))
        OutlinedButton(onClick = onClick, enabled = enabled) {
            Text(buttonLabel)
        }
    }
}

@Composable
private fun CoachProfileCard(language: AppLanguage, coachProfile: CoachProfile) {
    AppCard {
        Text(loc(language, "AI 코치 리포트", "AI coach report", "AIコーチレポート"), color = Premium, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Spacer(Modifier.height(12.dp))
        HighlightBlock(loc(language, "글쓰기 성향", "Writing style", "文章傾向"), coachProfile.writingStyle, Premium)
        Spacer(Modifier.height(10.dp))
        HighlightBlock(loc(language, "감정 흐름", "Mood flow", "感情の流れ"), coachProfile.moodSummary, Premium)
        Spacer(Modifier.height(10.dp))
        HighlightBlock(loc(language, "우울 경향 참고", "Low-mood reference", "抑うつ傾向の参考"), coachProfile.depressionSignal, Premium)
        Spacer(Modifier.height(10.dp))
        HighlightBlock(loc(language, "MBTI 추정", "MBTI tendency", "MBTI推定"), coachProfile.mbtiGuess, Premium)
        Spacer(Modifier.height(10.dp))
        HighlightBlock(loc(language, "다음 조언", "Next advice", "次の助言"), coachProfile.advice, Premium)
    }
}

@Composable
private fun TrendOverviewCard(language: AppLanguage, trendReport: TrendReport) {
    AppCard {
        Text(loc(language, "감정 그래프", "Emotion graphs", "感情グラフ"), color = Premium, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Spacer(Modifier.height(10.dp))
        Text(trendReport.summary, color = SubText, lineHeight = 21.sp)
        Spacer(Modifier.height(14.dp))
        TrendChart(title = loc(language, "최근 1주일", "Last 7 days", "直近1週間"), points = trendReport.weeklyPoints)
        Spacer(Modifier.height(14.dp))
        TrendChart(title = loc(language, "최근 1개월", "Last month", "直近1か月"), points = trendReport.monthlyPoints)
        Spacer(Modifier.height(14.dp))
        TrendChart(title = loc(language, "앞으로의 흐름", "Forward outlook", "これからの流れ"), points = trendReport.outlookPoints)
        Spacer(Modifier.height(12.dp))
        HighlightBlock(loc(language, "앞으로 나아질 흐름", "How it may improve", "これから良くなる流れ"), trendReport.outlook, Premium)
    }
}

@Composable
private fun PdfAccessCard(
    language: AppLanguage,
    pdfCredits: Int,
    rewardAdUiState: RewardAdUiState,
    onWatchPdfAd: () -> Unit,
    onExportPdf: () -> Unit
) {
    AppCard(background = Quote) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(loc(language, "PDF 리포트", "PDF report", "PDFレポート"), color = Premium, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Spacer(Modifier.height(6.dp))
                Text(loc(language, "현재 저장권 ${pdfCredits}개", "${pdfCredits} export credits available", "現在の保存権 ${pdfCredits} 回"), lineHeight = 21.sp)
            }
            PixelArtIcon(glyph = PixelArtGlyph.CALENDAR, cellSize = 3.dp)
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onWatchPdfAd, enabled = !rewardAdUiState.isBusy(RewardFeature.PDF_EXPORT), modifier = Modifier.weight(1f)) {
                Text(loc(language, "광고 보고 1회권 받기", "Earn 1 credit", "広告で1回分を獲得"))
            }
            OutlinedButton(onClick = onExportPdf, enabled = pdfCredits > 0, modifier = Modifier.weight(1f)) {
                Text(loc(language, "PDF 저장", "Export PDF", "PDF保存"))
            }
        }
    }
}

@Composable
private fun TrendChart(title: String, points: List<TrendPoint>) {
    Column {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        Spacer(Modifier.height(10.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceMuted)
                .padding(14.dp)
        ) {
            if (points.isEmpty()) return@Canvas
            val width = size.width
            val height = size.height
            val stepX = if (points.size == 1) 0f else width / (points.size - 1)
            for (index in 0..3) {
                val y = height * (index / 3f)
                drawLine(color = Border, start = Offset(0f, y), end = Offset(width, y), strokeWidth = 1f)
            }
            val path = Path()
            points.forEachIndexed { index, point ->
                val x = stepX * index
                val y = height - ((point.score / 100f) * height)
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path = path, color = Premium, style = Stroke(width = 4f, cap = StrokeCap.Round))
            points.forEachIndexed { index, point ->
                val x = stepX * index
                val y = height - ((point.score / 100f) * height)
                drawCircle(color = Color.White, radius = 7f, center = Offset(x, y))
                drawCircle(color = Premium, radius = 4f, center = Offset(x, y))
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            points.forEach { point ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(point.label, color = SubText, fontSize = 11.sp)
                    Text(point.score.toInt().toString(), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            }
        }
    }
}

private fun remainingAccessLabel(language: AppLanguage, unlockedUntil: Long): String {
    if (unlockedUntil == Long.MAX_VALUE) {
        return loc(language, "영구 활성화", "Permanently active", "常時有効")
    }
    val remainingMinutes = ((unlockedUntil - System.currentTimeMillis()) / 60_000L).coerceAtLeast(0L)
    val hours = remainingMinutes / 60L
    val minutes = remainingMinutes % 60L
    return when {
        remainingMinutes <= 0L -> loc(language, "잠금 해제 시간이 끝났어요", "Unlock time has ended", "解放時間が終了しました")
        hours > 0L -> loc(language, "남은 시간 ${hours}시간 ${minutes}분", "${hours}h ${minutes}m remaining", "残り ${hours}時間 ${minutes}分")
        else -> loc(language, "남은 시간 ${minutes}분", "${minutes}m remaining", "残り ${minutes}分")
    }
}

@Composable
private fun HighlightBlock(label: String, body: String, labelColor: Color = Accent) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceMuted)
            .padding(14.dp)
    ) {
        Text(label, color = labelColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Spacer(Modifier.height(8.dp))
        Text(body, lineHeight = 21.sp)
    }
}

@Composable
private fun EmptyStateCard(title: String, body: String) {
    AppCard {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            PixelArtIcon(glyph = PixelArtGlyph.CLOUD, cellSize = 3.dp)
            Text(title, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Text(body, color = SubText, lineHeight = 21.sp)
    }
}

@Composable
private fun SectionTitle(eyebrow: String, title: String, description: String) {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            PixelArtDot()
            Text(eyebrow, color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(6.dp))
        Text(title, fontSize = 24.sp, lineHeight = 31.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(description, color = SubText, lineHeight = 21.sp)
    }
}

@Composable
private fun SortChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (selected) Accent else SurfaceCard,
        contentColor = if (selected) Color.White else BodyText,
        border = BorderStroke(1.dp, if (selected) Accent else Border),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun TagChip(label: String, active: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (active) AccentSoft else SurfaceMuted)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(label, color = if (active) Accent else SubText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun NeutralChip(label: String) {
    Surface(shape = RoundedCornerShape(999.dp), color = SurfaceCard, border = BorderStroke(1.dp, Border)) {
        Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = BodyText, fontSize = 12.sp)
    }
}

@Composable
private fun InputLabel(text: String) {
    Text(text, color = BodyText, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun AppCard(
    modifier: Modifier = Modifier,
    background: Color = SurfaceCard,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(20.dp), content = content)
    }
}

private fun Entry.matchesArchiveFilter(query: String, tag: String): Boolean {
    val matchesQuery = query.isBlank() || listOf(title, situation, feeling, lesson, aiReflection)
        .any { it.contains(query, ignoreCase = true) }
    val matchesTag = tag.isBlank() || this.tag == tag
    return matchesQuery && matchesTag
}

private enum class PixelArtGlyph { STAR, CLOUD, JOURNAL, COMPASS, CALENDAR, GEM }

@Composable
private fun PixelStickerCard(glyph: PixelArtGlyph) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, Border)
    ) {
        Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
            PixelArtIcon(glyph = glyph, cellSize = 4.dp)
        }
    }
}

@Composable
private fun PixelArtDot() {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Accent)
    )
}

@Composable
private fun PixelArtIcon(glyph: PixelArtGlyph, cellSize: Dp) {
    val pixels = pixelGlyphData(glyph)
    Column {
        pixels.forEach { row ->
            Row {
                row.forEach { key ->
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .background(pixelColor(key))
                    )
                }
            }
        }
    }
}

private fun pixelGlyphData(glyph: PixelArtGlyph): List<String> {
    return when (glyph) {
        PixelArtGlyph.STAR -> listOf(
            "..yy..",
            ".yyyy.",
            "yyooyy",
            ".yyyy.",
            "..yy..",
            ".y..y."
        )
        PixelArtGlyph.CLOUD -> listOf(
            "..bb..",
            ".bwwb.",
            "bwwwwb",
            "bwwwwb",
            ".boob.",
            "..bb.."
        )
        PixelArtGlyph.JOURNAL -> listOf(
            "oooooo",
            "owwwpo",
            "owwwpo",
            "owwwpo",
            "owwwpo",
            "oooooo"
        )
        PixelArtGlyph.COMPASS -> listOf(
            "..pp..",
            ".pppp.",
            "pporpp",
            "poorpp",
            ".pppp.",
            "..pp.."
        )
        PixelArtGlyph.CALENDAR -> listOf(
            "orrrro",
            "owwwwo",
            "owggwo",
            "owwwwo",
            "owwwwo",
            "oooooo"
        )
        PixelArtGlyph.GEM -> listOf(
            "..pp..",
            ".prrp.",
            "prrrrp",
            ".prrr.",
            "..pp..",
            "..oo.."
        )
    }
}

private fun pixelColor(key: Char): Color {
    return when (key) {
        'o' -> Color(0xFF5A514C)
        'y' -> Color(0xFFF0C95D)
        'b' -> Color(0xFFBFD8E8)
        'w' -> Color(0xFFFDFBF6)
        'p' -> Color(0xFF8A7AE8)
        'r' -> Color(0xFFE97A68)
        'g' -> Color(0xFF7EB67A)
        else -> Color.Transparent
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewApp() {
    MindJournalTheme {
        MindJournalApp()
    }
}
