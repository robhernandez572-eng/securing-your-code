package com.hackpath.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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
import com.hackpath.app.viewmodel.GameViewModel
import com.hackpath.app.viewmodel.LessonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    onBack: () -> Unit,
    vm: LessonViewModel = hiltViewModel(),
    gameVm: GameViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val lesson = state.lesson ?: return
    val charState by gameVm.charState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        lesson.title,
                        fontFamily = FontFamily.Monospace,
                        color      = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = HackGreen)
                    }
                },
                actions = {
                    IconButton(onClick = { charState.selectedCharacterId?.let { gameVm.speakVoiceLine(it) } }) {
                        Text("🔊", fontSize = 18.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HackDark)
            )
        },
        containerColor = HackDark
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Content section
                ContentSection(lesson.content)

                // Code example
                if (lesson.codeExample != null) {
                    CodeBlock(
                        code     = lesson.codeExample,
                        language = lesson.codeLanguage
                    )
                }

                // Quiz
                if (lesson.quiz != null && !state.progress?.completed.let { it == true && state.quizSubmitted }) {
                    QuizSection(
                        quiz          = lesson.quiz,
                        selectedIndex = state.quizAnswerIndex,
                        submitted     = state.quizSubmitted,
                        onSelect      = vm::selectQuizAnswer,
                        onSubmit      = vm::submitQuiz
                    )
                } else if (lesson.quiz == null && state.progress?.completed != true) {
                    Button(
                        onClick  = vm::markRead,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = HackGreen),
                        shape    = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            "Mark Complete  +20 XP",
                            color      = Color.Black,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))
            }

            // XP banner
            AnimatedVisibility(
                visible = state.showXpBanner,
                enter   = slideInVertically { -it } + fadeIn(),
                exit    = slideOutVertically { -it } + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                XpBanner(xp = state.xpEarned, onDismiss = vm::dismissXpBanner)
            }
        }
    }
}

@Composable
private fun ContentSection(content: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = HackCard),
        shape  = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFF333333))
    ) {
        // Simple text rendering — a production app would use a markdown renderer
        Text(
            text     = content,
            color    = Color(0xFFE0E0E0),
            fontSize = 14.sp,
            lineHeight = 22.sp,
            modifier   = Modifier.padding(16.dp),
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun CodeBlock(code: String, language: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2D2D2D), RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                "// $language",
                color      = HackGreen.copy(0.7f),
                fontFamily = FontFamily.Monospace,
                fontSize   = 12.sp
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E), RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                .border(BorderStroke(1.dp, Color(0xFF333333)), RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                .padding(16.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            Text(
                text       = code,
                color      = Color(0xFFD4D4D4),
                fontFamily = FontFamily.Monospace,
                fontSize   = 13.sp,
                lineHeight  = 20.sp
            )
        }
    }
}

@Composable
private fun QuizSection(
    quiz: com.hackpath.app.data.model.Quiz,
    selectedIndex: Int?,
    submitted: Boolean,
    onSelect: (Int) -> Unit,
    onSubmit: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            "// Knowledge Check",
            color      = HackGreen,
            fontFamily = FontFamily.Monospace,
            fontSize   = 13.sp
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = HackCard),
            shape  = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, HackGreen.copy(0.3f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    quiz.question,
                    color      = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp
                )
                quiz.options.forEachIndexed { i, option ->
                    val isSelected = selectedIndex == i
                    val isCorrect  = submitted && i == quiz.correctIndex
                    val isWrong    = submitted && isSelected && i != quiz.correctIndex

                    val borderColor = when {
                        isCorrect  -> HackGreen
                        isWrong    -> HackRed
                        isSelected -> HackBlue
                        else       -> Color(0xFF444444)
                    }
                    val bgColor = when {
                        isCorrect  -> HackGreen.copy(0.1f)
                        isWrong    -> HackRed.copy(0.1f)
                        isSelected -> HackBlue.copy(0.1f)
                        else       -> Color.Transparent
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgColor)
                            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(8.dp))
                            .clickable(enabled = !submitted) { onSelect(i) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ('A' + i).toString(),
                            color      = borderColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 13.sp,
                            modifier   = Modifier.padding(end = 10.dp)
                        )
                        Text(option, color = Color(0xFFDDDDDD), fontSize = 14.sp)
                    }
                }

                if (submitted) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "💡 ${quiz.explanation}",
                        color    = Color(0xFFBBBBBB),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                } else if (selectedIndex != null) {
                    Button(
                        onClick  = onSubmit,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = HackGreen),
                        shape    = RoundedCornerShape(8.dp)
                    ) {
                        Text("Submit Answer", color = Color.Black, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun XpBanner(xp: Int, onDismiss: () -> Unit) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2500)
        onDismiss()
    }
    Card(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = HackGreen),
        shape    = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier          = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Star, null, tint = Color.Black, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "+$xp XP earned!",
                color      = Color.Black,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp
            )
        }
    }
}
