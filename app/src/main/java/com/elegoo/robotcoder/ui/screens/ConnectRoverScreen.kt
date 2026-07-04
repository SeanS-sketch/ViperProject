package com.elegoo.robotcoder.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elegoo.robotcoder.model.ConnectionStatus
import com.elegoo.robotcoder.model.DiscoveredBluetoothDevice
import com.elegoo.robotcoder.ui.components.AppCard
import com.elegoo.robotcoder.ui.components.AppTopBar
import com.elegoo.robotcoder.ui.components.PrimaryButton
import com.elegoo.robotcoder.ui.components.SecondaryButton
import com.elegoo.robotcoder.ui.components.StatusIndicator
import com.elegoo.robotcoder.ui.components.statusColorForLabel
import com.elegoo.robotcoder.viewmodel.ConnectRoverViewModel

/**
 * Connect Rover screen for Milestone 2 BLE scanning and connection management.
 */
@Composable
fun ConnectRoverScreen(
    viewModel: ConnectRoverViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pendingPermissionAction = remember { mutableStateOf<(() -> Unit)?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { grantResults ->
        if (grantResults.values.all { it }) {
            pendingPermissionAction.value?.invoke()
        } else {
            viewModel.onPermissionsDenied()
        }
        pendingPermissionAction.value = null
    }

    fun requestBluetoothPermissionsThen(action: () -> Unit) {
        pendingPermissionAction.value = action
        permissionLauncher.launch(viewModel.requiredPermissions())
    }

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
            AppCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
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

                    if (uiState.isScanning || uiState.connectionStatus == ConnectionStatus.CONNECTING) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PrimaryButton(
                    text = "Scan",
                    onClick = { requestBluetoothPermissionsThen(viewModel::startScan) },
                    enabled = uiState.isScanEnabled,
                    modifier = Modifier.weight(1f),
                )

                SecondaryButton(
                    text = "Stop Scan",
                    onClick = viewModel::stopScan,
                    enabled = uiState.isStopScanEnabled,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SecondaryButton(
                    text = "Connect",
                    onClick = { requestBluetoothPermissionsThen(viewModel::connect) },
                    enabled = uiState.isConnectEnabled,
                    modifier = Modifier.weight(1f),
                )

                SecondaryButton(
                    text = "Reconnect",
                    onClick = { requestBluetoothPermissionsThen(viewModel::reconnect) },
                    enabled = uiState.isReconnectEnabled,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                text = "Disconnect",
                onClick = viewModel::disconnect,
                enabled = uiState.isDisconnectEnabled,
            )

            Spacer(modifier = Modifier.height(24.dp))

            DeviceList(
                devices = uiState.discoveredDevices,
                selectedDeviceAddress = uiState.selectedDeviceAddress,
                onDeviceSelected = viewModel::selectDevice,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun DeviceList(
    devices: List<DiscoveredBluetoothDevice>,
    selectedDeviceAddress: String?,
    onDeviceSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Text(
            text = "Discovered Devices",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (devices.isEmpty()) {
            Text(
                text = "No devices found yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(devices, key = { it.address }) { device ->
                    DeviceRow(
                        device = device,
                        isSelected = device.address == selectedDeviceAddress,
                        onClick = { onDeviceSelected(device.address) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceRow(
    device: DiscoveredBluetoothDevice,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = device.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = "${device.rssi} dBm",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
