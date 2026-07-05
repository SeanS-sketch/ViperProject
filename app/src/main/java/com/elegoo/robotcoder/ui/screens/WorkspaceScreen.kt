package com.elegoo.robotcoder.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elegoo.robotcoder.model.Block
import com.elegoo.robotcoder.model.BlockType
import com.elegoo.robotcoder.ui.components.AppTopBar
import com.elegoo.robotcoder.ui.components.BlockPalette
import com.elegoo.robotcoder.ui.components.BlockView
import com.elegoo.robotcoder.viewmodel.WorkspaceViewModel
import kotlin.math.roundToInt

/**
 * Scratch-style visual editor for arranging UI-only blocks on a pan/zoom canvas.
 */
@Composable
fun WorkspaceScreen(
    viewModel: WorkspaceViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Programming Workspace") },
    ) { innerPadding ->
        BlockWorkspace(
            blocks = uiState.workspaceState.blocks,
            onAddBlock = viewModel::addBlock,
            onUpdateBlockPosition = viewModel::updateBlockPosition,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}

@Composable
private fun BlockWorkspace(
    blocks: List<Block>,
    onAddBlock: (BlockType, Float, Float) -> Unit,
    onUpdateBlockPosition: (String, Float, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(0.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                )
                .onSizeChanged { canvasSize = it }
                .pointerInput(Unit) {
                    detectTransformGestures { _, panChange, zoomChange, _ ->
                        val nextScale = (scale * zoomChange).coerceIn(0.45f, 2.5f)
                        scale = nextScale
                        pan += panChange
                    }
                },
        ) {
            WorkspaceGrid(
                pan = pan,
                scale = scale,
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(0f),
            )

            if (blocks.isEmpty()) {
                EmptyWorkspaceHint(modifier = Modifier.align(Alignment.Center))
            }

            blocks.forEach { block ->
                DraggableWorkspaceBlock(
                    block = block,
                    pan = pan,
                    scale = scale,
                    onUpdateBlockPosition = onUpdateBlockPosition,
                )
            }
        }

        BlockPalette(
            onBlockSelected = { type ->
                val x = ((canvasSize.width / 2f) - pan.x) / scale
                val y = ((canvasSize.height / 2f) - pan.y) / scale
                onAddBlock(type, x, y)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .background(MaterialTheme.colorScheme.surface),
        )
    }
}

@Composable
private fun DraggableWorkspaceBlock(
    block: Block,
    pan: Offset,
    scale: Float,
    onUpdateBlockPosition: (String, Float, Float) -> Unit,
) {
    var isDragging by remember(block.id) { mutableStateOf(false) }

    BlockView(
        type = block.type,
        isDragging = isDragging,
        onDragStart = { isDragging = true },
        onDragEnd = { isDragging = false },
        onDrag = { dragX, dragY ->
            onUpdateBlockPosition(
                block.id,
                block.x + (dragX / scale),
                block.y + (dragY / scale),
            )
        },
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (pan.x + (block.x * scale)).roundToInt(),
                    y = (pan.y + (block.y * scale)).roundToInt(),
                )
            }
            .zIndex(if (isDragging) 2f else 1f),
    )
}

@Composable
private fun WorkspaceGrid(
    pan: Offset,
    scale: Float,
    modifier: Modifier = Modifier,
) {
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
    val axisColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)

    Canvas(modifier = modifier) {
        val gridSpacing = 48f * scale
        if (gridSpacing < 14f) return@Canvas

        var x = positiveModulo(pan.x, gridSpacing)
        while (x < size.width) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f,
            )
            x += gridSpacing
        }

        var y = positiveModulo(pan.y, gridSpacing)
        while (y < size.height) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f,
            )
            y += gridSpacing
        }

        drawCircle(
            color = axisColor,
            radius = 5f,
            center = pan,
            style = Stroke(width = 2f),
        )
    }
}

@Composable
private fun EmptyWorkspaceHint(modifier: Modifier = Modifier) {
    Text(
        text = "Tap a block below to add it",
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
        textAlign = TextAlign.Center,
    )
}

private fun positiveModulo(value: Float, modulus: Float): Float {
    val result = value % modulus
    return if (result < 0f) result + modulus else result
}
