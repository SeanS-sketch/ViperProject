# Project Status - Rover Coder (ELEGOO Smart Robot Car 4.0)

## Current Milestone

**Milestone 2 - Bluetooth Foundation** (Code complete, hardware validation pending)

## Completed Features

### Milestone 1 - Application Foundation

- Kotlin + Jetpack Compose + Material Design 3 project scaffold
- MVVM architecture with dedicated ViewModels for all four screens
- Navigation Compose with persistent bottom navigation (Home, Workspace, Connect, Settings)
- Home screen with logo placeholder, title, and navigation actions
- Programming Workspace placeholder with toolbar and disabled Run/Stop buttons
- Settings screen with Dark Mode toggle, About, Credits, and Version
- Reusable UI components (PrimaryButton, SecondaryButton, AppCard, StatusIndicator, AppTopBar)
- Light and dark theme support via Material 3 color scheme
- `PROJECT_STATUS.md` tracking document

### Milestone 2 - Bluetooth Foundation

- Android BLE permissions added for Android 12+ (`BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`)
- Pre-Android 12 scan permission support added through `ACCESS_FINE_LOCATION`
- Dedicated `RoverBluetoothManager` created for BLE scanning, device discovery, connection management, reconnect, disconnect, sending UTF-8 messages, and future receive support
- Bluetooth configuration isolated in `BluetoothConfig` so future Arduino firmware UUIDs can be added without changing UI code
- BLE device model added with name, MAC address, and RSSI
- Connection states now include Disconnected, Scanning, Connecting, Connected, and Failed
- Connect Rover screen replaced with a functional BLE interface:
  - Runtime permission request
  - Scan and Stop Scan controls
  - Selectable discovered device list
  - Connect, Reconnect, and Disconnect controls
  - Status indicator
  - Loading indicator for scanning/connecting
  - User-facing status and error messages
- `ConnectRoverViewModel` now exposes BLE state through StateFlow while keeping Bluetooth logic outside Compose
- Debug build verified successfully with `assembleDebug`

## Pending Features

- Hardware validation on an Android device with an HC-08 BLE module
- Final BLE service UUID and characteristic UUID configuration after the Arduino firmware protocol is defined
- Receiving BLE messages from the rover once firmware support exists
- Drag-and-drop block coding workspace (future milestone)
- Program validation and execution engine (future milestone)
- Rover command transport integration with block execution (future milestone)
- Persistent settings and program storage (future milestone)
- Arduino firmware integration (separate track, not started)

## Known Issues

- Dark mode preference is session-only; it resets when the app process is killed (persistence deferred until storage is allowed).
- Launcher icons use vector placeholders rather than final branded artwork.
- Programming Workspace Run/Stop buttons are intentionally disabled placeholders until the execution milestone.
- BLE command sending is architected but cannot send rover commands until firmware UUIDs are configured.
- BLE scanning and connection behavior still need to be tested on physical Android hardware with the HC-08 module.

## Verification

- `assembleDebug` completed successfully.
- Runtime Bluetooth permission flow is implemented in the Connect Rover screen.
- BLE scanning, device selection, connection, reconnect, disconnect, timeout handling, and lost-connection handling are implemented in code.
- Physical scan/connection verification is pending because it requires an Android device and nearby HC-08 rover hardware.

## Future Milestones

### Milestone 3 - Block Coding Workspace

- Custom drag-and-drop block editor in the Programming Workspace
- Block palette, workspace canvas, and program model in `model/`
- Keep block coding independent from Bluetooth transport

### Milestone 4 - Program Execution

- Serialize block programs to rover command strings
- Run/Stop controls wired to execution pipeline
- Send commands over the Bluetooth layer

### Milestone 5 - Polish and Persistence

- Save/load programs locally
- Persist settings (theme, last connected device)
- Error handling, empty states, and UX refinements

### Milestone 6 - Arduino Firmware (Separate)

- Firmware to receive and execute commands on the ELEGOO Smart Robot Car 4.0
