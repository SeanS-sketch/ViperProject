package com.elegoo.robotcoder.model

import java.util.UUID

/**
 * UI-only representation of a block placed on the visual programming workspace.
 */
data class Block(
    val id: String = UUID.randomUUID().toString(),
    val type: BlockType,
    val x: Float,
    val y: Float,
)

enum class BlockType(val label: String) {
    Forward("Forward"),
    Backward("Backward"),
    TurnLeft("Turn Left"),
    TurnRight("Turn Right"),
    Stop("Stop"),
}
