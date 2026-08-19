package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.theme.*

@Composable
fun TeachableMachineSimulator(
    onGestureTested: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedGesture by remember { mutableStateOf("Thumbs Up 👍") }
    var confidenceScore by remember { mutableFloatStateOf(0.97f) }

    val gestureOptions = listOf(
        Triple("Thumbs Up 👍", "I Understand The Lesson!", Color(0xFF10B981)),
        Triple("Thumbs Down 👎", "I Need Extra Practice", Color(0xFFEF4444)),
        Triple("Wave Hand 👋", "Hello EduAI Buddy!", Color(0xFF3B82F6)),
        Triple("Hand Raised ✋", "I Have A Question", Color(0xFFF59E0B))
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("teachable_machine_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SoftSkyBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera Vision",
                        tint = KidsCyanPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "📷 Teachable Machine AI Vision",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkHeader
                        )
                        Text(
                            text = "Gesture Recognition Simulator",
                            fontSize = 12.sp,
                            color = TextSubduedBody
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = KidsAmberAccent.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Vision Model",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        color = KidsCoralOrange,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Camera preview simulation box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF334155)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = selectedGesture.takeLast(2), // Emoji
                            fontSize = 36.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Detected Signal: $selectedGesture",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Confidence bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { confidenceScore },
                            modifier = Modifier
                                .width(120.dp)
                                .height(8.dp)
                                .clip(CircleShape),
                            color = Color(0xFF34D399),
                            trackColor = Color(0xFF475569)
                        )
                        Text(
                            text = "${(confidenceScore * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34D399)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Tap a gesture to test classroom signal AI:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDarkHeader
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                gestureOptions.forEach { (gesture, meaning, badgeColor) ->
                    val isSelected = (selectedGesture == gesture)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedGesture = gesture
                                confidenceScore = (0.92f + Math.random() * 0.07f).toFloat()
                                onGestureTested()
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) badgeColor.copy(alpha = 0.15f) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) badgeColor else Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = gesture.takeLast(2),
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = gesture,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkHeader
                                    )
                                    Text(
                                        text = "AI Reaction: $meaning",
                                        fontSize = 11.sp,
                                        color = TextSubduedBody
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = badgeColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
