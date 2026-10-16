package com.hackpath.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hackpath.app.data.model.Course
import com.hackpath.app.ui.theme.*
import com.hackpath.app.viewmodel.TrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackScreen(
    onCourseClick: (String) -> Unit,
    onBack: () -> Unit,
    vm: TrackViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val track = state.track ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${track.icon} ${track.title}",
                        fontFamily = FontFamily.Monospace,
                        color      = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = HackGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HackDark)
            )
        },
        containerColor = HackDark
    ) { padding ->
        LazyColumn(
            modifier        = Modifier.fillMaxSize().padding(padding),
            contentPadding  = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    track.subtitle,
                    color      = Color(0xFFAAAAAA),
                    fontSize   = 14.sp,
                    modifier   = Modifier.padding(bottom = 8.dp)
                )
            }
            items(track.courses) { course ->
                val done  = state.completedIds.intersect(course.lessons.map { it.id }.toSet()).size
                val total = course.lessons.size
                CourseRow(
                    course    = course,
                    completed = done,
                    total     = total,
                    onClick   = { onCourseClick(course.id) }
                )
            }
        }
    }
}

@Composable
private fun CourseRow(course: Course, completed: Int, total: Int, onClick: () -> Unit) {
    val allDone = completed == total && total > 0
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors   = CardDefaults.cardColors(containerColor = HackCard),
        shape    = RoundedCornerShape(10.dp),
        border   = BorderStroke(1.dp, if (allDone) HackGreen.copy(0.5f) else Color(0xFF333333))
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (allDone) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                contentDescription = null,
                tint = if (allDone) HackGreen else Color(0xFF555555),
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    course.title,
                    color      = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp
                )
                Text(
                    course.description,
                    color    = Color(0xFFAAAAAA),
                    fontSize = 12.sp,
                    maxLines = 2
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "$completed / $total lessons · ${course.xpReward} XP",
                    color      = HackGreen.copy(0.7f),
                    fontFamily = FontFamily.Monospace,
                    fontSize   = 11.sp
                )
            }
        }
    }
}
