package com.hackpath.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackpath.app.data.model.Track
import com.hackpath.app.data.model.UserProfile
import com.hackpath.app.data.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val tracks: List<Track> = emptyList(),
    val profile: UserProfile = UserProfile(),
    val completedLessonIds: Set<String> = emptySet()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: CourseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repo.ensureProfile()
        }
        viewModelScope.launch {
            combine(
                repo.getProfile().map { it ?: UserProfile() },
                repo.getAllProgress()
            ) { profile, progress ->
                HomeUiState(
                    tracks = repo.getAllTracks(),
                    profile = profile,
                    completedLessonIds = progress.filter { it.completed }.map { it.lessonId }.toSet()
                )
            }.collect { _state.value = it }
        }
    }
}
