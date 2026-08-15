Signal Works Core 3.4.13a - Applicator Fix

This replaces only the broken 3.4.13 apply scripts.

The old script failed when the patch was extracted directly into the project folder because it tried to copy src onto itself.
The corrected script detects that layout, skips same-path copies, and safely continues the cleanup.

USE:
1. Put these two Apply-SignalWorks-3.4.13 files in the project root beside build.gradle.
2. Run Apply-SignalWorks-3.4.13.bat.
3. Then run: gradlew.bat clean build

The script is safe to rerun after the partial failed attempt.
