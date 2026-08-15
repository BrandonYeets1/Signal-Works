Signal Works a3.5.6 editable support model map

Traffic signal rear brackets:
  src/main/resources/assets/trafficcontrol/models/block/signal_arms/
    mast/{three,four,five}_{single,multi}.json
    pole_small/{three,four,five}_{single,multi}.json
    pole_large/{three,four,five}_{single,multi}.json
    hanging/{three,four,five}_{single,multi}.json

Traffic-signal mast arms:
  src/main/resources/assets/trafficcontrol/models/block/signal_supports/mast_arms/

Dedicated streetlight arms:
  src/main/resources/assets/trafficcontrol/models/block/streetlight_arms/

These files use the Signal Works 1.9.0-compatible Java model format and can be
opened/imported in Blockbench for further geometry/UV work. Preserve tintindex 0
on metal faces if you want dye colors to continue working.
