package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class DrawPath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun InteractiveDrawingPad(
    modifier: Modifier = Modifier,
    onClear: () -> Unit = {}
) {
    val paths = remember { mutableStateListOf<DrawPath>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var selectedColor by remember { mutableStateOf(KidsCyanPrimary) }

    val colorOptions = listOf(
        KidsCyanPrimary,
        KidsVioletTertiary,
        KidsCoralOrange,
        KidsEmeraldGreen,
        Color(0xFF1E293B)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("drawing_pad_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Drawing Pad",
                        tint = KidsCyanPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "✏️ Math Scratchpad & AI Canvas",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkHeader
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    colorOptions.forEach { col ->
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(col)
                                .border(
                                    width = if (selectedColor == col) 2.dp else 0.dp,
                                    color = if (selectedColor == col) KidsAmberAccent else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                                .padding(2.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            paths.clear()
                            onClear()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Pad",
                            tint = Color.Red,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Canvas Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(14.dp))
                    .pointerInput(selectedColor) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val newP = Path().apply { moveTo(offset.x, offset.y) }
                                currentPath = newP
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                currentPath?.let { p ->
                                    val currentOffset = change.position
                                    p.lineTo(currentOffset.x, currentOffset.y)
                                    // Trigger recomposition
                                    currentPath = Path().apply { addPath(p) }
                                }
                            },
                            onDragEnd = {
                                currentPath?.let { p ->
                                    paths.add(DrawPath(p, selectedColor, 8f))
                                    currentPath = null
                                }
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    paths.forEach { drawP ->
                        drawPath(
                            path = drawP.path,
                            color = drawP.color,
                            style = Stroke(
                                width = drawP.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                    currentPath?.let { p ->
                        drawPath(
                            path = p,
                            color = selectedColor,
                            style = Stroke(
                                width = 8f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                if (paths.isEmpty() && currentPath == null) {
                    Text(
                        text = "Draw math equations, shapes, or notes here...",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(8.dp),
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}
