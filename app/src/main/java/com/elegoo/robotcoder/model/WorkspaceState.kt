package com.elegoo.robotcoder.model

/**
 * In-memory, session-only state for the visual block editor.
 */
data class WorkspaceState(
    val blocks: List<Block> = emptyList(),
) {
    fun addBlock(type: BlockType, x: Float, y: Float): WorkspaceState {
        return copy(
            blocks = blocks + Block(
                type = type,
                x = x,
                y = y,
            ),
        )
    }

    fun removeBlock(id: String): WorkspaceState {
        return copy(blocks = blocks.filterNot { it.id == id })
    }

    fun updateBlockPosition(id: String, x: Float, y: Float): WorkspaceState {
        return copy(
            blocks = blocks.map { block ->
                if (block.id == id) {
                    block.copy(x = x, y = y)
                } else {
                    block
                }
            },
        )
    }
}
