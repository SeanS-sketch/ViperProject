package com.elegoo.robotcoder.bluetooth

import java.util.UUID

/**
 * BLE configuration values that will be finalized when the rover firmware protocol exists.
 *
 * The HC-08 module can expose different service and characteristic UUIDs depending on firmware
 * setup. Keeping these values nullable prevents the app from pretending an unknown UUID is real
 * while still giving future firmware work one clear place to configure command transport.
 */
object BluetoothConfig {
    val COMMAND_SERVICE_UUID: UUID? = null
    val COMMAND_WRITE_CHARACTERISTIC_UUID: UUID? = null
    val COMMAND_NOTIFY_CHARACTERISTIC_UUID: UUID? = null

    const val SCAN_TIMEOUT_MILLIS = 10_000L
    const val CONNECTION_TIMEOUT_MILLIS = 12_000L
}
