package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun NeuralNetVisualizer(
    modifier: Modifier = Modifier
) {
    var activeInputIndex by remember { mutableStateOf(0) }
    var selectedLearningRate by remember { mutableFloatStateOf(0.85f) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseProgress"
    )

    val inputLabels = listOf("User Query", "Math Drawing", "Voice Wave")
    val hiddenLabels = listOf("Pattern Match", "Socratic Rule", "Grammar Engine", "Safety Gate")
    val outputPredictions = when (activeInputIndex) {
        0 -> "AI Answer: 99.4% Confidence"
        1 -> "Math Steps: 98.7% Confidence"
        else -> "Speech Response: 99.1% Confidence"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("neural_net_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Deep midnight dark for neon net
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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
                Column {
                    Text(
                        text = "🧠 Interactive Neural Network Model",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                    Text(
                        text = "Grade 8 AI Model Representation",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = "Live Training",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        color = Color(0xFF34D399),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Input selector chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                inputLabels.forEachIndexed { idx, label ->
                    FilterChip(
                        selected = (activeInputIndex == idx),
                        onClick = { activeInputIndex = idx },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0284C7),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFF94A3B8)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Canvas visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val inputsX = w * 0.15f
                    val hiddenX = w * 0.50f
                    val outputX = w * 0.85f

                    val inputYs = listOf(h * 0.25f, h * 0.50f, h * 0.75f)
                    val hiddenYs = listOf(h * 0.15f, h * 0.38f, h * 0.62f, h * 0.85f)
                    val outputY = h * 0.50f

                    // Draw connection weights (synapses)
                    inputYs.forEachIndexed { iIdx, iY ->
                        val isSelectedInput = (iIdx == activeInputIndex)
                        hiddenYs.forEach { hY ->
                            drawLine(
                                color = if (isSelectedInput) Color(0x8038BDF8) else Color(0x2094A3B8),
                                start = Offset(inputsX, iY),
                                end = Offset(hiddenX, hY),
                                strokeWidth = if (isSelectedInput) 3f else 1.5f
                            )

                            // Signal pulse animation
                            if (isSelectedInput) {
                                val pulseX = inputsX + (hiddenX - inputsX) * pulseProgress
                                val pulseY = iY + (hY - iY) * pulseProgress
                                drawCircle(
                                    color = Color(0xFF38BDF8),
                                    radius = 4f,
                                    center = Offset(pulseX, pulseY)
                                )
                            }
                        }
                    }

                    hiddenYs.forEach { hY ->
                        drawLine(
                            color = Color(0x60818CF8),
                            start = Offset(hiddenX, hY),
                            end = Offset(outputX, outputY),
                            strokeWidth = 2f
                        )

                        val pulseX = hiddenX + (outputX - hiddenX) * pulseProgress
                        val pulseY = hY + (outputY - hY) * pulseProgress
                        drawCircle(
                            color = Color(0xFFA78BFA),
                            radius = 4f,
                            center = Offset(pulseX, pulseY)
                        )
                    }

                    // Draw Input Nodes
                    inputYs.forEachIndexed { idx, iY ->
                        val active = (idx == activeInputIndex)
                        drawCircle(
                            color = if (active) Color(0xFF0284C7) else Color(0xFF475569),
                            radius = if (active) 14f else 10f,
                            center = Offset(inputsX, iY)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = if (active) 6f else 4f,
                            center = Offset(inputsX, iY)
                        )
                    }

                    // Draw Hidden Nodes
                    hiddenYs.forEach { hY ->
                        drawCircle(
                            color = Color(0xFF6366F1),
                            radius = 12f,
                            center = Offset(hiddenX, hY)
                        )
                        drawCircle(
                            color = Color(0xFFC084FC),
                            radius = 5f,
                            center = Offset(hiddenX, hY)
                        )
                    }

                    // Draw Output Node
                    drawCircle(
                        color = Color(0xFF10B981),
                        radius = 16f,
                        center = Offset(outputX, outputY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 7f,
                        center = Offset(outputX, outputY)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Output prediction banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF064E3B)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡ Output Layer Prediction:",
                        fontSize = 12.sp,
                        color = Color(0xFFA7F3D0)
                    )
                    Text(
                        text = outputPredictions,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34D399)
                    )
                }
            }
        }
    }
}
