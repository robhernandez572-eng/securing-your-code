package com.hackpath.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hackpath.app.ui.theme.*
import com.hackpath.app.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val profile = state.profile

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Profile", fontFamily = FontFamily.Monospace, color = Color.White)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Avatar + rank
                Card(
                    colors = CardDefaults.cardColors(containerColor = HackCard),
                    shape  = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, HackGreen.copy(0.4f))
                ) {
                    Column(
                        modifier             = Modifier.padding(20.dp).fillMaxWidth(),
                        horizontalAlignment  = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(HackGreen.copy(0.15f), CircleShape)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 36.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            profile.username,
                            color      = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 22.sp
                        )
                        Text(
                            profile.levelTitle,
                            color      = HackGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize   = 14.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier             = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem("LEVEL", "${profile.level}", HackYellow)
                            StatItem("XP", "${profile.xp}", HackGreen)
                            StatItem("LESSONS", "${profile.completedLessons}", HackBlue)
                        }
                        Spacer(Modifier.height(14.dp))
                        val pct = (profile.xp % profile.xpToNextLevel).toFloat() / profile.xpToNextLevel
                        LinearProgressIndicator(
                            progress   = { pct },
                            modifier   = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color      = HackGreen,
                            trackColor = Color(0xFF333333)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${profile.xpToNextLevel - (profile.xp % profile.xpToNextLevel)} XP to Level ${profile.level + 1}",
                            color      = Color(0xFF888888),
                            fontFamily = FontFamily.Monospace,
                            fontSize   = 11.sp
                        )
                    }
                }
            }

            item {
                Text(
                    "// Rank Progression",
                    color      = HackGreen,
                    fontFamily = FontFamily.Monospace,
                    fontSize   = 13.sp
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = HackCard),
                    shape  = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF333333))
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        RankRow("Script Kiddie",  1,  5,  profile.level, Color(0xFF4CAF50))
                        RankRow("Code Monkey",    5,  10, profile.level, HackYellow)
                        RankRow("Dev Ninja",      10, 15, profile.level, HackOrange)
                        RankRow("Net Stalker",    15, 20, profile.level, Color(0xFFFF6B35))
                        RankRow("Pen Tester",     20, 25, profile.level, HackRed)
                        RankRow("Red Teamer",     25, 30, profile.level, Color(0xFFCC0000))
                        RankRow("Elite Hacker",   30, 99, profile.level, HackGreen)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            color      = color,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize   = 22.sp
        )
        Text(
            label,
            color      = Color(0xFF888888),
            fontFamily = FontFamily.Monospace,
            fontSize   = 10.sp
        )
    }
}

@Composable
private fun RankRow(title: String, from: Int, to: Int, currentLevel: Int, color: Color) {
    val achieved = currentLevel >= from
    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(if (achieved) color else Color(0xFF444444), CircleShape)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            title,
            color      = if (achieved) color else Color(0xFF555555),
            fontFamily = FontFamily.Monospace,
            fontWeight = if (achieved) FontWeight.SemiBold else FontWeight.Normal,
            fontSize   = 13.sp,
            modifier   = Modifier.weight(1f)
        )
        Text(
            "Lv. $from",
            color      = if (achieved) color.copy(0.6f) else Color(0xFF444444),
            fontFamily = FontFamily.Monospace,
            fontSize   = 11.sp
        )
    }
}
