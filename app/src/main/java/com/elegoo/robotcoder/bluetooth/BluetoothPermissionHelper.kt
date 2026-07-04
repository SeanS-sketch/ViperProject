package com.elegoo.robotcoder.bluetooth

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Centralizes Android Bluetooth permission rules across API levels.
 *
 * Android 12 introduced dedicated BLE permissions, while older versions require location
 * permission for scanning. Keeping this logic outside Compose makes the UI easier to test and
 * keeps the Bluetooth milestone's platform rules in one place.
 */
object BluetoothPermissionHelper {
    /**
     * Returns the runtime permissions required before BLE scanning or connecting can begin.
     */
    fun requiredPermissions(): Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
        )
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * Returns true when every permission needed for BLE operations has been granted.
     */
    fun hasRequiredPermissions(context: Context): Boolean = requiredPermissions().all { permission ->
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}
