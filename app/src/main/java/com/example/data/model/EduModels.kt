package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Alex",
    val exp: Int = 120,
    val level: Int = 2,
    val streakDays: Int = 3,
    val totalQuestionsAsked: Int = 5,
    val badgesUnlockedCsv: String = "FIRST_QUESTION,CURIOSITY_SPARK"
)

@Entity(tableName = "study_questions")
data class StudyQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionText: String,
    val subject: String = "General",
    val directAnswer: String,
    val detailedSteps: String, // Key points / step-by-step logic
    val funFact: String = "", // Did you know / search trivia
    val relatedQuestionsCsv: String = "", // Related search questions
    val isStarred: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean = false
)

