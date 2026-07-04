package com.elegoo.robotcoder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.elegoo.robotcoder.ui.theme.StatusNeutral
import com.elegoo.robotcoder.ui.theme.StatusPositive
import com.elegoo.robotcoder.ui.theme.StatusWarning

/**
 * Small visual indicator used to communicate state such as connection status.
 */
@Composable
fun StatusIndicator(
    label: String,
    indicatorColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(indicatorColor),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/**
 * Maps common app states to consistent indicator colors.
 */
fun statusColorForLabel(label: String): Color = when (label.lowercase()) {
    "connected" -> StatusPositive
    "connecting", "scanning" -> StatusWarning
    else -> StatusNeutral
}
