package com.elegoo.robotcoder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elegoo.robotcoder.bluetooth.BluetoothPermissionHelper
import com.elegoo.robotcoder.bluetooth.RoverBluetoothManager
import com.elegoo.robotcoder.model.ConnectionStatus
import com.elegoo.robotcoder.model.DiscoveredBluetoothDevice
import com.elegoo.robotcoder.model.displayLabel
import com.elegoo.robotcoder.utils.AppConstants
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * UI state for the Connect Rover screen.
 */
data class ConnectRoverUiState(
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val statusLabel: String = ConnectionStatus.DISCONNECTED.displayLabel(),
    val message: String = AppConstants.BLUETOOTH_MILESTONE_MESSAGE,
    val discoveredDevices: List<DiscoveredBluetoothDevice> = emptyList(),
    val selectedDeviceAddress: String? = null,
    val isScanning: Boolean = false,
    val isScanEnabled: Boolean = true,
    val isStopScanEnabled: Boolean = false,
    val isConnectEnabled: Boolean = false,
    val isDisconnectEnabled: Boolean = false,
    val isReconnectEnabled: Boolean = false,
)

/**
 * ViewModel for the Connect Rover screen.
 *
 * This class coordinates UI intent with the Bluetooth manager while keeping Android BLE callbacks
 * out of Compose. It uses AndroidViewModel so the manager can safely use the application context
 * without leaking an Activity.
 */
class ConnectRoverViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val bluetoothManager = RoverBluetoothManager(application.applicationContext)

    val uiState: StateFlow<ConnectRoverUiState> = bluetoothManager.state
        .map { bluetoothState ->
            val isConnected = bluetoothState.connectionStatus == ConnectionStatus.CONNECTED
            val isConnecting = bluetoothState.connectionStatus == ConnectionStatus.CONNECTING
            val hasSelectedDevice = bluetoothState.selectedDeviceAddress != null

            ConnectRoverUiState(
                connectionStatus = bluetoothState.connectionStatus,
                statusLabel = bluetoothState.connectionStatus.displayLabel(),
                message = bluetoothState.message,
                discoveredDevices = bluetoothState.discoveredDevices,
                selectedDeviceAddress = bluetoothState.selectedDeviceAddress,
                isScanning = bluetoothState.isScanning,
                isScanEnabled = !bluetoothState.isScanning && !isConnecting,
                isStopScanEnabled = bluetoothState.isScanning,
                isConnectEnabled = hasSelectedDevice && !isConnected && !isConnecting,
                isDisconnectEnabled = isConnected || isConnecting,
                isReconnectEnabled = hasSelectedDevice && !isConnected && !isConnecting,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ConnectRoverUiState(message = "Ready to scan for the rover."),
        )

    /**
     * Returns the Android runtime permissions required for BLE on this device.
     */
    fun requiredPermissions(): Array<String> = BluetoothPermissionHelper.requiredPermissions()

    /**
     * Starts scanning after the screen confirms permissions have been granted.
     */
    fun startScan() {
        bluetoothManager.startScan()
    }

    /**
     * Stops an active BLE scan.
     */
    fun stopScan() {
        bluetoothManager.stopScan()
    }

    /**
     * Marks a discovered device as selected for connect and reconnect actions.
     */
    fun selectDevice(address: String) {
        bluetoothManager.selectDevice(address)
    }

    /**
     * Connects to the selected BLE device.
     */
    fun connect() {
        bluetoothManager.connectSelectedDevice()
    }

    /**
     * Attempts to reconnect to the last selected BLE device.
     */
    fun reconnect() {
        bluetoothManager.reconnect()
    }

    /**
     * Disconnects from the active BLE device.
     */
    fun disconnect() {
        bluetoothManager.disconnect()
    }

    /**
     * Updates the user-facing state when Android permissions are denied.
     */
    fun onPermissionsDenied() {
        bluetoothManager.handlePermissionsDenied()
    }
}
