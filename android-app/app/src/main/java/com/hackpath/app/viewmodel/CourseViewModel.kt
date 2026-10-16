package com.hackpath.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackpath.app.data.model.Course
import com.hackpath.app.data.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourseUiState(
    val course: Course? = null,
    val completedIds: Set<String> = emptySet()
)

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val repo: CourseRepository,
    savedState: SavedStateHandle
) : ViewModel() {

    private val courseId: String = checkNotNull(savedState["courseId"])

    private val _state = MutableStateFlow(CourseUiState())
    val state: StateFlow<CourseUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val course = repo.getCourse(courseId)
            _state.update { it.copy(course = course) }
        }
        viewModelScope.launch {
            repo.getAllProgress().collect { list ->
                _state.update { it.copy(completedIds = list.filter { p -> p.completed }.map { p -> p.lessonId }.toSet()) }
            }
        }
    }
}
