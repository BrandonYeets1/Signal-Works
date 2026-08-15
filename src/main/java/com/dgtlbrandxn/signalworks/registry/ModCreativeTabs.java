package com.dgtlbrandxn.signalworks.registry;

import com.dgtlbrandxn.signalworks.TrafficControl;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TrafficControl.MOD_ID);

    /**
     * The legacy registry key remains {@code trafficcontrol:main}; only its visible Signal Works
     * suite identity changes. Keeping the key stable avoids unnecessary compatibility churn.
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register(
            "main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.trafficcontrol.signals"))
                    .icon(() -> ModItems.TRAFFIC_LIGHT.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        // Supports first: the pieces builders reach for most often.
                        output.accept(ModItems.SIGNAL_POLE_MEDIUM.get());
                        output.accept(ModItems.SIGNAL_POLE_LARGE.get());
                        output.accept(ModItems.MAST_ARM_MEDIUM.get());
                        output.accept(ModItems.MAST_ARM_LARGE.get());
                        output.accept(ModItems.SIGNAL_ARM.get());
                        output.accept(ModItems.STAND.get());

                        // Vehicle and pedestrian signals.
                        output.accept(ModItems.TRAFFIC_LIGHT.get());
                        output.accept(ModItems.TRAFFIC_LIGHT_TURN_LEFT.get());
                        output.accept(ModItems.TRAFFIC_LIGHT_TURN_RIGHT.get());
                        output.accept(ModItems.TRAFFIC_LIGHT_STRAIGHT_ARROW.get());
                        output.accept(ModItems.TRAFFIC_LIGHT_U_TURN.get());
                        output.accept(ModItems.TRAFFIC_LIGHT_BUS.get());
                        output.accept(ModItems.TRAFFIC_LIGHT_DOGHOUSE.get());
                        output.accept(ModItems.TRAFFIC_LIGHT_5.get());
                        output.accept(ModItems.TRAFFIC_LIGHT_5_RIGHT.get());
                        output.accept(ModItems.TRAFFIC_LIGHT_2.get());
                        output.accept(ModItems.RAMP_METER_SIGNAL.get());
                        output.accept(ModItems.TRAFFIC_LIGHT_1.get());
                        output.accept(ModItems.TRAFFIC_LIGHT_4.get());
                        // traffic_light_6 remains registered only for old-world compatibility.
                        output.accept(ModItems.FLASHER_AMBER.get());
                        output.accept(ModItems.FLASHER_RED.get());

                        // Controls, detection and enforcement.
                        output.accept(ModItems.TRAFFIC_LIGHT_CONTROL_BOX.get());
                        output.accept(ModItems.ENGINEER_WAND.get());
                        output.accept(ModItems.ADJUSTER.get());
                        output.accept(ModItems.SIGNAL_CUSTOMIZER.get());
                        output.accept(ModItems.TRAFFIC_CAMERA.get());
                        output.accept(ModItems.RED_LIGHT_CAMERA.get());
                        output.accept(ModItems.LANE_CONTROL_NO_LEFT.get());
                        output.accept(ModItems.LANE_CONTROL_NO_RIGHT.get());
                        output.accept(ModItems.LANE_CONTROL_NO_AHEAD.get());
                        output.accept(ModItems.TRAFFIC_SENSOR_LEFT.get());
                        output.accept(ModItems.TRAFFIC_SENSOR_STRAIGHT.get());
                        output.accept(ModItems.TRAFFIC_SENSOR_BUS.get());
                        output.accept(ModItems.TRAFFIC_SENSOR_RIGHT.get());
                        output.accept(ModItems.PEDESTRIAN_BUTTON.get());

                        // Municipal signs stay with the signal system; work-zone hardware lives in Construction.
                    })
                    .build()
    );


    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SIGNAGE = TABS.register(
            "signage",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.trafficcontrol.signage"))
                    .icon(() -> ModItems.PROP_SIGN_SIGNALAHEAD.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.PROP_SIGN_SIGNALAHEAD.get());
                        output.accept(ModItems.SIGN.get());
                        output.accept(ModItems.STREET_SIGN.get());
                        output.accept(ModItems.SIGNAL_POLE_SMALL.get());
                        output.accept(ModItems.MAST_ARM_SMALL.get());
                        output.accept(ModItems.FREEWAY_GUIDE_SIGN.get());
                        output.accept(ModItems.FREEWAY_SIGN_POLE.get());
                        output.accept(ModItems.FREEWAY_SIGN_MAST.get());

                        // 3-block lane pavement markings.
                        output.accept(ModItems.ROAD_DECAL_LEFT_ONLY.get());
                        output.accept(ModItems.ROAD_DECAL_RIGHT_ONLY.get());
                        output.accept(ModItems.ROAD_DECAL_STRAIGHT_ONLY.get());
                        output.accept(ModItems.ROAD_DECAL_LEFT_STRAIGHT.get());
                        output.accept(ModItems.ROAD_DECAL_RIGHT_STRAIGHT.get());
                        output.accept(ModItems.ROAD_DECAL_LEFT_RIGHT.get());
                        output.accept(ModItems.ROAD_DECAL_MERGE_LEFT.get());
                        output.accept(ModItems.ROAD_DECAL_MERGE_RIGHT.get());
                    })
                    .build()
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> UTILITIES = TABS.register(
            "utilities",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.trafficcontrol.utilities"))
                    .icon(() -> ModItems.POLE_TRANSFORMER.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.UTILITY_POLE_WOOD.get());
                        output.accept(ModItems.UTILITY_POLE_METAL.get());
                        output.accept(ModItems.UTILITY_WIRE.get());
                        output.accept(ModItems.UTILITY_GROUND_WIRE.get());
                        output.accept(ModItems.UTILITY_GUY_WIRE.get());
                        output.accept(ModItems.POLE_TRANSFORMER.get());
                    })
                    .build()
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> STREET_LIGHTS = TABS.register(
            "streetlights",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.trafficcontrol.lights"))
                    .icon(() -> ModItems.STREET_LIGHT_LED_GCL.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.STREETLIGHT_ARM_STRAIGHT.get());
                        output.accept(ModItems.STREETLIGHT_ARM_UPSWEEP.get());
                        output.accept(ModItems.STREETLIGHT_ARM_CURVED.get());
                        output.accept(ModItems.STREET_LIGHT_HPS_M400A2.get());
                        output.accept(ModItems.STREET_LIGHT_HPS_M400A2_CUTOFF.get());
                        output.accept(ModItems.STREET_LIGHT_LED_GCL.get());
                        ModItems.STREET_LIGHT_MODELS.values().forEach(item -> output.accept(item.get()));
                    })
                    .build()
    );


    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CONSTRUCTION = TABS.register(
            "construction",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.trafficcontrol.construction"))
                    .icon(() -> ModItems.CHANNELIZER_ORANGE.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        // Active work-zone equipment.
                        output.accept(ModItems.CONSTRUCTION_FLOODLIGHT.get());
                        output.accept(ModItems.CONSTRUCTION_MESSAGE_BOARD.get());

                        // Channelization and temporary traffic control.
                        output.accept(ModItems.CONE.get());
                        output.accept(ModItems.CHANNELIZER_ORANGE.get());
                        output.accept(ModItems.CHANNELIZER_GREY.get());
                        output.accept(ModItems.DRUM.get());
                        output.accept(ModItems.ROAD_FLARE.get());
                        output.accept(ModItems.TYPE_3_BARRIER.get());
                        output.accept(ModItems.TYPE_3_BARRIER_RIGHT.get());
                        output.accept(ModItems.CONCRETE_BARRIER.get());
                    })
                    .build()
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ROADSIDE = TABS.register(
            "roadside",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.trafficcontrol.roadside"))
                    .icon(() -> ModItems.GUARDRAIL_LED.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.GUARDRAIL_END.get());
                        output.accept(ModItems.TRAFFIC_RAIL.get());
                        output.accept(ModItems.GUARDRAIL_LED.get());
                    })
                    .build()
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> RAILROAD = TABS.register(
            "railroad",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.trafficcontrol.railroad"))
                    .icon(() -> ModItems.CROSSING_GATE_CROSSBUCK.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.CROSSING_GATE_BASE.get());
                        output.accept(ModItems.CROSSING_GATE_POLE.get());
                        output.accept(ModItems.CROSSING_GATE_GATE.get());
                        output.accept(ModItems.CROSSING_GATE_LAMPS.get());
                        output.accept(ModItems.CROSSING_GATE_CROSSBUCK.get());
                        output.accept(ModItems.SAFETRAN_TYPE_3.get());
                        output.accept(ModItems.SAFETRAN_MECHANICAL.get());
                        output.accept(ModItems.WCH_BELL.get());
                        output.accept(ModItems.WCH_MECHANICAL_BELL.get());
                        output.accept(ModItems.WIG_WAG.get());
                        output.accept(ModItems.VERTICAL_WIG_WAG.get());
                        output.accept(ModItems.OVERHEAD_POLE.get());
                        output.accept(ModItems.OVERHEAD.get());
                        output.accept(ModItems.OVERHEAD_LAMPS.get());
                        output.accept(ModItems.OVERHEAD_CROSSBUCK.get());
                        output.accept(ModItems.SHUNT_BORDER.get());
                        output.accept(ModItems.SHUNT_ISLAND.get());
                        output.accept(ModItems.CROSSING_RELAY_SE.get());
                        output.accept(ModItems.CROSSING_RELAY_SW.get());
                        output.accept(ModItems.CROSSING_RELAY_NW.get());
                        output.accept(ModItems.CROSSING_RELAY_NE.get());
                        output.accept(ModItems.CROSSING_RELAY_TOP_SW.get());
                        output.accept(ModItems.CROSSING_RELAY_TOP_SE.get());
                        output.accept(ModItems.CROSSING_RELAY_TOP_NW.get());
                        output.accept(ModItems.CROSSING_RELAY_TOP_NE.get());
                    })
                    .build()
    );

    private ModCreativeTabs() {
    }
}
