# Signal Works Core 3.4.4 — Sign Designer Hotfix

## New fixes

- Rebuilt the municipal sign designer as a compact 460 × 258 GUI.
- Fits a 1920 × 1080 window at Minecraft GUI scale 4 without clipping.
- Added an explicit server-data-ready slot so text fields wait for the complete sign payload before becoming editable.
- Existing sign text now populates the editor instead of initially appearing blank.
- Apply and Enter can no longer accidentally replace a sign with empty text while its menu data is still loading.
- Moved world-rendered lettering farther away from each blade face and changed it to the regular depth-tested font pass.
- E, T, and / remain safe for text entry while the editor is open.

## Assets

No textures, models, sounds, or other binary assets were added or replaced.
