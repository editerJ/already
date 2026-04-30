package com.regretdiary.app

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.math.abs

fun createEntry(
    situation: String,
    feeling: String,
    lesson: String,
    aiReflection: String,
    aiCoachEnabled: Boolean,
    language: AppLanguage,
    existingId: String? = null,
    timestamp: Long = System.currentTimeMillis()
): Entry {
    val draft = buildSingleEntryReflection(situation, feeling, language)
    val finalFeeling = feeling.ifBlank { loc(language, "복합적인 감정", "Mixed emotions", "複雑な感情") }
    val finalLesson = lesson.ifBlank { draft.lesson }
    val finalReflection = if (aiCoachEnabled) {
        aiReflection.ifBlank {
            loc(
                language,
                "${draft.reflection} 첨삭 팁: ${draft.rewriteSuggestion}",
                "${draft.reflection} Rewrite tip: ${draft.rewriteSuggestion}",
                "${draft.reflection} 添削ヒント: ${draft.rewriteSuggestion}"
            )
        }
    } else {
        buildBasicReflection(finalFeeling, language)
    }
    return Entry(
        id = existingId ?: UUID.randomUUID().toString(),
        title = deriveEntryTitle(situation, language),
        situation = situation.trim(),
        feeling = finalFeeling.trim(),
        mentalState = if (aiCoachEnabled) {
            draft.mentalState
        } else {
            loc(language, "AI 코치 광고를 보면 심리 상태 분석이 열려요", "Watch an AI coach ad to unlock mental-state analysis", "AIコーチ広告を見ると心理状態分析が開きます")
        },
        lesson = finalLesson.trim(),
        aiReflection = finalReflection,
        tag = draft.tag,
        timestamp = timestamp
    )
}

fun deriveEntryTitle(situation: String, language: AppLanguage): String {
    val cleaned = situation.trim().replace("\n", " ")
    return cleaned.take(24).ifBlank { loc(language, "새 후회 기록", "New reflection", "新しい振り返り") }
}

fun buildBasicReflection(feeling: String, language: AppLanguage): String {
    val emotion = feeling.ifBlank { loc(language, "복합적인 감정", "mixed emotions", "複雑な感情") }
    return loc(
        language,
        "이번 기록에는 '$emotion' 이 남아 있었어요. 지나치지 않고 적어둔 것만으로도 다음 선택을 바꾸는 시작이 됩니다.",
        "This entry still carries '$emotion'. Writing it down instead of moving past it is already the start of a better next choice.",
        "この記録には '$emotion' が残っています。書き留めたこと自体が、次の選択を変える最初の一歩です。"
    )
}

fun buildLiveCoachHint(situation: String, feeling: String, language: AppLanguage): String {
    val joined = "$situation $feeling".trim()
    if (joined.isBlank()) return ""
    return when {
        joined.length > 120 -> loc(language, "사실, 감정, 다음 행동을 1문장씩 나누면 AI 코치가 더 안정적으로 읽어줘요.", "Split facts, feelings, and the next action into separate sentences for steadier AI coaching.", "事実、感情、次の行動を1文ずつに分けると、AIコーチがより安定して読み取れます。")
        keywordCount(joined, "왜", "억울", "답답", "why", "unfair", "frustrat") > 0 -> loc(language, "해석이 먼저 올라오고 있어 보여요. 실제로 있었던 장면을 먼저 적으면 첨삭이 더 부드러워집니다.", "Interpretation seems to be arriving first. Describe the actual scene first for gentler editing guidance.", "解釈が先に来ているようです。実際の場面を先に書くと添削がよりやわらかくなります。")
        else -> loc(language, "좋아요. 감정 단어를 하나만 더 분명하게 적어도 첨삭 품질이 확 올라갑니다.", "This is a solid start. Adding one clearer feeling word would noticeably improve the coaching quality.", "よい流れです。感情語をもう1つだけはっきり書くと、添削の質がぐっと上がります。")
    }
}

fun buildSingleEntryReflection(situation: String, feeling: String, language: AppLanguage): ReflectionDraft {
    val joined = "$situation $feeling"
    val tag = when {
        keywordCount(joined, "말", "대답", "짜증", "said", "answer", "angry") > 0 -> loc(language, "말실수", "Communication", "言葉のすれ違い")
        keywordCount(joined, "연락", "미루", "답장", "reply", "avoid", "delay") > 0 -> loc(language, "미루기", "Avoidance", "先延ばし")
        keywordCount(joined, "친구", "상대", "관계", "friend", "relationship") > 0 -> loc(language, "관계", "Relationship", "関係")
        else -> loc(language, "감정", "Emotion", "感情")
    }

    val mentalState = when {
        keywordCount(joined, "불안", "긴장", "초조", "anx", "nervous") > 0 -> loc(language, "불안-경계 상태", "Anxious and guarded", "不安で警戒している状態")
        keywordCount(joined, "미안", "죄책감", "후회", "guilt", "sorry", "regret") > 0 -> loc(language, "죄책감-자기비판 상태", "Guilt and self-criticism", "罪悪感と自己批判の状態")
        keywordCount(joined, "억울", "짜증", "화", "angry", "defensive") > 0 -> loc(language, "과민-방어 상태", "Defensive and reactive", "過敏で防衛的な状態")
        keywordCount(joined, "회피", "미루", "부담", "avoid", "burden") > 0 -> loc(language, "회피-스트레스 상태", "Avoidant and stressed", "回避とストレスの状態")
        else -> loc(language, "정서 혼재 상태", "Mixed emotional state", "感情が混ざった状態")
    }

    val lesson = when (tag) {
        loc(language, "말실수", "Communication", "言葉のすれ違い") -> loc(language, "답하기 전에 확인 질문을 한 번 더 하고, 감정이 큰 날은 말의 속도를 늦추기", "Ask one clarifying question before replying, and slow down on emotionally heavy days.", "返す前に確認の質問を1つ入れ、感情が大きい日は話す速度を落としましょう。")
        loc(language, "미루기", "Avoidance", "先延ばし") -> loc(language, "완벽한 답을 만들기보다 10분 안에 짧은 첫 응답 보내기", "Send a short first response within 10 minutes instead of waiting for a perfect answer.", "完璧な返事を待つより、10分以内に短い最初の返答を送りましょう。")
        loc(language, "관계", "Relationship", "関係") -> loc(language, "내 감정보다 먼저 상대 상황을 확인하고, 해석 전에 사실을 묻기", "Check the other person's situation before reacting, and ask for facts before interpretation.", "自分の感情より先に相手の状況を確かめ、解釈の前に事実を尋ねましょう。")
        else -> loc(language, "감정이 올라온 이유를 한 줄로 적고 다음 행동을 작게 정하기", "Name the emotion in one line and choose one small next action.", "感情が湧いた理由を一文で書き、次の行動を小さく決めましょう。")
    }

    val reflection = when (tag) {
        loc(language, "말실수", "Communication", "言葉のすれ違い") -> loc(language, "감정이 올라온 순간 방어적이거나 날카로운 표현이 먼저 나온 기록이에요. 다음엔 반응보다 확인을 먼저 두는 연습이 필요해요.", "A defensive or sharp response appeared before reflection. Next time, practice checking first and reacting second.", "感情が上がった瞬間に、防衛的または鋭い表現が先に出た記録です。次は反応より確認を先に置く練習が必要です。")
        loc(language, "미루기", "Avoidance", "先延ばし") -> loc(language, "불편한 마음을 피하려고 미뤘지만 부담이 더 커진 패턴이에요. 빠른 첫 반응이 오히려 관계를 덜 어렵게 만들어요.", "You delayed to avoid discomfort, but the burden grew. A quick first response would likely reduce tension.", "不快感を避けようとして先延ばしにした結果、負担が大きくなったパターンです。早い最初の反応のほうが関係を軽くします。")
        loc(language, "관계", "Relationship", "関係") -> loc(language, "상대의 의도보다 내 감정의 충격이 먼저 커진 기록이에요. 사실 확인과 공감 한 문장이 관계 손상을 줄여줄 수 있어요.", "Your emotional impact became larger than the other person's intention. A fact-checking question and one empathic sentence could reduce harm.", "相手の意図より自分の感情の衝撃が先に大きくなった記録です。事実確認と共感の一文が関係の損傷を減らせます。")
        else -> loc(language, "이 기록은 감정이 정리되기 전에 선택이 나간 장면에 가까워요. 감정 이름 붙이기만 해도 다음 행동이 훨씬 선명해질 수 있어요.", "This decision seems to have happened before the emotion was fully processed. Naming the feeling alone can make the next action much clearer.", "この記録は感情が整理される前に選択が出た場面に近いです。感情に名前をつけるだけでも次の行動がかなり明確になります。")
    }

    val rewriteSuggestion = when {
        situation.isBlank() -> loc(language, "상황을 한 문장만 더 적어두면 AI 첨삭이 더 구체적으로 바뀝니다.", "Add one more sentence about the situation to make the AI guidance more specific.", "状況をあと一文だけ足すと、AI添削がより具体的になります。")
        feeling.isBlank() -> loc(language, "감정 단어를 하나만 더 분명하게 적으면 조언 품질이 올라갑니다.", "Name one feeling more clearly to improve the quality of the advice.", "感情語をもう1つはっきり書くと、助言の質が上がります。")
        else -> loc(language, "사실과 해석을 분리해서 쓰면 기록이 더 차분하고 설득력 있게 정리됩니다.", "Separate facts from interpretation to make the entry calmer and more persuasive.", "事実と解釈を分けて書くと、記録がより落ち着いて説得力ある形に整います。")
    }

    return ReflectionDraft(
        reflection = reflection,
        lesson = lesson,
        tag = tag,
        mentalState = mentalState,
        rewriteSuggestion = rewriteSuggestion
    )
}

fun buildInsightReport(entries: List<Entry>, language: AppLanguage): InsightReport {
    if (entries.isEmpty()) {
        return InsightReport(
            pattern = loc(language, "아직 기록이 없어요. 첫 번째 기록이 쌓이면 패턴을 분석해드릴 수 있어요.", "There are no entries yet. Once the first reflection is saved, patterns can be analyzed.", "まだ記録がありません。最初の記録がたまるとパターンを分析できます。"),
            need = loc(language, "지금은 평가보다 기록 습관을 만드는 게 먼저예요.", "Right now, building the habit of recording matters more than evaluation.", "今は評価より、記録する習慣を作ることが先です。"),
            action = loc(language, "오늘 하루에서 가장 마음에 남는 순간 한 가지를 적어보세요.", "Write down one moment that stayed with you most today.", "今日いちばん心に残った瞬間を1つ書いてみましょう。"),
            compass = loc(language, "후회를 다루는 방식이 바뀌면 다음 선택도 달라집니다.", "When the way you process regret changes, the next choice changes too.", "後悔の扱い方が変わると、次の選択も変わります。")
        )
    }

    val tags = entries.groupingBy { it.tag }.eachCount()
    val topTag = tags.maxByOrNull { it.value }?.key ?: loc(language, "감정", "Emotion", "感情")
    val feelings = entries.joinToString(" ") { it.feeling }
    val mentalStates = entries.groupingBy { it.mentalState }.eachCount()
    val topMentalState = mentalStates.maxByOrNull { it.value }?.key ?: loc(language, "정서 혼재 상태", "Mixed emotional state", "感情が混ざった状態")

    val need = when {
        keywordCount(feelings, "불안", "긴장", "anx", "nervous") > 0 -> loc(language, "안전하게 이해받고 싶다는 욕구가 커 보여요. 피드백이나 대화가 공격처럼 느껴질 때가 있을 수 있어요.", "There seems to be a strong need to feel safe and understood. Feedback or conversation may sometimes feel like an attack.", "安心して理解されたい欲求が強く見えます。フィードバックや会話が攻撃のように感じられる時がありそうです。")
        keywordCount(feelings, "미안", "죄책감", "guilt", "sorry") > 0 -> loc(language, "관계를 회복하고 싶다는 마음이 강해 보여요. 다만 후회가 자기비난으로 흐르지 않게 균형이 필요해요.", "There is a strong wish to repair relationships. The key is keeping regret from turning into harsh self-blame.", "関係を修復したい気持ちが強く見えます。ただし、後悔が自己否定に流れすぎないようバランスが必要です。")
        else -> loc(language, "감정을 바로 해결하려 하기보다, 먼저 알아차리고 이름 붙이는 시간이 더 필요해 보여요.", "More time may be needed to notice and name emotions before trying to solve them immediately.", "すぐ解決しようとするより、まず気づいて名前をつける時間が必要そうです。")
    }

    val pattern = when (topTag) {
        loc(language, "말실수", "Communication", "言葉のすれ違い") -> loc(language, "최근 기록에서 자주 보이는 패턴은 감정이 커질 때 말의 속도와 강도가 같이 올라가는 점이에요.", "A common recent pattern is that both speed and sharpness increase when emotions intensify.", "最近の記録では、感情が大きくなると話す速さと強さが一緒に上がる傾向が見えます。")
        loc(language, "미루기", "Avoidance", "先延ばし") -> loc(language, "최근 기록에서 자주 보이는 패턴은 불편한 대화를 미루다가 부담이 더 커지는 점이에요.", "A common recent pattern is postponing uncomfortable conversations until the burden grows.", "最近の記録では、不快な会話を先延ばしにして負担が大きくなる傾向が見えます。")
        loc(language, "관계", "Relationship", "関係") -> loc(language, "최근 기록에서 자주 보이는 패턴은 상대 의도보다 상처받은 감정에 먼저 반응하는 점이에요.", "A common recent pattern is reacting to hurt feelings before checking the other person's intent.", "最近の記録では、相手の意図より傷ついた感情に先に反応する傾向が見えます。")
        else -> loc(language, "최근 기록에서 자주 보이는 패턴은 감정 정리 전에 행동이 먼저 나간다는 점이에요.", "A common recent pattern is acting before the emotion has been fully sorted out.", "最近の記録では、感情が整う前に行動が先に出る傾向が見えます。")
    }

    val action = buildString {
        append(
            when (topTag) {
                loc(language, "말실수", "Communication", "言葉のすれ違い") -> loc(language, "이번 주 규칙: 바로 답하지 말고 10초 멈춘 뒤 확인 질문 1개를 먼저 하기.", "Rule for this week: pause for 10 seconds before replying and ask one clarifying question first.", "今週のルール: すぐ返さず、10秒止まってから確認の質問を1つ先にしましょう。")
                loc(language, "미루기", "Avoidance", "先延ばし") -> loc(language, "이번 주 규칙: 10분 안에 첫 답장을 보내고, 부담되는 연락은 세 문장 초안부터 쓰기.", "Rule for this week: send the first reply within 10 minutes and start difficult messages with a three-line draft.", "今週のルール: 10分以内に最初の返事を送り、重い連絡は3文の下書きから始めましょう。")
                loc(language, "관계", "Relationship", "関係") -> loc(language, "이번 주 규칙: 의도 확인 질문을 먼저 하고, 해석보다 사실을 먼저 적기.", "Rule for this week: ask about intent first and write facts before interpretation.", "今週のルール: 意図を確かめる質問を先にして、解釈より事実を先に書きましょう。")
                else -> loc(language, "이번 주 규칙: 감정 이름 붙이기와 사건-해석 분리하기.", "Rule for this week: name the emotion and separate the event from your interpretation.", "今週のルール: 感情に名前をつけ、出来事と解釈を分けましょう。")
            }
        )
        append(loc(language, " 현재 자주 드러나는 심리 상태는 ", " Current recurring mental state: ", " 現在よく現れる心理状態は "))
        append(topMentalState)
        append(loc(language, " 로 보여요.", ".", " です。"))
    }

    return InsightReport(
        pattern = pattern,
        need = need,
        action = action,
        compass = loc(language, "후회를 없애려 하기보다, 다음 선택을 바꾸는 재료로 써보세요.", "Do not try to erase regret. Use it as material for a better next choice.", "後悔を消そうとするより、次の選択を変える材料として使ってみましょう。")
    )
}

fun buildCoachProfile(entries: List<Entry>, language: AppLanguage): CoachProfile {
    if (entries.isEmpty()) {
        return CoachProfile(
            writingStyle = loc(language, "기록이 쌓이면 글쓰기 성향을 읽어드릴게요.", "Writing style guidance will appear once more entries are saved.", "記録がたまると、文章の傾向ガイドが表示されます。"),
            moodSummary = loc(language, "감정 흐름을 읽으려면 몇 개의 기록이 더 필요해요.", "A few more entries are needed to read your mood flow.", "感情の流れを読むには、もう少し記録が必要です。"),
            depressionSignal = loc(language, "우울 경향 평가는 진단이 아니라 기록 패턴 참고용으로 제공돼요.", "Depression tendency is offered only as a non-diagnostic journaling reference.", "抑うつ傾向は診断ではなく、記録パターンの参考としてのみ表示されます。"),
            mbtiGuess = loc(language, "MBTI 경향은 기록이 쌓이면 부드럽게 추정해드릴게요.", "MBTI tendency will be estimated gently after more journaling data exists.", "MBTI傾向は記録がたまるとやわらかく推定します。"),
            advice = loc(language, "먼저 3개 정도의 기록을 남기고 다시 AI 코치를 열어보세요.", "Save around three entries first, then open the AI coach again.", "まず3件ほど記録してから、もう一度AIコーチを開いてみてください。")
        )
    }

    val averageLength = entries.map { it.situation.length + it.feeling.length }.average()
    val empathySignals = entries.sumOf { keywordCount(it.situation + " " + it.feeling, "미안", "걱정", "배려", "sorry", "care", "worry") }
    val planningSignals = entries.sumOf { keywordCount(it.lesson, "계획", "정리", "먼저", "plan", "organize", "first") }
    val avoidanceSignals = entries.sumOf { keywordCount(it.situation + " " + it.feeling, "미루", "회피", "avoid", "delay", "burden") }
    val lowMoodSignals = entries.sumOf { keywordCount(it.feeling + " " + it.mentalState, "우울", "무기력", "공허", "슬픔", "depress", "empty", "sad", "hopeless") }
    val moodScoreAverage = entries.map { moodScore(it) }.average().toFloat()

    val writingStyle = when {
        averageLength > 120 && empathySignals > planningSignals -> loc(language, "서술이 섬세하고 관계 맥락을 많이 살피는 편이에요. 감정의 결을 잘 잡지만, 핵심 장면이 길어질 때는 한 문장 요약을 먼저 두면 더 읽기 쉬워져요.", "Your writing is detailed and relational. Leading with a one-sentence summary of the core scene would make it easier to revisit later.", "記述が繊細で、関係の文脈をよく見ています。核心の場面を一文で先に置くと、あとで読み返しやすくなります。")
        averageLength > 100 -> loc(language, "상황 설명이 충분하고 성찰 밀도가 높은 편이에요. 길어질 때는 사실과 해석을 나눠 쓰면 첨삭 결과가 더 선명해집니다.", "You give solid context and reflective detail. Separating facts from interpretation will make the coaching output clearer.", "状況説明が十分で、内省の密度が高いタイプです。事実と解釈を分けると、添削結果がより明確になります。")
        else -> loc(language, "짧고 직접적으로 기록하는 편이에요. 빠르게 적는 장점이 크고, 감정 단어를 1개만 더 보태면 AI 조언의 정확도가 더 좋아집니다.", "Your journaling is concise and direct. Adding one more feeling word would improve the precision of the advice.", "短く直接的に記録するタイプです。感情語をもう1つ足すとAI助言の精度が上がります。")
    }

    val moodSummary = when {
        moodScoreAverage < 35f -> loc(language, "최근 기록은 에너지가 많이 내려가 있고 자기비판이 쉽게 붙는 흐름에 가까워요. 급히 고치려 하기보다 수면, 식사, 휴식 같은 기본 리듬을 먼저 회복하는 접근이 좋아 보여요.", "Recent entries suggest low energy with self-critical undertones. Restoring basics like sleep, food, and recovery rhythm may help more than trying to fix everything at once.", "最近の記録はエネルギーがかなり下がり、自己批判がつきやすい流れです。急いで直そうとするより、睡眠・食事・休息の基本リズムを整える方がよさそうです。")
        moodScoreAverage < 55f -> loc(language, "감정 기복은 있지만 회복 여지도 함께 보여요. 무거운 날과 괜찮은 날을 구분해서 적는 습관이 조언 품질을 높여줄 거예요.", "There is emotional fluctuation, but signs of recovery are present too. Separating heavy days from steadier days will improve the quality of the guidance.", "感情の波はありますが、回復の余地も見えます。重い日と落ち着いた日を分けて記録すると、助言の質が上がります。")
        else -> loc(language, "전체적으로는 감정 회복력이 있는 흐름이에요. 다만 관계나 일정 압박이 커질 때 급격히 흔들리는 구간을 주의해서 보면 좋아요.", "Overall, your entries suggest decent emotional recovery capacity. Watch for sharper dips when relationships or schedules become intense.", "全体として感情の回復力はあります。関係や予定の圧が強まる時に大きく揺れやすい点を意識するとよさそうです。")
    }

    val depressionSignal = when {
        lowMoodSignals >= 10 -> loc(language, "최근 기록에는 무기력, 공허감, 자기비난 표현이 반복됩니다. 진단은 아니지만 우울 신호가 길어질 수 있어 보여요. 혼자 버티기보다 가까운 사람과 나누고, 필요하면 전문 도움을 연결해두는 편이 안전합니다.", "Recent entries repeatedly mention emptiness, low energy, and self-criticism. This is not a diagnosis, but it suggests a sustained low-mood stretch. Reaching out to trusted support and professional help if needed would be wise.", "最近の記録には無気力、空虚感、自己批判の表現が繰り返し見られます。診断ではありませんが、低い気分が長引く流れかもしれません。ひとりで抱え込まず、信頼できる人や必要なら専門家につなげておくと安全です。")
        lowMoodSignals >= 5 -> loc(language, "우울감으로 읽힐 수 있는 단어가 몇 차례 반복돼요. 진단 단계는 아니지만, 컨디션 저하와 자기비판이 이어질 때는 일정 강도를 줄이고 회복 루틴을 확보하는 게 좋아 보여요.", "Several entries repeat language that can signal low mood. This is not diagnostic, but if fatigue and self-criticism keep stacking up, reducing load and protecting recovery routines would help.", "抑うつ感として読める言葉が何度か繰り返されています。診断段階ではありませんが、疲労と自己批判が続く時は負荷を下げ、回復ルーティンを確保するのがよさそうです。")
        else -> loc(language, "현재 기록만으로는 강한 우울 경향이 반복된다고 보이진 않아요. 다만 갑자기 무기력이나 공허감이 길어지면 기록만 믿지 말고 실제 도움 자원도 함께 살펴보세요.", "The current journal history does not show a strong repeated low-mood pattern. If numbness or emptiness suddenly lasts longer, do not rely on journaling alone and consider outside support too.", "今の記録だけでは強い抑うつ傾向の反復は目立ちません。ただし無気力や空虚感が長引く時は、記録だけでなく外部の支援先も見てください。")
    }

    val advice = if (avoidanceSignals > planningSignals) {
        loc(language, "지금은 완벽한 정리보다 빠른 첫 행동이 더 중요해요. 답장을 미루는 대신 3문장 초안, 감정이 큰 날엔 10초 멈춤, 자기비난이 올라오면 사실 1개와 배운 점 1개만 남겨보세요.", "Right now, a fast first action matters more than a perfect one. Try a three-line draft instead of delaying replies, pause for 10 seconds on emotionally heavy days, and when self-criticism rises, capture one fact and one lesson only.", "今は完璧な整理より、早い最初の行動が大切です。返信を先延ばしにする代わりに3文の下書き、感情が大きい日は10秒止まり、自己批判が強い時は事実1つと学び1つだけ残してみてください。")
    } else {
        loc(language, "기록의 질은 이미 좋아요. 다음 단계는 감정 변화를 수치로 보는 습관이에요. 주간 그래프를 열어 흔들리는 요일과 회복되는 요일을 같이 확인해보면 조언이 더 개인화됩니다.", "Your journal quality is already strong. The next step is tracking emotional change numerically. Use the weekly graph to compare days that dip with days that recover.", "記録の質はすでに高いです。次の段階は感情変化を数値で見る習慣です。週間グラフで揺れやすい日と回復しやすい日を比べてみてください。")
    }

    return CoachProfile(
        writingStyle = writingStyle,
        moodSummary = moodSummary,
        depressionSignal = depressionSignal,
        mbtiGuess = estimateMbti(entries, empathySignals, planningSignals, avoidanceSignals, language),
        advice = advice
    )
}

fun buildTrendReport(entries: List<Entry>, language: AppLanguage): TrendReport {
    val today = currentDate()
    val weeklyPoints = buildList {
        for (offset in 6 downTo 0) {
            val date = today.minusDays(offset.toLong())
            val dayEntries = entries.filter { it.dateKey == date.toString() }
            add(TrendPoint(label = date.format(shortDayFormatter()), score = averageMoodScore(dayEntries), count = dayEntries.size))
        }
    }

    val monthlyPoints = buildList {
        for (offset in 3 downTo 0) {
            val end = today.minusDays((offset * 7).toLong())
            val start = end.minusDays(6)
            val rangeEntries = entries.filter {
                val date = LocalDate.parse(it.dateKey)
                !date.isBefore(start) && !date.isAfter(end)
            }
            add(TrendPoint(label = "${start.monthValue}/${start.dayOfMonth}-${end.monthValue}/${end.dayOfMonth}", score = averageMoodScore(rangeEntries), count = rangeEntries.size))
        }
    }

    val trendSlope = if (weeklyPoints.size >= 2) {
        (weeklyPoints.last().score - weeklyPoints.first().score) / (weeklyPoints.size - 1)
    } else {
        0f
    }
    val forecastBase = weeklyPoints.lastOrNull()?.score ?: 50f
    val outlookPoints = buildList {
        for (step in 1..4) {
            add(TrendPoint(label = loc(language, "+${step}주", "+${step}w", "+${step}週"), score = (forecastBase + (trendSlope * step)).coerceIn(18f, 92f), count = 0))
        }
    }

    val summary = when {
        weeklyPoints.all { it.count == 0 } -> loc(language, "아직 최근 1주일 기록이 적어서 그래프가 중립값으로 보입니다. 3일 이상 적으면 흐름이 더 정확해져요.", "Recent weekly data is still sparse, so the chart uses neutral values. Three or more active days will make the trend clearer.", "最近1週間の記録がまだ少ないため、グラフは中立値で表示されています。3日以上記録すると流れがより正確になります。")
        trendSlope > 2f -> loc(language, "최근 일주일은 감정 회복이 점진적으로 올라오는 흐름이에요.", "The last week suggests a gradual emotional recovery trend.", "直近1週間は感情の回復がゆるやかに上向いています。")
        trendSlope < -2f -> loc(language, "최근 일주일은 감정 에너지가 내려가는 흐름이 보여요. 휴식과 부담 조절이 중요해 보여요.", "The last week shows a downward energy trend. Rest and load management look important.", "直近1週間は感情エネルギーが下がる流れです。休息と負荷調整が大切そうです。")
        else -> loc(language, "최근 일주일은 큰 붕괴 없이 흔들리며 유지되는 흐름이에요.", "The last week looks relatively steady, with fluctuations but no sharp collapse.", "直近1週間は大きく崩れず、揺れながら保たれている流れです。")
    }

    val outlook = when {
        outlookPoints.lastOrNull()?.score ?: 50f >= 60f -> loc(language, "현재 패턴을 유지하면 다음 몇 주는 조금 더 안정된 쪽으로 갈 가능성이 있어요. 기록이 이어지는 날의 회복 점수가 특히 높습니다.", "If the current pattern continues, the next few weeks may move toward more stability. Recovery scores are strongest on days when journaling continues.", "現在のパターンが続けば、次の数週間はやや安定に向かう可能性があります。記録が続く日の回復スコアが特に高いです。")
        outlookPoints.lastOrNull()?.score ?: 50f <= 40f -> loc(language, "앞으로의 그래프는 부담이 누적될 때 다시 내려갈 수 있음을 보여줘요. 일정 밀도와 관계 스트레스를 줄이는 쪽이 우선입니다.", "The forecast suggests another dip if pressure keeps accumulating. Lowering schedule density and relational stress should come first.", "今後のグラフは、負担が積み重なると再び下がる可能性を示しています。予定の密度と対人ストレスを下げることが優先です。")
        else -> loc(language, "앞으로는 급격한 변화보다 완만한 회복 또는 유지 흐름이 예상돼요. 작은 루틴을 지키는 쪽이 가장 효과적입니다.", "The outlook suggests gradual recovery or maintenance rather than a sharp change. Small routines are likely to help most.", "今後は急変より、ゆるやかな回復または維持が見込まれます。小さなルーティンを守るのが最も効果的です。")
    }

    return TrendReport(
        weeklyPoints = weeklyPoints,
        monthlyPoints = monthlyPoints,
        outlookPoints = outlookPoints,
        summary = summary,
        outlook = outlook
    )
}

fun computeJournalStats(entries: List<Entry>, language: AppLanguage): JournalStats {
    if (entries.isEmpty()) {
        return JournalStats(
            topTag = loc(language, "기록 대기", "Awaiting entries", "記録待ち"),
            topMentalState = loc(language, "아직 없음", "Not yet", "まだありません")
        )
    }

    val topTag = entries.groupingBy { it.tag }.eachCount().maxByOrNull { it.value }?.key.orEmpty()
    val topMentalState = entries.groupingBy { it.mentalState }.eachCount().maxByOrNull { it.value }?.key.orEmpty()
    val activeDates = entries.map { LocalDate.parse(it.dateKey) }.toSet()
    val monthKey = currentMonthKey()
    return JournalStats(
        totalEntries = entries.size,
        activeDays = activeDates.size,
        currentStreak = calculateCurrentStreak(entries),
        thisMonthEntries = entries.count { it.dateKey.startsWith(monthKey) },
        topTag = topTag,
        topMentalState = topMentalState
    )
}

fun calculateCurrentStreak(entries: List<Entry>): Int {
    if (entries.isEmpty()) return 0

    val uniqueDates = entries.map { LocalDate.parse(it.dateKey) }.toSet()
    val today = currentDate()
    val anchor = when {
        uniqueDates.contains(today) -> today
        uniqueDates.contains(today.minusDays(1)) -> today.minusDays(1)
        else -> return 0
    }

    var streak = 0
    var cursor = anchor
    while (uniqueDates.contains(cursor)) {
        streak += 1
        cursor = cursor.minusDays(1)
    }
    return streak
}

private fun estimateMbti(
    entries: List<Entry>,
    empathySignals: Int,
    planningSignals: Int,
    avoidanceSignals: Int,
    language: AppLanguage
): String {
    val text = entries.joinToString(" ") { "${it.situation} ${it.feeling} ${it.lesson}" }
    val introversionSignals = keywordCount(text, "혼자", "내향", "조용", "alone", "quiet", "inside")
    val extroversionSignals = keywordCount(text, "친구", "사람", "대화", "friend", "people", "talk")
    val sensingSignals = keywordCount(text, "사실", "구체", "일정", "fact", "detail", "schedule")
    val intuitionSignals = keywordCount(text, "의미", "느낌", "가능성", "meaning", "possibility", "pattern")
    val thinkingSignals = keywordCount(text, "논리", "정리", "분석", "logic", "analyze", "organize")
    val feelingSignals = empathySignals + keywordCount(text, "감정", "상처", "공감", "emotion", "hurt", "empathy")

    val ie = if (introversionSignals >= extroversionSignals) "I" else "E"
    val ns = if (intuitionSignals >= sensingSignals) "N" else "S"
    val ft = if (feelingSignals >= thinkingSignals) "F" else "T"
    val jp = if (planningSignals >= avoidanceSignals) "J" else "P"
    val mbti = ie + ns + ft + jp
    val tilt = if (
        abs(introversionSignals - extroversionSignals) <= 1 ||
        abs(intuitionSignals - sensingSignals) <= 1 ||
        abs(feelingSignals - thinkingSignals) <= 1 ||
        abs(planningSignals - avoidanceSignals) <= 1
    ) {
        loc(language, "부드러운 추정", "soft estimate", "やわらかい推定")
    } else {
        loc(language, "기록 기반 추정", "journal-based estimate", "記録ベースの推定")
    }
    return loc(language, "$mbti 경향 $tilt", "$mbti tendency, $tilt", "$mbti 傾向、$tilt")
}

private fun averageMoodScore(entries: List<Entry>): Float {
    return if (entries.isEmpty()) 50f else entries.map { moodScore(it) }.average().toFloat()
}

private fun moodScore(entry: Entry): Float {
    val text = "${entry.feeling} ${entry.mentalState} ${entry.situation} ${entry.lesson}"
    var score = 58f
    score -= keywordCount(text, "우울", "무기력", "공허", "슬픔", "불안", "죄책", "후회", "짜증", "depress", "empty", "sad", "anx", "guilt", "regret", "angry") * 5f
    score += keywordCount(text, "안도", "감사", "편안", "희망", "회복", "relief", "grateful", "calm", "hope", "better") * 4f
    score += keywordCount(entry.lesson, "다음", "실행", "연락", "정리", "next", "plan", "reply", "organize") * 1.5f
    return score.coerceIn(12f, 92f)
}

private fun keywordCount(source: String, vararg keywords: String): Int {
    return keywords.count { source.contains(it, ignoreCase = true) }
}

private fun shortDayFormatter(): DateTimeFormatter = DateTimeFormatter.ofPattern("M/d")
