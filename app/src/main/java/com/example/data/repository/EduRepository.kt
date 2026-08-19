package com.example.data.repository

import com.example.data.local.EduDao
import com.example.data.model.*
import com.example.data.remote.GeminiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.random.Random

class EduRepository(private val dao: EduDao) {

    val userProgress: Flow<UserProgressEntity> = dao.getUserProgress().map {
        it ?: UserProgressEntity()
    }

    val studyQuestions: Flow<List<StudyQuestionEntity>> = dao.getAllStudyQuestions()
    val starredQuestions: Flow<List<StudyQuestionEntity>> = dao.getStarredQuestions()

    suspend fun addExp(amount: Int) {
        val current = dao.getUserProgressOnce() ?: UserProgressEntity()
        val newExp = current.exp + amount
        val newLevel = (newExp / 100) + 1
        val updated = current.copy(
            exp = newExp,
            level = newLevel,
            totalQuestionsAsked = current.totalQuestionsAsked + 1
        )
        dao.saveUserProgress(updated)
    }

    suspend fun unlockBadge(badgeId: String) {
        val current = dao.getUserProgressOnce() ?: UserProgressEntity()
        val existing = current.badgesUnlockedCsv.split(",").filter { it.isNotBlank() }.toMutableSet()
        if (!existing.contains(badgeId)) {
            existing.add(badgeId)
            val updated = current.copy(badgesUnlockedCsv = existing.joinToString(","))
            dao.saveUserProgress(updated)
        }
    }

    suspend fun answerStudyQuestion(questionText: String, subjectTag: String = "General"): StudyQuestionEntity {
        val systemPrompt = """
            You are EduAI Study Search, an intelligent AI homework and study question solver for students and children (Grade 8 & elementary).
            Your answers MUST be ultra-clear, factual, grounded like a top Google Search study summary, and 100% kid-friendly.
            Structure your response using these exact section headers:
            
            DIRECT_ANSWER:
            [A clear, crisp 1-2 sentence direct explanation of the core answer in simple words]
            
            KEY_POINTS:
            • [Key fact / Step 1 explaining the logic or concept]
            • [Key fact / Step 2 explaining why or how it works]
            • [Key fact / Step 3 with a relatable everyday analogy or real-world example]
            
            DID_YOU_KNOW:
            [One fascinating, memorable study trivia fact related to this question]
            
            RELATED:
            [Related search question 1] | [Related search question 2] | [Related search question 3]
        """.trimIndent()

        val prompt = "Subject: $subjectTag\nStudy Question: $questionText\n\nPlease answer with full clarity and step-by-step points."
        val rawResponse = GeminiClient.askBuddy(prompt, systemPrompt)

        val directAnswer = extractSection(rawResponse, "DIRECT_ANSWER:", "KEY_POINTS:")
            .ifBlank { generateFallbackAnswer(questionText, subjectTag) }

        val keyPoints = extractSection(rawResponse, "KEY_POINTS:", "DID_YOU_KNOW:")
            .ifBlank { generateFallbackKeyPoints(questionText, subjectTag) }

        val didYouKnow = extractSection(rawResponse, "DID_YOU_KNOW:", "RELATED:")
            .ifBlank { generateFallbackDidYouKnow(subjectTag) }

        val relatedQuestions = extractSection(rawResponse, "RELATED:", "")
            .ifBlank { generateFallbackRelated(subjectTag) }

        val entity = StudyQuestionEntity(
            questionText = questionText.trim(),
            subject = detectSubject(questionText, subjectTag),
            directAnswer = directAnswer,
            detailedSteps = keyPoints,
            funFact = didYouKnow,
            relatedQuestionsCsv = relatedQuestions,
            isStarred = false
        )

        dao.insertStudyQuestion(entity)
        addExp(25)
        unlockBadge("FIRST_QUESTION")
        if (entity.subject == "Science") unlockBadge("SCIENCE_SEEKER")
        if (entity.subject == "Math") unlockBadge("MATH_WHIZ")
        if (entity.subject == "History") unlockBadge("HISTORY_BUFF")
        if (entity.subject == "Space") unlockBadge("SPACE_EXPLORER")

        return entity
    }

    suspend fun toggleStar(id: Long, currentStarred: Boolean) {
        dao.updateStarStatus(id, !currentStarred)
        if (!currentStarred) {
            unlockBadge("STUDY_SCHOLAR")
        }
    }

    suspend fun deleteQuestion(question: StudyQuestionEntity) {
        dao.deleteStudyQuestion(question)
    }

    suspend fun clearAllQuestions() {
        dao.clearAllStudyQuestions()
    }

    fun getRandomStudyQuestion(subjectFilter: String? = null): String {
        val pool = when (subjectFilter?.lowercase()) {
            "science" -> listOf(
                "Why is the ocean salty? 🌊",
                "How do clouds make rain and snow? 🌧️",
                "Why do leaves change color in autumn? 🍂",
                "How do vaccines protect our body from viruses? 💉",
                "What makes a volcano erupt? 🌋",
                "Why do apples turn brown after being sliced? 🍎",
                "How do solar panels turn sunlight into electricity? ⚡",
                "Why is glass transparent while sand is not? 🏖️"
            )
            "space" -> listOf(
                "Why is Mars called the Red Planet? 🪐",
                "What happens inside a black hole? 🕳️",
                "How do astronauts sleep in zero gravity on the ISS? 👨‍🚀",
                "Why does the Moon have different phases? 🌙",
                "How old is our Sun and will it ever burn out? ☀️",
                "What causes the Northern Lights (Aurora Borealis)? 🌌",
                "How big is the Milky Way Galaxy compared to Earth? ✨"
            )
            "math" -> listOf(
                "How do fractions work and why do we find a common denominator? 🍕",
                "What is the Pythagorean Theorem and where do we use it? 📐",
                "Why can't we divide any number by zero? ➗",
                "What is the Fibonacci sequence in nature and flowers? 🌻",
                "How do percentages work when calculating a sale discount? 🏷️",
                "Solve step-by-step: 3x + 9 = 24 ✏️",
                "What is the difference between mean, median, and mode? 📊"
            )
            "history" -> listOf(
                "Who built the Great Pyramids of Giza and how? 🏛️",
                "Why was the Silk Road so important for world trade? 🐫",
                "What caused the Renaissance period in Europe? 🎨",
                "Who invented the printing press and why did it change the world? 📜",
                "How did ancient Greeks start the Olympic Games? 🏃",
                "What was the purpose of the Great Wall of China? 🏯"
            )
            "geography" -> listOf(
                "Why do we have four different seasons on Earth? 🌍",
                "What is the deepest trench in the world ocean? 🌊",
                "Why is the Sahara Desert so hot during the day and cold at night? 🏜️",
                "How are mountains and earthquake fault lines formed? 🏔️",
                "Why do different time zones exist across countries? ⏰"
            )
            "tech" -> listOf(
                "How does Artificial Intelligence actually learn from data? 🤖",
                "What is an algorithm in computer coding? 💻",
                "How do search engines like Google find answers in milliseconds? 🔍",
                "What is binary code and why do computers use 0s and 1s? 💾",
                "How does Wi-Fi send internet through thin air? 📶"
            )
            "nature" -> listOf(
                "How do bees communicate using the 'waggle dance'? 🐝",
                "Why do chameleons change their skin colors? 🦎",
                "How do trees communicate with each other through root networks? 🌲",
                "Why do whales sing songs underwater? 🐋",
                "How do owls turn their heads almost all the way around? 🦉"
            )
            else -> listOf(
                "Why is the sky blue during the day and red at sunset? ☀️",
                "How does photosynthesis allow plants to make oxygen and food? 🌿",
                "What is a black hole and can anything escape it? 🌌",
                "How do search engines index billions of web pages? 🔍",
                "Why do we dream when we sleep at night? 😴",
                "What is the difference between speed and velocity in physics? 🚀",
                "How do birds fly and what creates aerodynamic lift? 🦅",
                "Who was Leonardo da Vinci and what did he invent? 🎨",
                "Why does ice float on top of liquid water? 🧊",
                "How do our eyes see different colors of light? 👁️"
            )
        }
        return pool[Random.nextInt(pool.size)]
    }

    private fun detectSubject(question: String, subjectHint: String): String {
        if (subjectHint != "General" && subjectHint.isNotBlank()) return subjectHint
        val lower = question.lowercase()
        return when {
            lower.contains("math") || lower.contains("fraction") || lower.contains("x +") || lower.contains("solve") || lower.contains("geometry") || lower.contains("percent") -> "Math"
            lower.contains("space") || lower.contains("moon") || lower.contains("planet") || lower.contains("mars") || lower.contains("star") || lower.contains("galaxy") || lower.contains("black hole") -> "Space"
            lower.contains("history") || lower.contains("pyramid") || lower.contains("war") || lower.contains("ancient") || lower.contains("king") || lower.contains("century") -> "History"
            lower.contains("geography") || lower.contains("ocean") || lower.contains("mountain") || lower.contains("country") || lower.contains("earth") || lower.contains("season") -> "Geography"
            lower.contains("code") || lower.contains("computer") || lower.contains("ai") || lower.contains("algorithm") || lower.contains("internet") || lower.contains("wifi") -> "Tech"
            lower.contains("animal") || lower.contains("tree") || lower.contains("plant") || lower.contains("bee") || lower.contains("nature") || lower.contains("bird") || lower.contains("whale") -> "Nature"
            lower.contains("atom") || lower.contains("chemistry") || lower.contains("biology") || lower.contains("physics") || lower.contains("science") || lower.contains("cell") || lower.contains("cloud") -> "Science"
            else -> "General"
        }
    }

    private fun generateFallbackAnswer(question: String, subject: String): String {
        return "This is a fundamental concept in $subject! In simple terms, when you explore '$question', nature and science work together through established laws and physical principles to produce the result we observe."
    }

    private fun generateFallbackKeyPoints(question: String, subject: String): String {
        return "• Core Idea: '$question' connects key principles of $subject to real-world observations.\n• Step 1: Scientists and researchers measure and test how these variables interact under different conditions.\n• Step 2: Everyday systems follow these reliable patterns, making learning about them fascinating and practical!"
    }

    private fun generateFallbackDidYouKnow(subject: String): String {
        return "Did you know? Asking questions and studying new topics actively creates new neural pathways in your brain, making you smarter every single day!"
    }

    private fun generateFallbackRelated(subject: String): String {
        return "How does this apply in everyday life? | What is the history behind this discovery? | How do scientists test this today?"
    }

    private fun extractSection(text: String, startTag: String, endTag: String): String {
        if (!text.contains(startTag)) return ""
        val start = text.indexOf(startTag) + startTag.length
        val end = if (endTag.isNotBlank() && text.contains(endTag, ignoreCase = true)) {
            text.indexOf(endTag, startIndex = start, ignoreCase = true)
        } else {
            text.length
        }
        return if (start < end && start in text.indices) {
            text.substring(start, end).trim()
        } else ""
    }

    fun getAllBadges(unlockedCsv: String): List<Badge> {
        val unlockedSet = unlockedCsv.split(",").filter { it.isNotBlank() }.toSet()
        return listOf(
            Badge("FIRST_QUESTION", "Curious Explorer", "Asked your first AI study question", "ic_search", unlockedSet.contains("FIRST_QUESTION")),
            Badge("CURIOSITY_SPARK", "Curiosity Spark", "Searched across different school subjects", "ic_spark", unlockedSet.contains("CURIOSITY_SPARK")),
            Badge("SCIENCE_SEEKER", "Science Seeker", "Explored science, nature, and biology questions", "ic_science", unlockedSet.contains("SCIENCE_SEEKER")),
            Badge("MATH_WHIZ", "Math Whiz", "Solved step-by-step math and logic questions", "ic_math", unlockedSet.contains("MATH_WHIZ")),
            Badge("HISTORY_BUFF", "History Buff", "Discovered world history & civilization facts", "ic_history", unlockedSet.contains("HISTORY_BUFF")),
            Badge("SPACE_EXPLORER", "Cosmic Scholar", "Ventured into astronomy and space discoveries", "ic_space", unlockedSet.contains("SPACE_EXPLORER")),
            Badge("STUDY_SCHOLAR", "Study Scholar", "Saved key questions to your personal study notes", "ic_star", unlockedSet.contains("STUDY_SCHOLAR"))
        )
    }
}

