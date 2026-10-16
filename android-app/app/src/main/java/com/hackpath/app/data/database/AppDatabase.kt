package com.hackpath.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hackpath.app.data.model.LessonProgress
import com.hackpath.app.data.model.UserProfile

@Database(
    entities = [LessonProgress::class, UserProfile::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao
    abstract fun userProfileDao(): UserProfileDao
}
