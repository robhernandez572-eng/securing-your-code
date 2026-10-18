package com.hackpath.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
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
import com.hackpath.app.ui.theme.*
import com.hackpath.app.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    onLessonClick: (String) -> Unit,
    onBack: () -> Unit,
    vm: CourseViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val course = state.course ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        course.title,
                        fontFamily = FontFamily.Monospace,
                        color      = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp
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
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = HackCard),
                    shape  = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, HackGreen.copy(0.2f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(course.description, color = Color(0xFFCCCCCC), fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "${course.lessons.size} lessons · ${course.xpReward} XP total",
                            color      = HackGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize   = 12.sp
                        )
                    }
                }
            }
            itemsIndexed(course.lessons) { index, lesson ->
                val isDone   = lesson.id in state.completedIds
                val isFirst  = index == 0
                val prevDone = index == 0 || course.lessons[index - 1].id in state.completedIds
                val unlocked = isFirst || prevDone

                LessonRow(
                    number   = index + 1,
                    title    = lesson.title,
                    isDone   = isDone,
                    unlocked = unlocked,
                    isLast   = index == course.lessons.lastIndex,
                    onClick  = { if (unlocked) onLessonClick(lesson.id) }
                )
            }
        }
    }
}

@Composable
private fun LessonRow(
    number: Int,
    title: String,
    isDone: Boolean,
    unlocked: Boolean,
    isLast: Boolean,
    onClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = when {
                            isDone    -> HackGreen
                            unlocked  -> HackCard
                            else      -> Color(0xFF1A1A1A)
                        },
                        shape = CircleShape
                    )
                    .then(
                        if (unlocked && !isDone)
                            Modifier.background(Color.Transparent, CircleShape)
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color.Black, modifier = Modifier.size(20.dp))
                } else if (!unlocked) {
                    Icon(Icons.Default.Lock, null, tint = Color(0xFF555555), modifier = Modifier.size(16.dp))
                } else {
                    Text(
                        "$number",
                        color      = HackGreen,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 14.sp
                    )
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(if (isDone) HackGreen.copy(0.5f) else Color(0xFF333333))
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isLast) 0.dp else 0.dp)
                .padding(bottom = 40.dp.takeIf { !isLast } ?: 0.dp)
                .clickable(enabled = unlocked, onClick = onClick)
        ) {
            Text(
                title,
                color      = if (unlocked) Color.White else Color(0xFF555555),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                fontSize   = 15.sp,
                modifier   = Modifier.padding(top = 8.dp)
            )
        }
    }
}
