package com.hackpath.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackpath.app.data.model.Lesson
import com.hackpath.app.data.model.LessonProgress
import com.hackpath.app.data.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonUiState(
    val lesson: Lesson? = null,
    val progress: LessonProgress? = null,
    val quizAnswerIndex: Int? = null,
    val quizSubmitted: Boolean = false,
    val xpEarned: Int = 0,
    val showXpBanner: Boolean = false
)

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val repo: CourseRepository,
    savedState: SavedStateHandle
) : ViewModel() {

    private val lessonId: String = checkNotNull(savedState["lessonId"])

    private val _state = MutableStateFlow(LessonUiState())
    val state: StateFlow<LessonUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val lesson   = repo.getLesson(lessonId)
            val progress = repo.getLessonProgress(lessonId)
            _state.update { it.copy(lesson = lesson, progress = progress) }
        }
    }

    fun selectQuizAnswer(index: Int) {
        if (_state.value.quizSubmitted) return
        _state.update { it.copy(quizAnswerIndex = index) }
    }

    fun submitQuiz() {
        val state = _state.value
        val lesson = state.lesson ?: return
        val selected = state.quizAnswerIndex ?: return
        val correct = lesson.quiz?.correctIndex == selected
        val xp = if (correct) 50 else 10
        _state.update { it.copy(quizSubmitted = true, xpEarned = xp) }
        viewModelScope.launch {
            repo.completeLesson(lessonId, quizPassed = correct, xpEarned = xp)
        }
    }

    fun markRead() {
        val state = _state.value
        if (state.lesson?.quiz == null) {
            viewModelScope.launch {
                repo.completeLesson(lessonId, quizPassed = false, xpEarned = 20)
                _state.update { it.copy(xpEarned = 20, showXpBanner = true) }
            }
        }
    }

    fun dismissXpBanner() {
        _state.update { it.copy(showXpBanner = false) }
    }
}
