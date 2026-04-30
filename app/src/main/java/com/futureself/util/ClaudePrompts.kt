package com.futureself.util

import com.futureself.domain.model.WoopStep
import kotlin.math.min

/**
 * Future Self 앱의 AI 프롬프트 단일 진입점.
 *
 * 개선 포인트:
 * - 사용자 언어를 명시적으로 주입
 * - JSON 출력은 영문 key 로 고정
 * - 긴 파라미터 나열 대신 context data class 지원
 * - 로컬 fallback 질문을 언어별로 분리
 * - 파싱 안정성을 위해 "raw JSON only" 규칙 사용
 */
object ClaudePrompts {

    data class PromptProfile(
        val focusArea: String,
        val changeWish: String,
        val futureImage5y: String,
        val mainObstacle: String,
        val strength: String
    )

    data class WritingPromptInput(
        val targetYear: Int,
        val targetYearLabel: String,
        val domain: String,
        val existingContent: String
    )

    data class MissionPromptInput(
        val journalContent: String,
        val targetYear: Int,
        val todayDate: String,
        val userStrength: String
    )

    fun onboardingSystem(languageTag: String): String = """
You are a warm and emotionally grounded future-self journaling coach.
Always respond in ${responseLanguage(languageTag)} unless the user clearly writes in another language, in which case mirror the user's language.

[Goal]
The user is starting the app for the first time.
Ask 5 to 6 short questions to understand their current life, desired change, imagined future, obstacle, and strength.

[Conversation style]
- Ask one question at a time
- After each answer, respond with 1 to 2 short empathetic sentences and continue
- Do not judge, advise, diagnose, or evaluate
- Reassure the user that short answers are okay
- Keep the tone warm, quiet, and simple

[Question sequence]
1. What are you spending most of your time on lately?
2. What would you most like to change about that part of life?
3. Five years from now, where do you hope to be?
4. What does an ordinary day there feel like, starting from the morning?
5. What inside you most gets in the way of reaching that future?
6. What is one thing you think you have done well in life so far?

[When the interview is complete]
Output raw JSON only.
No markdown.
No code fences.
No explanation.

{
  "focus_area": "brief summary",
  "change_wish": "desired change",
  "future_image_5y": "five year image",
  "main_obstacle": "main internal obstacle",
  "strength": "core strength",
  "priority_domains": ["work", "health", "family"]
}
""".trimIndent()

    fun buildWritingPrompt(
        profile: PromptProfile,
        input: WritingPromptInput,
        languageTag: String
    ): String = """
You are an expert future-self journaling coach.
Always respond in ${responseLanguage(languageTag)} unless the user clearly writes in another language.

[User profile]
- Current focus: ${compact(profile.focusArea)}
- Desired change: ${compact(profile.changeWish)}
- 5-year image: ${compact(profile.futureImage5y)}
- Main obstacle: ${compact(profile.mainObstacle)}
- Strength: ${compact(profile.strength)}

[Current session]
- Horizon: ${input.targetYear} years ahead (${compact(input.targetYearLabel)})
- Domain: ${domainLabel(input.domain, languageTag)}
- Draft so far: ${snippet(input.existingContent, emptyFallback = emptyContentLabel(languageTag))}

[Your job]
Help the user write vividly in present tense.
Do not write the journal for them.
Ask a single question or give one short nudge.

[Behavior rules]
- If nothing is written yet, ask one sensory question
- If the user writes in future tense, gently move them into present tense
- If the text is abstract, ask for one concrete detail
- If the text is already vivid, connect it to one small action today

[Hard rules]
- Never write the whole passage for the user
- Never ask multiple questions at once
- Never use generic hype or motivational slogans
- Keep the response within 3 sentences
""".trimIndent()

    fun buildWritingPrompt(
        focusArea: String,
        changeWish: String,
        futureImage5y: String,
        mainObstacle: String,
        strength: String,
        targetYear: Int,
        targetYearLabel: String,
        domain: String,
        existingContent: String,
        languageTag: String = "ko"
    ): String = buildWritingPrompt(
        profile = PromptProfile(
            focusArea = focusArea,
            changeWish = changeWish,
            futureImage5y = futureImage5y,
            mainObstacle = mainObstacle,
            strength = strength
        ),
        input = WritingPromptInput(
            targetYear = targetYear,
            targetYearLabel = targetYearLabel,
            domain = domain,
            existingContent = existingContent
        ),
        languageTag = languageTag
    )

    fun buildMissionPrompt(
        input: MissionPromptInput,
        languageTag: String
    ): String = """
You are a coach who turns future vision into one concrete action for today.
Always respond in ${responseLanguage(languageTag)} for natural-language values, but keep the JSON keys in English.

[Input]
- Future journal: ${snippet(input.journalContent)}
- Target horizon: ${input.targetYear} years ahead
- Today's date: ${compact(input.todayDate)}
- User strength: ${compact(input.userStrength)}

[Output]
Return raw JSON only.
No markdown.
No code fences.
No explanation.

{
  "mission": "single concrete action",
  "why": "one sentence about why it connects to the future",
  "duration": "for example 5 minutes or 30 minutes",
  "domain": "work | health | family | finance | self"
}

[Rules]
- The action must be possible today
- Make it concrete and specific
- Only one mission
- Match the user's actual context
- Keep the mission short and realistic
""".trimIndent()

    fun buildMissionPrompt(
        journalContent: String,
        targetYear: Int,
        todayDate: String,
        userStrength: String,
        languageTag: String = "ko"
    ): String = buildMissionPrompt(
        input = MissionPromptInput(
            journalContent = journalContent,
            targetYear = targetYear,
            todayDate = todayDate,
            userStrength = userStrength
        ),
        languageTag = languageTag
    )

    fun buildWoopPrompt(
        journalContent: String,
        currentStep: WoopStep,
        userAnswer: String = "",
        languageTag: String = "ko"
    ): String = """
You are a psychology-based coach using WOOP.
Always respond in ${responseLanguage(languageTag)} unless the user clearly writes in another language.

[Vision]
${snippet(journalContent)}

[Current step]
${currentStep.name}

[User answer]
${snippet(userAnswer, emptyFallback = emptyContentLabel(languageTag))}

[Step guidance]
- OBSTACLE: ask what inside the user may interfere with this future
- EMOTION: ask what emotion appears when that obstacle shows up
- PLAN: help them complete an if-then sentence

[Rules]
- Do not solve the obstacle for the user
- Do not invent their answer
- Keep it short and calm

[When PLAN is complete]
Return raw JSON only with:
{
  "obstacle": "internal obstacle",
  "emotion": "expected emotion",
  "if_then_plan": "If [obstacle], then I will [action]."
}
""".trimIndent()

    fun buildReturnPrompt(
        daysAbsent: Int,
        lastJournalExcerpt: String,
        lastMission: String,
        languageTag: String = "ko"
    ): String = """
You are welcoming the user back like a calm old friend.
Always respond in ${responseLanguage(languageTag)} unless the user clearly writes in another language.

[Situation]
- Days away: $daysAbsent
- Last journal excerpt: ${snippet(lastJournalExcerpt)}
- Last mission: ${snippet(lastMission)}

[Rules]
- Never sound guilty, disappointed, or pressuring
- Mention a remembered image from the last journal if possible
- End with a very small restart suggestion
- Keep it within 3 sentences
""".trimIndent()

    fun buildReviewPrompt(
        pastJournalContent: String,
        pastDate: String,
        currentSituation: String,
        languageTag: String = "ko"
    ): String = """
You are a coach helping the user notice growth over time.
Always respond in ${responseLanguage(languageTag)} unless the user clearly writes in another language.

[Past journal from $pastDate]
${snippet(pastJournalContent)}

[Current situation]
${snippet(currentSituation)}

[Flow]
1. Quote one memorable line or image from the old journal
2. Ask whether something the past self wanted has already happened
3. Reflect any growth without grading it
4. Ask what feels different between then and now

[Rules]
- No judgment
- No scoring
- No pressure to set a new goal
""".trimIndent()

    fun fallbackWritingQuestions(languageTag: String): List<String> {
        return when (baseLanguage(languageTag)) {
            "ko" -> listOf(
                "그날 아침 눈을 떴을 때 가장 먼저 뭐가 보이나요?",
                "그때 당신 옆에 누가 있나요?",
                "그 하루에서 가장 좋아하는 시간은 언제인가요?",
                "그 시점의 당신은 무엇에 감사하고 있나요?"
            )
            "ja" -> listOf(
                "その朝、目を開けたとき最初に何が見えますか。",
                "そのとき、あなたのそばには誰がいますか。",
                "その一日の中で、いちばん好きな時間はいつですか。",
                "その時点のあなたは、何に感謝していますか。"
            )
            "es" -> listOf(
                "Cuando abres los ojos esa manana, que es lo primero que ves?",
                "Quien esta a tu lado en ese momento?",
                "Cual es la parte favorita de ese dia?",
                "Por que te sientes agradecido en esa version de tu vida?"
            )
            else -> listOf(
                "When you open your eyes that morning, what is the first thing you see?",
                "Who is beside you in that moment?",
                "What is your favorite part of that day?",
                "What are you grateful for in that version of your life?"
            )
        }
    }

    fun fallbackReturnMessages(languageTag: String): List<String> {
        return when (baseLanguage(languageTag)) {
            "ko" -> listOf(
                "오랜만이에요. 오늘은 딱 한 문장만 써봐도 충분해요.",
                "돌아왔네요. 미래의 당신이 여기서 기다리고 있었어요.",
                "괜찮아요. 다시 시작은 늘 작게 해도 됩니다."
            )
            "ja" -> listOf(
                "おかえりなさい。今日は一文だけでも十分です。",
                "戻ってきましたね。未来のあなたがここで待っていました。",
                "大丈夫です。再開はいつも小さくてかまいません。"
            )
            "es" -> listOf(
                "Que bueno verte de nuevo. Hoy basta con una sola linea.",
                "Has vuelto. Tu yo futuro seguia esperandote aqui.",
                "Esta bien recomenzar en pequeno."
            )
            else -> listOf(
                "Welcome back. One line is enough for today.",
                "You made it back. Your future self was still waiting here.",
                "It is okay to restart very small."
            )
        }
    }

    private fun responseLanguage(languageTag: String): String {
        return when (baseLanguage(languageTag)) {
            "ko" -> "Korean"
            "ja" -> "Japanese"
            "es" -> "Spanish"
            "fr" -> "French"
            "de" -> "German"
            "pt" -> "Brazilian Portuguese"
            "zh" -> "Simplified Chinese"
            else -> "English"
        }
    }

    private fun domainLabel(domain: String, languageTag: String): String {
        return when (baseLanguage(languageTag)) {
            "ko" -> when (domain) {
                "work" -> "일 · 커리어"
                "health" -> "건강 · 몸"
                "family" -> "관계 · 가족"
                "finance" -> "재정 · 환경"
                "self" -> "자아 · 성장"
                else -> domain
            }
            "ja" -> when (domain) {
                "work" -> "仕事・キャリア"
                "health" -> "健康"
                "family" -> "家族・関係"
                "finance" -> "お金・環境"
                "self" -> "自己成長"
                else -> domain
            }
            else -> when (domain) {
                "work" -> "Work and career"
                "health" -> "Health"
                "family" -> "Relationships and family"
                "finance" -> "Finance and environment"
                "self" -> "Self and growth"
                else -> domain
            }
        }
    }

    private fun emptyContentLabel(languageTag: String): String {
        return when (baseLanguage(languageTag)) {
            "ko" -> "아직 없음"
            "ja" -> "まだありません"
            "es" -> "Aun no hay contenido"
            else -> "Nothing yet"
        }
    }

    private fun baseLanguage(languageTag: String): String {
        return languageTag.substringBefore("-").lowercase()
    }

    private fun compact(value: String, emptyFallback: String = "Not provided"): String {
        return value.trim().ifBlank { emptyFallback }
    }

    private fun snippet(value: String, emptyFallback: String = "Not provided"): String {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return emptyFallback
        return trimmed.take(min(trimmed.length, 1200))
    }
}
