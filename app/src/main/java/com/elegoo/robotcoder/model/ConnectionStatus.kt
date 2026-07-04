package com.elegoo.robotcoder.model

/**
 * Represents the high-level connection state between the phone and the rover.
 *
 * Milestone 1 only defines the model shape. Milestone 2 will populate these
 * values from real Bluetooth events.
 */
enum class ConnectionStatus {
    DISCONNECTED,
    SCANNING,
    CONNECTING,
    CONNECTED,
}

/**
 * User-facing label for each [ConnectionStatus] value.
 */
fun ConnectionStatus.displayLabel(): String = when (this) {
    ConnectionStatus.DISCONNECTED -> "Disconnected"
    ConnectionStatus.SCANNING -> "Scanning"
    ConnectionStatus.CONNECTING -> "Connecting"
    ConnectionStatus.CONNECTED -> "Connected"
}
