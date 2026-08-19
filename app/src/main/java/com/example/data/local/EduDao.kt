package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EduDao {
    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    fun getUserProgress(): Flow<UserProgressEntity?>

    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    suspend fun getUserProgressOnce(): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProgress(progress: UserProgressEntity)

    @Query("SELECT * FROM study_questions ORDER BY id DESC")
    fun getAllStudyQuestions(): Flow<List<StudyQuestionEntity>>

    @Query("SELECT * FROM study_questions WHERE isStarred = 1 ORDER BY id DESC")
    fun getStarredQuestions(): Flow<List<StudyQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyQuestion(question: StudyQuestionEntity): Long

    @Query("UPDATE study_questions SET isStarred = :isStarred WHERE id = :id")
    suspend fun updateStarStatus(id: Long, isStarred: Boolean)

    @Delete
    suspend fun deleteStudyQuestion(question: StudyQuestionEntity)

    @Query("DELETE FROM study_questions")
    suspend fun clearAllStudyQuestions()
}

