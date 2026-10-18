package com.hackpath.app.data.database

import androidx.room.*
import com.hackpath.app.data.model.LessonProgress
import com.hackpath.app.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM progress WHERE lessonId = :lessonId")
    suspend fun getProgress(lessonId: String): LessonProgress?

    @Query("SELECT * FROM progress")
    fun getAllProgress(): Flow<List<LessonProgress>>

    @Query("SELECT COUNT(*) FROM progress WHERE completed = 1")
    fun completedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: LessonProgress)

    @Query("SELECT lessonId FROM progress WHERE completed = 1")
    suspend fun completedLessonIds(): List<String>
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 0")
    fun getProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: UserProfile)
}
