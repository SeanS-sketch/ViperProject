# Project Status — Rover Coder (ELEGOO Smart Robot Car 4.0)

## Current Milestone

**Milestone 1 — Application Foundation** (Complete)

## Completed Features

- Kotlin + Jetpack Compose + Material Design 3 project scaffold
- MVVM architecture with dedicated ViewModels for all four screens
- Navigation Compose with persistent bottom navigation (Home, Workspace, Connect, Settings)
- Home screen with logo placeholder, title, and navigation actions
- Programming Workspace placeholder with toolbar and disabled Run/Stop buttons
- Connect Rover placeholder with Bluetooth icon, status card, and disabled controls
- Settings screen with Dark Mode toggle, About, Credits, and Version
- Reusable UI components (PrimaryButton, SecondaryButton, AppCard, StatusIndicator, AppTopBar)
- Light and dark theme support via Material 3 color scheme
- `PROJECT_STATUS.md` tracking document

## Pending Features

- Bluetooth scanning, pairing, and connection management (Milestone 2)
- Drag-and-drop block coding workspace (future milestone)
- Program validation and execution engine (future milestone)
- Rover command transport over Bluetooth (future milestone)
- Persistent settings and program storage (future milestone)
- Arduino firmware integration (separate track, not started)

## Known Issues

- Dark mode preference is session-only; it resets when the app process is killed (persistence deferred until storage is allowed).
- Launcher icons use vector placeholders rather than final branded artwork.
- Run/Stop and Bluetooth action buttons are intentionally disabled placeholders.

## Future Milestones

### Milestone 2 — Bluetooth Connectivity
- Runtime permissions
- Device scan and pairing flow for one ELEGOO Smart Robot Car 4.0
- Connection state exposed through `ConnectRoverViewModel` and shared app state

### Milestone 3 — Block Coding Workspace
- Custom drag-and-drop block editor in the Programming Workspace
- Block palette, workspace canvas, and program model in `model/`

### Milestone 4 — Program Execution
- Serialize block programs to rover commands
- Run/Stop controls wired to execution pipeline
- Send commands over the Bluetooth layer

### Milestone 5 — Polish and Persistence
- Save/load programs locally
- Persist settings (theme, last connected device)
- Error handling, empty states, and UX refinements

### Milestone 6 — Arduino Firmware (Separate)
- Firmware to receive and execute commands on the ELEGOO Smart Robot Car 4.0
