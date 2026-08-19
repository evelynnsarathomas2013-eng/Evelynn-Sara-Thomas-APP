package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.EduDatabase
import com.example.data.model.*
import com.example.data.repository.EduRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class EduViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = EduDatabase.getDatabase(application).eduDao()
    private val repository = EduRepository(dao)

    val userProgress: StateFlow<UserProgressEntity> = repository.userProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProgressEntity())

    val studyQuestions: StateFlow<List<StudyQuestionEntity>> = repository.studyQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val starredQuestions: StateFlow<List<StudyQuestionEntity>> = repository.starredQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Question solver state
    private val _questionState = MutableStateFlow<UiState<StudyQuestionEntity>>(UiState.Idle)
    val questionState: StateFlow<UiState<StudyQuestionEntity>> = _questionState.asStateFlow()

    init {
        // Seed an initial study question if empty
        viewModelScope.launch {
            repository.studyQuestions.first().let { list ->
                if (list.isEmpty()) {
                    dao.insertStudyQuestion(
                        StudyQuestionEntity(
                            questionText = "Why is the sky blue? ☀️",
                            subject = "Science",
                            directAnswer = "The sky appears blue because Earth's atmosphere scatters shorter blue wavelengths of sunlight in every direction more than other colors.",
                            detailedSteps = "• Sunlight contains all colors of the rainbow (visible light spectrum).\n• Earth's atmosphere is full of tiny nitrogen and oxygen molecules.\n• Shorter blue light waves bounce (scatter) off these gas molecules much more easily than longer red waves. This is known as Rayleigh scattering.",
                            funFact = "On Mars, the sky is often pink or butterscotch during the day because of airborne iron oxide dust!",
                            relatedQuestionsCsv = "Why are sunsets orange and red? | Why is space completely black? | How do rainbows form?"
                        )
                    )
                }
            }
        }
    }

    fun askStudyQuestion(questionText: String, subjectTag: String = "General") {
        if (questionText.isBlank()) return
        viewModelScope.launch {
            _questionState.value = UiState.Loading
            try {
                val entity = repository.answerStudyQuestion(questionText, subjectTag)
                _questionState.value = UiState.Success(entity)
            } catch (e: Exception) {
                _questionState.value = UiState.Error(e.message ?: "Failed to generate answer")
            }
        }
    }

    fun getRandomQuestion(subject: String? = null): String {
        return repository.getRandomStudyQuestion(subject)
    }

    fun toggleStar(question: StudyQuestionEntity) {
        viewModelScope.launch {
            repository.toggleStar(question.id, question.isStarred)
        }
    }

    fun deleteQuestion(question: StudyQuestionEntity) {
        viewModelScope.launch {
            repository.deleteQuestion(question)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllQuestions()
            _questionState.value = UiState.Idle
        }
    }

    fun resetQuestionState() {
        _questionState.value = UiState.Idle
    }

    fun updateName(newName: String) {
        viewModelScope.launch {
            val current = dao.getUserProgressOnce() ?: UserProgressEntity()
            dao.saveUserProgress(current.copy(name = newName))
        }
    }

    fun getBadges(): List<Badge> {
        return repository.getAllBadges(userProgress.value.badgesUnlockedCsv)
    }
}

