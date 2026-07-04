package com.elegoo.robotcoder.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.elegoo.robotcoder.model.ConnectionStatus
import com.elegoo.robotcoder.model.DiscoveredBluetoothDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Owns all BLE scanning, connection, and text-command transport for one rover.
 *
 * The manager intentionally has no Compose dependencies. UI code observes [state] and calls public
 * functions, while Android BLE callbacks remain contained here. This keeps Milestone 2 reusable when
 * later milestones add block execution and Arduino firmware.
 */
class RoverBluetoothManager(
    private val context: Context,
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    private val discoveredPlatformDevices = mutableMapOf<String, BluetoothDevice>()
    private var scanner: BluetoothLeScanner? = null
    private var gatt: BluetoothGatt? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null
    private var selectedAddress: String? = null

    private val _state = MutableStateFlow(BluetoothManagerState())
    val state: StateFlow<BluetoothManagerState> = _state.asStateFlow()

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device ?: return
            val address = device.address ?: return
            discoveredPlatformDevices[address] = device

            val deviceName = device.name?.takeIf { it.isNotBlank() } ?: "Unknown BLE Device"
            val visibleDevice = DiscoveredBluetoothDevice(
                name = deviceName,
                address = address,
                rssi = result.rssi,
            )
            val updatedDevices = (_state.value.discoveredDevices
                .filterNot { it.address == address } + visibleDevice)
                .sortedByDescending { it.rssi }

            _state.value = _state.value.copy(
                discoveredDevices = updatedDevices,
                message = "${updatedDevices.size} BLE device(s) found.",
            )
        }

        override fun onScanFailed(errorCode: Int) {
            _state.value = _state.value.copy(
                connectionStatus = ConnectionStatus.FAILED,
                isScanning = false,
                message = "Bluetooth scan failed. Error code: $errorCode.",
            )
        }
    }

    private val connectionCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            mainHandler.removeCallbacksAndMessages(CONNECTION_TIMEOUT_TOKEN)

            if (status != BluetoothGatt.GATT_SUCCESS) {
                closeGatt("Connection failed or was lost.")
                return
            }

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    _state.value = _state.value.copy(
                        connectionStatus = ConnectionStatus.CONNECTED,
                        message = "Connected. Discovering BLE services.",
                    )
                    gatt.discoverServices()
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    closeGatt("Disconnected from rover.")
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                _state.value = _state.value.copy(message = "Connected, but service discovery failed.")
                return
            }

            writeCharacteristic = findConfiguredWriteCharacteristic(gatt.services)
            val message = if (writeCharacteristic == null) {
                "Connected. Command UUIDs are not configured yet."
            } else {
                "Connected and ready to send text commands."
            }

            _state.value = _state.value.copy(
                connectionStatus = ConnectionStatus.CONNECTED,
                message = message,
            )
        }
    }

    /**
     * Starts a BLE scan after permissions have already been granted by the UI layer.
     */
    @SuppressLint("MissingPermission")
    fun startScan() {
        if (!BluetoothPermissionHelper.hasRequiredPermissions(context)) {
            _state.value = _state.value.copy(
                connectionStatus = ConnectionStatus.FAILED,
                message = "Bluetooth permission is required before scanning.",
            )
            return
        }

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            _state.value = _state.value.copy(
                connectionStatus = ConnectionStatus.FAILED,
                message = "Turn on Bluetooth before scanning.",
            )
            return
        }

        stopScan()
        discoveredPlatformDevices.clear()
        scanner = bluetoothAdapter.bluetoothLeScanner
        scanner?.startScan(scanCallback)
        _state.value = _state.value.copy(
            connectionStatus = ConnectionStatus.SCANNING,
            discoveredDevices = emptyList(),
            isScanning = true,
            message = "Scanning for BLE devices.",
        )

        mainHandler.postDelayed({
            if (_state.value.isScanning) {
                stopScan()
                val message = if (_state.value.discoveredDevices.isEmpty()) {
                    "No BLE devices found. Check that the rover is powered on and nearby."
                } else {
                    "Scan complete."
                }
                _state.value = _state.value.copy(message = message)
            }
        }, BluetoothConfig.SCAN_TIMEOUT_MILLIS)
    }

    /**
     * Stops an active BLE scan without clearing discovered devices.
     */
    @SuppressLint("MissingPermission")
    fun stopScan() {
        scanner?.stopScan(scanCallback)
        scanner = null
        if (_state.value.isScanning) {
            _state.value = _state.value.copy(
                connectionStatus = ConnectionStatus.DISCONNECTED,
                isScanning = false,
                message = "Scan stopped.",
            )
        }
    }

    /**
     * Selects a discovered BLE device so the UI can connect or reconnect to it.
     */
    fun selectDevice(address: String) {
        selectedAddress = address
        _state.value = _state.value.copy(
            selectedDeviceAddress = address,
            message = "Device selected.",
        )
    }

    /**
     * Opens a GATT connection to the selected BLE device.
     */
    @SuppressLint("MissingPermission")
    fun connectSelectedDevice() {
        val address = selectedAddress
        val device = address?.let { discoveredPlatformDevices[it] }
        if (address == null || device == null) {
            _state.value = _state.value.copy(message = "Select a BLE device before connecting.")
            return
        }

        if (!BluetoothPermissionHelper.hasRequiredPermissions(context)) {
            _state.value = _state.value.copy(
                connectionStatus = ConnectionStatus.FAILED,
                message = "Bluetooth permission is required before connecting.",
            )
            return
        }

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            _state.value = _state.value.copy(
                connectionStatus = ConnectionStatus.FAILED,
                message = "Turn on Bluetooth before connecting.",
            )
            return
        }

        stopScan()
        gatt?.close()
        writeCharacteristic = null
        _state.value = _state.value.copy(
            connectionStatus = ConnectionStatus.CONNECTING,
            message = "Connecting to selected device.",
        )
        gatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            device.connectGatt(context, false, connectionCallback, BluetoothDevice.TRANSPORT_LE)
        } else {
            device.connectGatt(context, false, connectionCallback)
        }

        mainHandler.postAtTime({
            if (_state.value.connectionStatus == ConnectionStatus.CONNECTING) {
                closeGatt("Connection timed out. Try reconnecting.")
            }
        }, CONNECTION_TIMEOUT_TOKEN, android.os.SystemClock.uptimeMillis() + BluetoothConfig.CONNECTION_TIMEOUT_MILLIS)
    }

    /**
     * Attempts to reconnect to the previously selected BLE device.
     */
    fun reconnect() {
        connectSelectedDevice()
    }

    /**
     * Records that the user denied Bluetooth permissions so the UI can explain the next step.
     */
    fun handlePermissionsDenied() {
        stopScan()
        _state.value = _state.value.copy(
            connectionStatus = ConnectionStatus.FAILED,
            message = "Bluetooth permission was denied. Grant permission to scan for the rover.",
        )
    }

    /**
     * Disconnects from the active BLE device and releases the GATT connection.
     */
    @SuppressLint("MissingPermission")
    fun disconnect() {
        stopScan()
        gatt?.disconnect()
        closeGatt("Disconnected from rover.")
    }

    /**
     * Sends a UTF-8 command string through the configured BLE write characteristic.
     */
    @SuppressLint("MissingPermission")
    fun sendMessage(command: String) {
        val currentGatt = gatt
        val characteristic = writeCharacteristic
        if (currentGatt == null || characteristic == null) {
            _state.value = _state.value.copy(
                message = "Command channel is not configured yet.",
            )
            return
        }

        val payload = command.toByteArray(Charsets.UTF_8)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            currentGatt.writeCharacteristic(
                characteristic,
                payload,
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT,
            )
        } else {
            @Suppress("DEPRECATION")
            characteristic.value = payload
            currentGatt.writeCharacteristic(characteristic)
        }
    }

    private fun findConfiguredWriteCharacteristic(
        services: List<BluetoothGattService>,
    ): BluetoothGattCharacteristic? {
        val serviceUuid = BluetoothConfig.COMMAND_SERVICE_UUID ?: return null
        val characteristicUuid = BluetoothConfig.COMMAND_WRITE_CHARACTERISTIC_UUID ?: return null
        return services
            .firstOrNull { it.uuid == serviceUuid }
            ?.characteristics
            ?.firstOrNull { it.uuid == characteristicUuid }
    }

    @SuppressLint("MissingPermission")
    private fun closeGatt(message: String) {
        mainHandler.removeCallbacksAndMessages(CONNECTION_TIMEOUT_TOKEN)
        writeCharacteristic = null
        gatt?.close()
        gatt = null
        _state.value = _state.value.copy(
            connectionStatus = ConnectionStatus.DISCONNECTED,
            isScanning = false,
            message = message,
        )
    }

    private companion object {
        val CONNECTION_TIMEOUT_TOKEN = Any()
    }
}

/**
 * Reactive state emitted by [RoverBluetoothManager] for the Connect Rover screen.
 */
data class BluetoothManagerState(
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val discoveredDevices: List<DiscoveredBluetoothDevice> = emptyList(),
    val selectedDeviceAddress: String? = null,
    val isScanning: Boolean = false,
    val message: String = "Ready to scan.",
)
