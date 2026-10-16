package com.hackpath.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hackpath.app.data.model.Difficulty
import com.hackpath.app.data.model.Track
import com.hackpath.app.ui.theme.*
import com.hackpath.app.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onTrackClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "> HackPath",
                        fontFamily = FontFamily.Monospace,
                        color = HackGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, "Profile", tint = HackGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HackDark
                )
            )
        },
        containerColor = HackDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                XpProgressCard(
                    profile  = state.profile,
                    completed = state.completedLessonIds.size
                )
            }
            item {
                Text(
                    "// Learning Tracks",
                    color = HackGreen,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            items(state.tracks) { track ->
                val lessonIds = track.courses.flatMap { it.lessons }.map { it.id }.toSet()
                val done = state.completedLessonIds.intersect(lessonIds).size
                val total = lessonIds.size
                TrackCard(
                    track     = track,
                    completed = done,
                    total     = total,
                    onClick   = { onTrackClick(track.id) }
                )
            }
        }
    }
}

@Composable
private fun XpProgressCard(profile: com.hackpath.app.data.model.UserProfile, completed: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = HackCard),
        shape    = RoundedCornerShape(12.dp),
        border   = BorderStroke(1.dp, HackGreen.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        profile.username,
                        color      = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp
                    )
                    Text(
                        profile.levelTitle,
                        color      = HackGreen,
                        fontFamily = FontFamily.Monospace,
                        fontSize   = 12.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "LVL ${profile.level}",
                        color      = HackYellow,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp
                    )
                    Text(
                        "${profile.xp} XP",
                        color    = Color(0xFFAAAAAA),
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            val progress = (profile.xp % profile.xpToNextLevel).toFloat() / profile.xpToNextLevel
            LinearProgressIndicator(
                progress       = { progress },
                modifier       = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color          = HackGreen,
                trackColor     = Color(0xFF333333)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "$completed lessons completed · ${profile.xpToNextLevel - (profile.xp % profile.xpToNextLevel)} XP to next level",
                color    = Color(0xFF888888),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun TrackCard(
    track: Track,
    completed: Int,
    total: Int,
    onClick: () -> Unit
) {
    val diffColor = when (track.difficulty) {
        Difficulty.BEGINNER     -> Color(0xFF4CAF50)
        Difficulty.INTERMEDIATE -> HackYellow
        Difficulty.ADVANCED     -> HackOrange
        Difficulty.EXPERT       -> HackRed
    }
    val progress = if (total > 0) completed.toFloat() / total else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = HackCard),
        shape  = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, diffColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        brush  = Brush.radialGradient(listOf(diffColor.copy(0.2f), Color.Transparent)),
                        shape  = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(track.icon, fontSize = 26.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    track.title,
                    color      = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
                Text(
                    track.subtitle,
                    color    = Color(0xFFAAAAAA),
                    fontSize = 12.sp,
                    maxLines = 2
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier             = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment    = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress   = { progress },
                        modifier   = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color      = diffColor,
                        trackColor = Color(0xFF333333)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "$completed/$total",
                        color      = diffColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize   = 11.sp
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = diffColor.copy(alpha = 0.15f)
            ) {
                Text(
                    track.difficulty.name,
                    color      = diffColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize   = 10.sp,
                    modifier   = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}
