package com.elegoo.robotcoder.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.elegoo.robotcoder.model.BlockType

@Composable
fun BlockView(
    type: BlockType,
    isDragging: Boolean,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (dragX: Float, dragY: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val elevation by animateDpAsState(
        targetValue = if (isDragging) 10.dp else 3.dp,
        label = "blockElevation",
    )
    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.04f else 1f,
        label = "blockScale",
    )
    val blockColor = blockColorFor(type)

    Card(
        modifier = modifier
            .size(width = 132.dp, height = 56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(type) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDragCancel = onDragEnd,
                    onDragEnd = onDragEnd,
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    },
                )
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = blockColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
    ) {
        Box(
            modifier = Modifier
                .size(width = 132.dp, height = 56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(blockColor),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = type.label,
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

fun blockColorFor(type: BlockType): Color {
    return when (type) {
        BlockType.Forward -> Color(0xFF2E7D5B)
        BlockType.Backward -> Color(0xFF6B6F2A)
        BlockType.TurnLeft -> Color(0xFF356D9A)
        BlockType.TurnRight -> Color(0xFF7B5EA7)
        BlockType.Stop -> Color(0xFF9B4D4D)
    }
}
