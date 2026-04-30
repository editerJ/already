package com.regretdiary.app

import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val storageDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val storageMonthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

fun loc(language: AppLanguage, ko: String, en: String): String {
    return when (language) {
        AppLanguage.KO -> ko
        AppLanguage.EN -> en
        AppLanguage.JA -> japaneseCopy[ko] ?: en
    }
}

fun localeFor(language: AppLanguage): Locale {
    return when (language) {
        AppLanguage.KO -> Locale.KOREAN
        AppLanguage.EN -> Locale.ENGLISH
        AppLanguage.JA -> Locale.JAPANESE
    }
}

fun zoneId(): ZoneId = ZoneId.systemDefault()

fun Long.toDateKey(): String {
    return Instant.ofEpochMilli(this)
        .atZone(zoneId())
        .toLocalDate()
        .format(storageDateFormatter)
}

fun currentDate(): LocalDate = LocalDate.now(zoneId())

fun todayDateKey(): String = currentDate().format(storageDateFormatter)

fun currentMonthKey(): String = YearMonth.now(zoneId()).format(storageMonthFormatter)

fun shiftMonthKey(monthKey: String, deltaMonths: Long): String {
    val yearMonth = runCatching { YearMonth.parse(monthKey, storageMonthFormatter) }
        .getOrDefault(YearMonth.now(zoneId()))
    return yearMonth.plusMonths(deltaMonths).format(storageMonthFormatter)
}

fun formatEntryTimestamp(timestamp: Long, language: AppLanguage): String {
    val formatter = if (language == AppLanguage.KO) {
        DateTimeFormatter.ofPattern("M월 d일 HH:mm", localeFor(language))
    } else if (language == AppLanguage.JA) {
        DateTimeFormatter.ofPattern("M月d日 HH:mm", localeFor(language))
    } else {
        DateTimeFormatter.ofPattern("MMM d, HH:mm", localeFor(language))
    }
    return Instant.ofEpochMilli(timestamp).atZone(zoneId()).format(formatter)
}

fun formatDateLabel(dateKey: String, language: AppLanguage): String {
    val date = runCatching { LocalDate.parse(dateKey, storageDateFormatter) }
        .getOrDefault(currentDate())
    val formatter = if (language == AppLanguage.KO) {
        DateTimeFormatter.ofPattern("yyyy년 M월 d일", localeFor(language))
    } else if (language == AppLanguage.JA) {
        DateTimeFormatter.ofPattern("yyyy年M月d日", localeFor(language))
    } else {
        DateTimeFormatter.ofPattern("MMMM d, yyyy", localeFor(language))
    }
    return date.format(formatter)
}

fun formatMonthLabel(monthKey: String, language: AppLanguage): String {
    val month = runCatching { YearMonth.parse(monthKey, storageMonthFormatter) }
        .getOrDefault(YearMonth.now(zoneId()))
    val formatter = if (language == AppLanguage.KO) {
        DateTimeFormatter.ofPattern("yyyy년 M월", localeFor(language))
    } else if (language == AppLanguage.JA) {
        DateTimeFormatter.ofPattern("yyyy年M月", localeFor(language))
    } else {
        DateTimeFormatter.ofPattern("MMMM yyyy", localeFor(language))
    }
    return month.format(formatter)
}

fun weekDayLabels(language: AppLanguage): List<String> {
    return when (language) {
        AppLanguage.KO -> listOf("일", "월", "화", "수", "목", "금", "토")
        AppLanguage.JA -> listOf("日", "月", "火", "水", "木", "金", "土")
        AppLanguage.EN -> listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    }
}

fun languageLabel(language: AppLanguage): String {
    return when (language) {
        AppLanguage.KO -> "한국어"
        AppLanguage.EN -> "English"
        AppLanguage.JA -> "日本語"
    }
}

fun fontStyleLabel(fontStyle: AppFontStyle, language: AppLanguage): String {
    return when (fontStyle) {
        AppFontStyle.SANS -> loc(language, "기본 산세리프", "Clean sans", "すっきりサンセリフ")
        AppFontStyle.SERIF -> loc(language, "차분한 세리프", "Calm serif", "落ち着いたセリフ")
        AppFontStyle.MONO -> loc(language, "픽셀 모노", "Pixel mono", "ピクセル風モノ")
        AppFontStyle.ROUNDED -> loc(language, "부드러운 라운드", "Soft round", "やわらか丸ゴシック")
    }
}

fun loc(language: AppLanguage, ko: String, en: String, ja: String): String {
    return when (language) {
        AppLanguage.KO -> ko
        AppLanguage.EN -> en
        AppLanguage.JA -> ja
    }
}

private val japaneseCopy = mapOf(
    "마음정리 일기" to "こころ整理日記",
    "차분하게 돌아보고, 다음 행동만 남겨두는 기록장" to "静かに振り返り、次の一歩だけを残す日記",
    "홈" to "ホーム",
    "인사이트" to "インサイト",
    "보관함" to "アーカイブ",
    "설정" to "設定",
    "무슨 일이 있었나요?" to "何がありましたか？",
    "어떤 감정이 남았나요?" to "どんな感情が残りましたか？",
    "다음에는 어떻게 해보고 싶나요?" to "次はどうしてみたいですか？",
    "저장하고 회고 받기" to "保存して振り返る",
    "수정 저장" to "変更を保存",
    "편집 취소" to "編集をキャンセル",
    "최신순" to "新しい順",
    "오래된순" to "古い順",
    "아직 저장된 기록이 없어요." to "まだ保存された記録がありません。",
    "기록 흐름과 상태" to "記録の流れと状態",
    "누적 흐름을 보는 화면" to "積み重なった流れを見る画面",
    "가장 자주 보이는 태그" to "よく出るタグ",
    "반복 패턴" to "繰り返しのパターン",
    "놓치기 쉬운 감정과 욕구" to "見落としやすい感情とニーズ",
    "다음 행동 제안" to "次の行動の提案",
    "이번 주 문장" to "今週の一文",
    "PDF 리포트 저장" to "PDFレポートを保存",
    "달력으로 다시 보는 기록" to "カレンダーで見返す記録",
    "보관함 필터" to "アーカイブフィルター",
    "제목, 상황, 감정 검색" to "タイトル、状況、感情を検索",
    "전체" to "すべて",
    "조건에 맞는 기록이 없어요." to "条件に合う記録がありません。",
    "차분하게 쓰기 위한 설정" to "落ち着いて書くための設定",
    "언어" to "言語",
    "폰트" to "フォント",
    "저장 보안" to "保存セキュリティ",
    "앱 잠금" to "アプリロック",
    "사용 안내" to "使い方",
    "튜토리얼 다시 보기" to "チュートリアルをもう一度見る",
    "프리미엄 구매" to "プレミアムを購入",
    "구매 복원" to "購入を復元",
    "복원" to "復元",
    "기록 삭제" to "記録を削除",
    "삭제" to "削除",
    "취소" to "キャンセル",
    "수정" to "編集",
    "이 기록 수정" to "この記録を編集",
    "다음 행동" to "次の行動",
    "AI 회고" to "AI振り返り",
    "상황" to "状況",
    "감정" to "感情",
    "심리 상태" to "心理状態",
    "AI 분석" to "AI分析",
    "프리미엄에서 심리 상태 분석 제공" to "心理状態分析はプレミアムで利用できます",
    "복합적인 감정" to "複雑な感情",
    "감정" to "感情",
    "말실수" to "言葉のすれ違い",
    "미루기" to "先延ばし",
    "관계" to "関係",
    "불안-경계 상태" to "不安で警戒している状態",
    "죄책감-자기비판 상태" to "罪悪感と自己批判の状態",
    "과민-방어 상태" to "過敏で防衛的な状態",
    "회피-스트레스 상태" to "回避とストレスの状態",
    "정서 혼재 상태" to "感情が混ざった状態",
    "아직 없음" to "まだありません",
    "기록 대기" to "記録待ち"
)
