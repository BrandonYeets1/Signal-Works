# Traffic Control NeoForge 1.21.1 — Milestone 3.0

## Command-terminal controller

The Traffic Signal Controller now opens a larger terminal-inspired interface rather than the compact overlapping settings panel.

The screen contains:

- a live local system log
- a clear intersection-profile panel
- the original clickable settings controls
- a command input line
- multiplayer-safe updates through the existing server menu buttons

Supported commands:

```text
help
status
set through <5-60>
set priority <left|right|both>
set night <on|off>
set speed <slow|normal|fast>
set failsafe <on|off>
```

Through-green values entered in the terminal are rounded to the nearest five seconds and clamped to the controller's supported 5–60 second range.

## Dynamic signal arms — foundation

Side-mounted signals can now find a vertical traffic pole up to four blocks behind the signal. The renderer creates a US-style arm whose length exactly matches the selected distance.

Each block of distance acts as a deterministic mounting slot:

```text
Pole |==== slot 1 ==== slot 2 ==== slot 3 ==== [Signal]
```

The arm includes:

- a rear plate against the signal housing
- a narrow horizontal support tube
- a width-aware clamp around small, medium, or large poles
- inherited pole color
- support for all signal rotations, including diagonal heads

The path between the signal and pole must be air. This prevents a generated arm from passing through buildings or unrelated blocks.

Overhead mast mounting and same-height center/front mast mounting remain separate. A signal still uses only one attachment style at a time.

## Placement/testing

1. Apply this patch over Milestone 2.9.4.
2. Build with Java 21.
3. Open a Traffic Light Control Box and test both buttons and typed commands.
4. For a dynamic side arm, place a vertical traffic pole one to four blocks behind the signal and leave the blocks between them empty.
5. The signal should face away from the pole. AUTO and SIDE mount modes can use the dynamic arm; TOP remains overhead-only.

## Packaging

This patch contains no textures, inventory icons, or historical milestone documents.
