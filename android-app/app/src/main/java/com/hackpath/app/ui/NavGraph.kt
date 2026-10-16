package com.hackpath.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hackpath.app.ui.screens.*

sealed class Screen(val route: String) {
    object Home     : Screen("home")
    object Track    : Screen("track/{trackId}") {
        fun go(id: String) = "track/$id"
    }
    object Course   : Screen("course/{courseId}") {
        fun go(id: String) = "course/$id"
    }
    object Lesson   : Screen("lesson/{lessonId}") {
        fun go(id: String) = "lesson/$id"
    }
    object Profile  : Screen("profile")
}

@Composable
fun HackPathNavGraph() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(
                onTrackClick   = { nav.navigate(Screen.Track.go(it)) },
                onProfileClick = { nav.navigate(Screen.Profile.route) }
            )
        }

        composable(
            route = Screen.Track.route,
            arguments = listOf(navArgument("trackId") { type = NavType.StringType })
        ) {
            TrackScreen(
                onCourseClick = { nav.navigate(Screen.Course.go(it)) },
                onBack        = { nav.popBackStack() }
            )
        }

        composable(
            route = Screen.Course.route,
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) {
            CourseScreen(
                onLessonClick = { nav.navigate(Screen.Lesson.go(it)) },
                onBack        = { nav.popBackStack() }
            )
        }

        composable(
            route = Screen.Lesson.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) {
            LessonScreen(onBack = { nav.popBackStack() })
        }

        composable(Screen.Profile.route) {
            ProfileScreen(onBack = { nav.popBackStack() })
        }
    }
}
