package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyQuestionEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.EduViewModel
import com.example.ui.viewmodel.UiState

@Composable
fun HomeScreen(
    viewModel: EduViewModel,
    onNavigateToSaved: () -> Unit,
    onNavigateToParent: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userProgress by viewModel.userProgress.collectAsState()
    val questionState by viewModel.questionState.collectAsState()
    val allQuestions by viewModel.studyQuestions.collectAsState()

    var searchInput by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("All") }

    val subjectCategories = listOf(
        "All" to "🌟",
        "Science" to "🔬",
        "Math" to "📐",
        "History" to "📜",
        "Geography" to "🌍",
        "Space" to "🌌",
        "Tech" to "💻",
        "Nature" to "🌿"
    )

    // Curated quick random study prompts
    val promptSuggestions = listOf(
        "Why is the sky blue? ☀️",
        "How do black holes work? 🕳️",
        "Explain fractions with pizza 🍕",
        "Who built the Pyramids of Giza? 🏛️",
        "Why do we have 4 seasons? 🌍",
        "How does AI learn from data? 🤖",
        "Why is ocean water salty? 🌊",
        "How do airplanes stay in the air? ✈️"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SoftSkyBackground)
            .padding(horizontal = 16.dp)
            .testTag("home_study_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(KidsCyanPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔍", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "EduAI Study Search",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDarkHeader
                        )
                        Text(
                            text = "Hi ${userProgress.name}! • Level ${userProgress.level} Scholar",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextDarkHeader
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFEDD5),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFED7AA))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🔥 ${userProgress.streakDays}d Streak", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC2410C))
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onNavigateToParent,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Parent Gate", tint = TextDarkHeader)
                    }
                }
            }
        }

        // Google Search Style Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🔎 Ask Any Study or Homework Question:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkHeader
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = searchInput,
                        onValueChange = { searchInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("study_search_input"),
                        shape = RoundedCornerShape(16.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color(0xFF000000),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF000000),
                            unfocusedTextColor = Color(0xFF000000),
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            cursorColor = KidsCyanPrimary,
                            focusedBorderColor = KidsCyanPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedPlaceholderColor = Color(0xFF475569),
                            unfocusedPlaceholderColor = Color(0xFF64748B)
                        ),
                        placeholder = {
                            Text(
                                "e.g. Why is the sky blue? or Solve 3x + 6 = 18...",
                                fontSize = 13.sp,
                                color = Color(0xFF475569)
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = KidsCyanPrimary)
                        },
                        trailingIcon = {
                            if (searchInput.isNotBlank()) {
                                IconButton(onClick = { searchInput = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF000000))
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchInput.isNotBlank()) {
                                    viewModel.askStudyQuestion(searchInput, selectedSubject)
                                }
                            }
                        ),
                        singleLine = false,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (searchInput.isNotBlank()) {
                                    viewModel.askStudyQuestion(searchInput, selectedSubject)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("ask_ai_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KidsCyanPrimary)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Search & Answer", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val randomQ = viewModel.getRandomQuestion(if (selectedSubject != "All") selectedSubject else null)
                                searchInput = randomQ
                                viewModel.askStudyQuestion(randomQ, selectedSubject)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("random_question_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = KidsCoralOrange),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, KidsCoralOrange)
                        ) {
                            Text("🎲 Random Question", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = KidsCoralOrange)
                        }
                    }
                }
            }
        }

        // Subject Badges Selector
        item {
            Text(
                text = "📚 Explore by Subject:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarkHeader
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(subjectCategories) { (subj, emoji) ->
                    FilterChip(
                        selected = (selectedSubject == subj),
                        onClick = {
                            selectedSubject = subj
                            val randomQ = viewModel.getRandomQuestion(if (subj != "All") subj else null)
                            searchInput = randomQ
                        },
                        label = {
                            Text("$emoji $subj", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KidsCyanPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = TextDarkHeader
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Quick Suggestions Row
        item {
            Text(
                text = "💡 Popular Study Prompts:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarkHeader
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(promptSuggestions) { prompt ->
                    SuggestionChip(
                        onClick = {
                            searchInput = prompt
                            viewModel.askStudyQuestion(prompt, selectedSubject)
                        },
                        label = {
                            Text(prompt, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDarkHeader)
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Active AI Answer Result Card
        item {
            when (val state = questionState) {
                is UiState.Loading -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = KidsCyanPrimary, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Searching & synthesizing AI study answer...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkHeader
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Gathering step-by-step facts across trusted educational databases...",
                                fontSize = 12.sp,
                                color = TextDarkHeader
                            )
                        }
                    }
                }

                is UiState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("⚠️ Could not load search result", fontWeight = FontWeight.Bold, color = Color.Red)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(state.message, fontSize = 12.sp, color = TextDarkHeader)
                        }
                    }
                }

                is UiState.Success -> {
                    val entity = state.data
                    ActiveAnswerCard(
                        entity = entity,
                        onToggleStar = { viewModel.toggleStar(entity) },
                        onRelatedClick = { relatedQ ->
                            searchInput = relatedQ
                            viewModel.askStudyQuestion(relatedQ, entity.subject)
                        },
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Study Answer", "${entity.questionText}\n\n${entity.directAnswer}\n\n${entity.detailedSteps}")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied answer to clipboard! 📋", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                is UiState.Idle -> {
                    // Show most recent question or intro card
                    if (allQuestions.isNotEmpty()) {
                        val latest = allQuestions.first()
                        ActiveAnswerCard(
                            entity = latest,
                            onToggleStar = { viewModel.toggleStar(latest) },
                            onRelatedClick = { relatedQ ->
                                searchInput = relatedQ
                                viewModel.askStudyQuestion(relatedQ, latest.subject)
                            },
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Study Answer", "${latest.questionText}\n\n${latest.directAnswer}\n\n${latest.detailedSteps}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied answer to clipboard! 📋", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }

        // Recent Questions History preview
        if (allQuestions.size > 1) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🕒 Recent Search Questions:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkHeader
                    )

                    TextButton(onClick = onNavigateToSaved) {
                        Text("View All (${allQuestions.size}) →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KidsCyanPrimary)
                    }
                }
            }

            items(allQuestions.take(4)) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            searchInput = item.questionText
                            viewModel.askStudyQuestion(item.questionText, item.subject)
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.questionText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkHeader,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.directAnswer,
                                fontSize = 12.sp,
                                color = TextDarkHeader,
                                maxLines = 1
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleStar(item) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (item.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Star",
                                tint = if (item.isStarred) KidsAmberAccent else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ActiveAnswerCard(
    entity: StudyQuestionEntity,
    onToggleStar: () -> Unit,
    onRelatedClick: (String) -> Unit,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_answer_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (entity.subject.lowercase()) {
                        "science", "nature" -> Color(0xFFD1FAE5)
                        "space" -> Color(0xFFEDE9FE)
                        "math" -> Color(0xFFFFEDD5)
                        "history" -> Color(0xFFFEF3C7)
                        "tech" -> Color(0xFFE0E7FF)
                        else -> Color(0xFFF1F5F9)
                    }
                ) {
                    Text(
                        text = "⚡ AI Search Overview • ${entity.subject}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextDarkHeader)
                    }
                    IconButton(onClick = onToggleStar, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (entity.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star",
                            tint = if (entity.isStarred) KidsAmberAccent else Color(0xFF94A3B8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Title
            Text(
                text = entity.questionText,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDarkHeader
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Direct AI Answer
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF0FDF4),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "💡 Direct Answer:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entity.directAnswer,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 21.sp,
                        color = TextDarkHeader
                    )
                }
            }

            // Key Points / Steps
            if (entity.detailedSteps.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "📋 Key Concepts & Step-by-Step Breakdown:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkHeader
                )
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SoftSkyBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SleekCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = entity.detailedSteps,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = TextDarkHeader,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Did You Know Card
            if (entity.funFact.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("✨", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Did You Know?", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = entity.funFact,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = TextDarkHeader
                            )
                        }
                    }
                }
            }

            // Related Questions (People Also Ask)
            if (entity.relatedQuestionsCsv.isNotBlank()) {
                val relatedItems = entity.relatedQuestionsCsv.split("|").map { it.trim() }.filter { it.isNotBlank() }
                if (relatedItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "🔍 People Also Ask (Tap to Search):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkHeader
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        relatedItems.forEach { rel ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onRelatedClick(rel) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = rel,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextDarkHeader,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(Icons.Default.ArrowForward, contentDescription = "Ask", tint = KidsCyanPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
