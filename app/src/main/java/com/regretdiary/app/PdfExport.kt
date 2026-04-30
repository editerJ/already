package com.regretdiary.app

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter

fun exportInsightPdf(
    context: Context,
    insightReport: InsightReport,
    coachProfile: CoachProfile,
    trendReport: TrendReport,
    entries: List<Entry>,
    stats: JournalStats,
    language: AppLanguage
): PdfExportResult {
    return runCatching {
        val docsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        if (!docsDir.exists()) {
            docsDir.mkdirs()
        }

        val timestamp = Instant.ofEpochMilli(System.currentTimeMillis())
            .atZone(zoneId())
            .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"))
        val file = File(docsDir, "mind-journal-report-$timestamp.pdf")
        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 42f
        val contentWidth = pageWidth - (margin * 2)
        val pageBottom = pageHeight - 48f
        val titlePaint = Paint().apply {
            textSize = 20f
            isFakeBoldText = true
        }
        val sectionPaint = Paint().apply {
            textSize = 14f
            isFakeBoldText = true
        }
        val bodyPaint = Paint().apply { textSize = 11.5f }
        val metaPaint = Paint().apply {
            textSize = 10f
            color = 0xFF5B655F.toInt()
        }

        var pageNumber = 0
        lateinit var page: PdfDocument.Page
        lateinit var canvas: android.graphics.Canvas
        var y = margin

        fun startPage() {
            if (pageNumber > 0) {
                document.finishPage(page)
            }
            pageNumber += 1
            page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
            canvas = page.canvas
            y = margin
        }

        fun ensureSpace(requiredHeight: Float) {
            if (pageNumber == 0 || y + requiredHeight > pageBottom) {
                startPage()
            }
        }

        fun wrapText(text: String, paint: Paint, maxWidth: Float, prefix: String = ""): List<String> {
            if (text.isBlank()) return listOf(prefix)
            val words = text.replace("\n", " ").split(" ")
            val lines = mutableListOf<String>()
            var current = prefix
            words.forEach { word ->
                val candidate = if (current.isBlank()) word else "$current $word"
                if (paint.measureText(candidate) <= maxWidth) {
                    current = candidate
                } else {
                    if (current.isNotBlank()) {
                        lines += current
                    }
                    current = if (prefix.isNotBlank()) "$prefix$word" else word
                }
            }
            if (current.isNotBlank()) {
                lines += current
            }
            return lines.ifEmpty { listOf(prefix) }
        }

        fun drawWrapped(text: String, paint: Paint, lineHeight: Float = 18f, prefix: String = "") {
            val lines = wrapText(text, paint, contentWidth, prefix)
            ensureSpace(lines.size * lineHeight + 4f)
            lines.forEach { line ->
                canvas.drawText(line, margin, y, paint)
                y += lineHeight
            }
        }

        fun drawSection(title: String, body: String) {
            ensureSpace(26f)
            canvas.drawText(title, margin, y, sectionPaint)
            y += 18f
            drawWrapped(body, bodyPaint)
            y += 10f
        }

        startPage()
        canvas.drawText(loc(language, "마음정리 일기 분석 리포트", "Mind Journal Analysis Report"), margin, y, titlePaint)
        y += 20f
        drawWrapped(
            loc(
                language,
                "생성일: ${formatEntryTimestamp(System.currentTimeMillis(), language)}",
                "Generated: ${formatEntryTimestamp(System.currentTimeMillis(), language)}"
            ),
            metaPaint,
            lineHeight = 16f
        )
        y += 8f
        drawSection(
            loc(language, "요약 지표", "Summary stats"),
            loc(
                language,
                "총 ${stats.totalEntries}개 기록, 활동일 ${stats.activeDays}일, 현재 연속 ${stats.currentStreak}일, 이번 달 ${stats.thisMonthEntries}개, 가장 많은 태그 ${stats.topTag}",
                "${stats.totalEntries} entries, ${stats.activeDays} active days, ${stats.currentStreak}-day streak, ${stats.thisMonthEntries} this month, top tag ${stats.topTag}"
            )
        )
        drawSection(loc(language, "반복 패턴", "Recurring pattern"), insightReport.pattern)
        drawSection(loc(language, "감정과 욕구", "Feelings and needs"), insightReport.need)
        drawSection(loc(language, "다음 행동", "Next action"), insightReport.action)
        drawSection(loc(language, "이번 주 문장", "This week's line"), insightReport.compass)
        drawSection(loc(language, "AI 코치: 글쓰기 성향", "AI coach: writing style"), coachProfile.writingStyle)
        drawSection(loc(language, "AI 코치: 감정 흐름", "AI coach: mood flow"), coachProfile.moodSummary)
        drawSection(loc(language, "AI 코치: 우울 신호", "AI coach: low-mood signal"), coachProfile.depressionSignal)
        drawSection(loc(language, "AI 코치: MBTI 추정", "AI coach: MBTI tendency"), coachProfile.mbtiGuess)
        drawSection(loc(language, "AI 코치: 다음 조언", "AI coach: next advice"), coachProfile.advice)
        drawSection(loc(language, "감정 그래프 요약", "Emotion graph summary"), trendReport.summary)
        drawSection(loc(language, "앞으로의 흐름", "Forward outlook"), trendReport.outlook)
        drawSection(
            loc(language, "주간 감정 점수", "Weekly emotion scores"),
            trendReport.weeklyPoints.joinToString("  ") { "${it.label} ${it.score.toInt()}" }
        )
        drawSection(
            loc(language, "월간 감정 점수", "Monthly emotion scores"),
            trendReport.monthlyPoints.joinToString("  ") { "${it.label} ${it.score.toInt()}" }
        )
        drawSection(
            loc(language, "예상 회복 곡선", "Forecast recovery curve"),
            trendReport.outlookPoints.joinToString("  ") { "${it.label} ${it.score.toInt()}" }
        )
        ensureSpace(20f)
        canvas.drawText(loc(language, "최근 기록", "Recent entries"), margin, y, sectionPaint)
        y += 18f

        entries.take(10).forEachIndexed { index, entry ->
            drawWrapped("${index + 1}. ${entry.title}", sectionPaint)
            drawWrapped(loc(language, "시간: ${formatEntryTimestamp(entry.timestamp, language)}", "Time: ${formatEntryTimestamp(entry.timestamp, language)}"), metaPaint, lineHeight = 15f)
            drawWrapped(loc(language, "상황: ${entry.situation}", "Situation: ${entry.situation}"), bodyPaint)
            drawWrapped(loc(language, "감정: ${entry.feeling}", "Feelings: ${entry.feeling}"), bodyPaint)
            drawWrapped(loc(language, "다음 행동: ${entry.lesson}", "Next action: ${entry.lesson}"), bodyPaint)
            y += 8f
        }

        document.finishPage(page)
        file.outputStream().use { output -> document.writeTo(output) }
        document.close()
        PdfExportResult(
            success = true,
            message = loc(language, "PDF 저장 완료: ${file.absolutePath}", "PDF saved: ${file.absolutePath}"),
            path = file.absolutePath
        )
    }.getOrElse {
        PdfExportResult(
            success = false,
            message = loc(language, "PDF 저장에 실패했어요.", "Failed to save the PDF.")
        )
    }
}
