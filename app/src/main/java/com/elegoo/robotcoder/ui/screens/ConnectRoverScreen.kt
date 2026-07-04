package com.elegoo.robotcoder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elegoo.robotcoder.ui.components.AppCard
import com.elegoo.robotcoder.ui.components.AppTopBar
import com.elegoo.robotcoder.ui.components.PrimaryButton
import com.elegoo.robotcoder.ui.components.SecondaryButton
import com.elegoo.robotcoder.ui.components.StatusIndicator
import com.elegoo.robotcoder.ui.components.statusColorForLabel
import com.elegoo.robotcoder.viewmodel.ConnectRoverViewModel

/**
 * Placeholder Connect Rover screen that previews the future Bluetooth workflow.
 */
@Composable
fun ConnectRoverScreen(
    viewModel: ConnectRoverViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Connect Rover") },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Icon(
                imageVector = Icons.Default.Bluetooth,
                contentDescription = "Bluetooth",
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppCard {
                Text(
                    text = "Connection Status",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(12.dp))

                StatusIndicator(
                    label = uiState.statusLabel,
                    indicatorColor = statusColorForLabel(uiState.statusLabel),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Scan",
                onClick = {},
                enabled = uiState.isScanEnabled,
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                text = "Connect",
                onClick = {},
                enabled = uiState.isConnectEnabled,
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                text = "Disconnect",
                onClick = {},
                enabled = uiState.isDisconnectEnabled,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = uiState.milestoneMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
