package com.hackpath.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackpath.app.data.model.Track
import com.hackpath.app.data.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrackUiState(
    val track: Track? = null,
    val completedIds: Set<String> = emptySet()
)

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val repo: CourseRepository,
    savedState: SavedStateHandle
) : ViewModel() {

    private val trackId: String = checkNotNull(savedState["trackId"])
    private val _state = MutableStateFlow(TrackUiState())
    val state: StateFlow<TrackUiState> = _state.asStateFlow()

    init {
        _state.update { it.copy(track = repo.getTrack(trackId)) }
        viewModelScope.launch {
            repo.getAllProgress().collect { list ->
                _state.update { it.copy(completedIds = list.filter { p -> p.completed }.map { p -> p.lessonId }.toSet()) }
            }
        }
    }
}
