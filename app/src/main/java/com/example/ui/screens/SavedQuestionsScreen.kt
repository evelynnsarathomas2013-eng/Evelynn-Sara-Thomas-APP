package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyQuestionEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.EduViewModel

@Composable
fun SavedQuestionsScreen(
    viewModel: EduViewModel,
    onSelectQuestion: (StudyQuestionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val starredList by viewModel.starredQuestions.collectAsState()
    val allList by viewModel.studyQuestions.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Starred Notes, 1: All Search History
    var subjectFilter by remember { mutableStateOf("All") }

    val currentItems = (if (selectedTab == 0) starredList else allList).filter {
        subjectFilter == "All" || it.subject.equals(subjectFilter, ignoreCase = true)
    }

    val subjects = listOf("All", "Science", "Math", "History", "Geography", "Space", "Tech", "Nature")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SoftSkyBackground)
            .padding(16.dp)
            .testTag("saved_questions_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "📚 Study Notes & History",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkHeader
                )
                Text(
                    text = "${currentItems.size} study questions ready for review",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDarkHeader
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFEF3C7),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⭐ ${starredList.size} Saved", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = KidsCyanPrimary,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = (selectedTab == 0),
                onClick = { selectedTab = 0 },
                text = { Text("⭐ Starred Notes (${starredList.size})", fontWeight = FontWeight.Bold, color = TextDarkHeader) }
            )
            Tab(
                selected = (selectedTab == 1),
                onClick = { selectedTab = 1 },
                text = { Text("🕒 All History (${allList.size})", fontWeight = FontWeight.Bold, color = TextDarkHeader) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Subject Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(subjects) { subj ->
                FilterChip(
                    selected = (subjectFilter == subj),
                    onClick = { subjectFilter = subj },
                    label = { Text(subj, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = KidsCyanPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = TextDarkHeader
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (currentItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔍", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedTab == 0) "No starred study notes yet!" else "No study questions found.",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkHeader
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (selectedTab == 0) "Star (⭐) any question on the Search tab to save it here for fast revision." else "Ask or pick a random question to start building your study log!",
                            fontSize = 13.sp,
                            color = TextDarkHeader
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(currentItems, key = { it.id }) { item ->
                    StudyQuestionCard(
                        question = item,
                        onCardClick = { onSelectQuestion(item) },
                        onToggleStar = { viewModel.toggleStar(item) },
                        onDelete = { viewModel.deleteQuestion(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun StudyQuestionCard(
    question: StudyQuestionEntity,
    onCardClick: () -> Unit,
    onToggleStar: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("study_question_card_${question.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (question.subject.lowercase()) {
                        "science", "nature" -> Color(0xFFD1FAE5)
                        "space" -> Color(0xFFEDE9FE)
                        "math" -> Color(0xFFFFEDD5)
                        "history" -> Color(0xFFFEF3C7)
                        "tech" -> Color(0xFFE0E7FF)
                        else -> Color(0xFFF1F5F9)
                    }
                ) {
                    Text(
                        text = "• ${question.subject}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleStar,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (question.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star",
                            tint = if (question.isStarred) KidsAmberAccent else Color(0xFF94A3B8)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFCBD5E1)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = question.questionText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarkHeader
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = question.directAnswer,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = TextDarkHeader,
                maxLines = 3
            )

            if (question.detailedSteps.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SoftSkyBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = question.detailedSteps.lines().take(2).joinToString("\n"),
                        fontSize = 12.sp,
                        color = TextDarkHeader,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
