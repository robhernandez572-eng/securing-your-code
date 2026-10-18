package com.hackpath.app.data.repository

import com.hackpath.app.data.database.ProgressDao
import com.hackpath.app.data.database.UserProfileDao
import com.hackpath.app.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepository @Inject constructor(
    private val progressDao: ProgressDao,
    private val profileDao: UserProfileDao,
    private val courseContent: CourseContent
) {
    fun getAllTracks(): List<Track> = courseContent.allTracks

    fun getTrack(trackId: String): Track? = courseContent.allTracks.find { it.id == trackId }

    fun getCourse(courseId: String): Course? =
        courseContent.allTracks.flatMap { it.courses }.find { it.id == courseId }

    fun getLesson(lessonId: String): Lesson? =
        courseContent.allTracks.flatMap { it.courses }.flatMap { it.lessons }.find { it.id == lessonId }

    suspend fun getLessonProgress(lessonId: String): LessonProgress? =
        progressDao.getProgress(lessonId)

    fun getAllProgress(): Flow<List<LessonProgress>> = progressDao.getAllProgress()

    suspend fun completedLessonIds(): Set<String> = progressDao.completedLessonIds().toSet()

    fun getProfile(): Flow<UserProfile?> = profileDao.getProfile()

    suspend fun completeLesson(lessonId: String, quizPassed: Boolean, xpEarned: Int) {
        progressDao.upsertProgress(
            LessonProgress(
                lessonId = lessonId,
                completed = true,
                quizPassed = quizPassed,
                completedAt = System.currentTimeMillis()
            )
        )
        val current = profileDao.getProfile().first() ?: UserProfile()
        val newXp = current.xp + xpEarned
        val newLevel = 1 + (newXp / 500)
        profileDao.upsertProfile(
            current.copy(
                xp = newXp,
                level = newLevel,
                completedLessons = current.completedLessons + 1
            )
        )
    }

    suspend fun ensureProfile() {
        val profile = profileDao.getProfile().first()
        if (profile == null) {
            profileDao.upsertProfile(UserProfile())
        }
    }
}
