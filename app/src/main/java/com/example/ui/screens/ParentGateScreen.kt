package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.EduViewModel

@Composable
fun ParentGateScreen(
    viewModel: EduViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress by viewModel.userProgress.collectAsState()
    val starredList by viewModel.starredQuestions.collectAsState()
    val allQuestions by viewModel.studyQuestions.collectAsState()

    var isUnlocked by remember { mutableStateOf(false) }
    var mathAnswerInput by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    var editNameInput by remember { mutableStateOf(progress.name) }
    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SoftSkyBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("parent_gate_column")
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDarkHeader)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "🔒 Parent & Settings Gate",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarkHeader
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (!isUnlocked) {
            // Security Gate Math Challenge
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Parent Gate",
                        tint = KidsCyanPrimary,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Adults / Guardian Verification",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkHeader
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Solve this quick math problem to access controls:",
                        fontSize = 12.sp,
                        color = TextSubduedBody
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "What is 8 × 9?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = KidsCyanPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = mathAnswerInput,
                        onValueChange = { mathAnswerInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_math_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color(0xFF000000),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
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
                        placeholder = { Text("Enter answer...", color = Color(0xFF64748B)) }
                    )

                    if (errorMsg.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorMsg, fontSize = 12.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (mathAnswerInput.trim() == "72") {
                                isUnlocked = true
                                errorMsg = ""
                            } else {
                                errorMsg = "Incorrect answer. Try again!"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("unlock_parent_gate_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KidsCyanPrimary)
                    ) {
                        Text("Verify & Enter 🔓", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Parent Dashboard Unlocked
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "📊 Student Study Progress",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkHeader
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    StatRow("Student Name:", progress.name)
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))
                    StatRow("Current Level:", "Level ${progress.level} Scholar")
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))
                    StatRow("Total Learning EXP:", "${progress.exp} EXP")
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))
                    StatRow("Active Study Streak:", "${progress.streakDays} Days 🔥")
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))
                    StatRow("Total Questions Asked:", "${allQuestions.size}")
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))
                    StatRow("Starred Study Notes:", "${starredList.size} ⭐")

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "✏️ Update Student Profile Name:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkHeader
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = editNameInput,
                            onValueChange = { editNameInput = it },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color(0xFF000000),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF000000),
                                unfocusedTextColor = Color(0xFF000000),
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                cursorColor = KidsCyanPrimary,
                                focusedBorderColor = KidsCyanPrimary,
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (editNameInput.isNotBlank()) {
                                    viewModel.updateName(editNameInput)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KidsCyanPrimary)
                        ) {
                            Text("Save")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "⚙️ Study History Management:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkHeader
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showClearDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear All Study Questions History", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All Questions?", fontWeight = FontWeight.Bold, color = TextDarkHeader) },
            text = { Text("This will permanently remove all search history and saved questions.", color = TextDarkHeader) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Clear All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = TextDarkHeader)
                }
            }
        )
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = TextSubduedBody)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDarkHeader)
    }
}

