package com.elegoo.robotcoder.model

/**
 * User-visible information for a BLE device discovered during scanning.
 *
 * The Android BluetoothDevice object stays inside the Bluetooth layer so the UI can render a
 * simple immutable model without holding platform objects.
 */
data class DiscoveredBluetoothDevice(
    val name: String,
    val address: String,
    val rssi: Int,
)
