package com.hackpath.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Difficulty { BEGINNER, INTERMEDIATE, ADVANCED, EXPERT }

enum class TrackCategory {
    FOUNDATIONS,
    WEB_DEV,
    NETWORKING,
    SECURITY_BASICS,
    RED_TEAM,
    BLUE_TEAM
}

data class Track(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: String,
    val category: TrackCategory,
    val difficulty: Difficulty,
    val xpReward: Int,
    val courses: List<Course>
)

data class Course(
    val id: String,
    val trackId: String,
    val title: String,
    val description: String,
    val difficulty: Difficulty,
    val xpReward: Int,
    val lessons: List<Lesson>
)

data class Lesson(
    val id: String,
    val courseId: String,
    val order: Int,
    val title: String,
    val content: String,
    val codeExample: String?,
    val codeLanguage: String,
    val quiz: Quiz?
)

data class Quiz(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

@Entity(tableName = "progress")
data class LessonProgress(
    @PrimaryKey val lessonId: String,
    val completed: Boolean = false,
    val quizPassed: Boolean = false,
    val completedAt: Long = 0L
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 0,
    val username: String = "Hacker",
    val xp: Int = 0,
    val level: Int = 1,
    val streak: Int = 0,
    val lastActiveDate: Long = 0L,
    val completedLessons: Int = 0,
    val completedCourses: Int = 0
) {
    val levelTitle: String get() = when {
        level < 5  -> "Script Kiddie"
        level < 10 -> "Code Monkey"
        level < 15 -> "Dev Ninja"
        level < 20 -> "Net Stalker"
        level < 25 -> "Pen Tester"
        level < 30 -> "Red Teamer"
        else       -> "Elite Hacker"
    }

    val xpToNextLevel: Int get() = level * 500
}
