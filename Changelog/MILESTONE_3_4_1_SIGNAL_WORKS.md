# Signal Works Core — Brand Transition 3.4.1

Version: `2.0.0-alpha.3.4.1`

This release establishes the Signal Works identity before Milestone 3.5 begins.

## New in this release

- The visible mod name is now **Signal Works Core**.
- The primary creative catalog is now **Signal Works Signals**.
- The streetlight catalog is now **Signal Works Lights**.
- Railroad equipment has its own **Signal Works RR** creative catalog.
- The Gradle project name is now `SignalWorks-Core-NeoForge-1.21.1`.
- Built JARs now use the `signalworks-core` artifact name.
- Mod-list, resource-pack, sign-repository, key-category, and documentation branding are updated.
- Stale setup/status documents were replaced with current NeoForge 1.21.1 information.

## Compatibility

The rebrand does **not** rename the `trafficcontrol` mod ID, namespace, Java package, or any existing
registry ID. Existing worlds should resolve the same blocks, items, block entities, menus, sounds, and
saved data.

Only the JAR filename changes. Remove the older Traffic Control JAR before installing the new Signal
Works Core JAR because both represent the same `trafficcontrol` mod ID.

## Asset policy

No PNG textures, model geometry, sounds, or bulk asset catalogs are included or replaced in this patch.
The existing generic traffic-cone mod icon is retained because it contains no obsolete text branding.

## Acceptance checks

1. The Mods screen shows **Signal Works Core**.
2. Creative inventory shows **Signal Works Signals**, **Signal Works Lights**, and **Signal Works RR**.
3. Previously placed blocks load without missing-registry warnings.
4. Existing controller, signal, sign, and light data remains intact after saving and reopening the world.
5. Only one JAR providing mod ID `trafficcontrol` is installed.
