# Milestone 3.2.2 — Combined Signal Red + Streetlight Output Hotfix

## Combined signal heads

Doghouse, four-section and five-section heads now treat the circular signal and the turn arrow as separate movements inside one housing.

During a protected turn phase:

- Green arrow: circular red remains illuminated.
- Yellow arrow: circular red remains illuminated.
- Four-section yellow clearance: circular yellow is shown by itself because that model has no yellow-arrow lens.
- Dedicated arrow-only heads continue to use their own red arrow normally.

## Streetlights

Imported JSON streetlight fixtures now emit light directly from their own block whenever `lit=true`, using their catalog output level:

- LED: 15
- Standard/classic: 12
- Dim: 9

The invisible road-light helpers remain in place for wider coverage.

Modular HPS and LED fixture heads now also create three level-15 helper sources below and ahead of the optic. This fixes tall installations where light from the fixture block faded before reaching the road.

All modular fixtures now use night-only automatic operation and remove their helper lights during daytime, redstone shutdown, or block removal.
