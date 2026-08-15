package com.dgtlbrandxn.signalworks.registry;

import com.dgtlbrandxn.signalworks.TrafficControl;
import com.dgtlbrandxn.signalworks.item.AdjusterItem;
import com.dgtlbrandxn.signalworks.item.DescribedBlockItem;
import com.dgtlbrandxn.signalworks.item.EngineerWandItem;
import com.dgtlbrandxn.signalworks.item.SignalCustomizerItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, TrafficControl.MOD_ID);

    public static final DeferredHolder<Item, BlockItem> CROSSING_GATE_BASE = ITEMS.register("crossing_gate_base", () -> new BlockItem(ModBlocks.CROSSING_GATE_BASE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> STAND = ITEMS.register("stand", () -> new DescribedBlockItem(ModBlocks.STAND.get(), new Item.Properties(), "item.trafficcontrol.stand.description"));
    public static final DeferredHolder<Item, BlockItem> CROSSING_GATE_GATE = ITEMS.register("crossing_gate_gate", () -> new BlockItem(ModBlocks.CROSSING_GATE_GATE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CROSSING_GATE_LAMPS = ITEMS.register("crossing_gate_lamps", () -> new BlockItem(ModBlocks.CROSSING_GATE_LAMPS.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CROSSING_GATE_POLE = ITEMS.register("crossing_gate_pole", () -> new BlockItem(ModBlocks.CROSSING_GATE_POLE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CROSSING_GATE_CROSSBUCK = ITEMS.register("crossing_gate_crossbuck", () -> new BlockItem(ModBlocks.CROSSING_GATE_CROSSBUCK.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> SAFETRAN_TYPE_3 = ITEMS.register("safetran_type_3", () -> new BlockItem(ModBlocks.SAFETRAN_TYPE_3.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CROSSING_RELAY_SE = ITEMS.register("crossing_relay_se", () -> new BlockItem(ModBlocks.CROSSING_RELAY_SE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CROSSING_RELAY_SW = ITEMS.register("crossing_relay_sw", () -> new BlockItem(ModBlocks.CROSSING_RELAY_SW.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CROSSING_RELAY_NW = ITEMS.register("crossing_relay_nw", () -> new BlockItem(ModBlocks.CROSSING_RELAY_NW.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CROSSING_RELAY_NE = ITEMS.register("crossing_relay_ne", () -> new BlockItem(ModBlocks.CROSSING_RELAY_NE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CROSSING_RELAY_TOP_SW = ITEMS.register("crossing_relay_top_sw", () -> new BlockItem(ModBlocks.CROSSING_RELAY_TOP_SW.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CROSSING_RELAY_TOP_SE = ITEMS.register("crossing_relay_top_se", () -> new BlockItem(ModBlocks.CROSSING_RELAY_TOP_SE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CROSSING_RELAY_TOP_NW = ITEMS.register("crossing_relay_top_nw", () -> new BlockItem(ModBlocks.CROSSING_RELAY_TOP_NW.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CROSSING_RELAY_TOP_NE = ITEMS.register("crossing_relay_top_ne", () -> new BlockItem(ModBlocks.CROSSING_RELAY_TOP_NE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> OVERHEAD_POLE = ITEMS.register("overhead_pole", () -> new BlockItem(ModBlocks.OVERHEAD_POLE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> OVERHEAD = ITEMS.register("overhead", () -> new BlockItem(ModBlocks.OVERHEAD.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> OVERHEAD_LAMPS = ITEMS.register("overhead_lamps", () -> new BlockItem(ModBlocks.OVERHEAD_LAMPS.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> OVERHEAD_CROSSBUCK = ITEMS.register("overhead_crossbuck", () -> new BlockItem(ModBlocks.OVERHEAD_CROSSBUCK.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> SAFETRAN_MECHANICAL = ITEMS.register("safetran_mechanical", () -> new BlockItem(ModBlocks.SAFETRAN_MECHANICAL.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> PROP_SIGN_SIGNALAHEAD = ITEMS.register(
            "prop_sign_signalahead",
            () -> new BlockItem(ModBlocks.PROP_SIGN_SIGNALAHEAD.get(), new Item.Properties())
    );
    public static final DeferredHolder<Item, BlockItem> FREEWAY_GUIDE_SIGN = ITEMS.register(
            "freeway_guide_sign", () -> new BlockItem(ModBlocks.FREEWAY_GUIDE_SIGN.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> SIGN = ITEMS.register(
            "sign", () -> new DescribedBlockItem(
                    ModBlocks.SIGN.get(), new Item.Properties(), "item.trafficcontrol.sign.description"));
    public static final DeferredHolder<Item, BlockItem> CONE = ITEMS.register("cone", () -> new BlockItem(ModBlocks.CONE.get(), new Item.Properties()));

    /** Hidden compatibility item for inventories containing the original channelizer. */
    @Deprecated(forRemoval = false)
    public static final DeferredHolder<Item, BlockItem> CHANNELIZER = ITEMS.register("channelizer", () -> new BlockItem(ModBlocks.CHANNELIZER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CHANNELIZER_ORANGE = ITEMS.register("channelizer_orange", () -> new BlockItem(ModBlocks.CHANNELIZER_ORANGE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CHANNELIZER_GREY = ITEMS.register("channelizer_grey", () -> new BlockItem(ModBlocks.CHANNELIZER_GREY.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> DRUM = ITEMS.register("drum", () -> new BlockItem(ModBlocks.DRUM.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> ROAD_FLARE = ITEMS.register(
            "road_flare", () -> new DescribedBlockItem(
                    ModBlocks.ROAD_FLARE.get(), new Item.Properties(),
                    "item.trafficcontrol.road_flare.description"));
    public static final DeferredHolder<Item, BlockItem> ROAD_DECAL_LEFT_ONLY = ITEMS.register("road_decal_left_only", () -> new BlockItem(ModBlocks.ROAD_DECAL_LEFT_ONLY.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> ROAD_DECAL_RIGHT_ONLY = ITEMS.register("road_decal_right_only", () -> new BlockItem(ModBlocks.ROAD_DECAL_RIGHT_ONLY.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> ROAD_DECAL_STRAIGHT_ONLY = ITEMS.register("road_decal_straight_only", () -> new BlockItem(ModBlocks.ROAD_DECAL_STRAIGHT_ONLY.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> ROAD_DECAL_LEFT_STRAIGHT = ITEMS.register("road_decal_left_straight", () -> new BlockItem(ModBlocks.ROAD_DECAL_LEFT_STRAIGHT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> ROAD_DECAL_RIGHT_STRAIGHT = ITEMS.register("road_decal_right_straight", () -> new BlockItem(ModBlocks.ROAD_DECAL_RIGHT_STRAIGHT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> ROAD_DECAL_LEFT_RIGHT = ITEMS.register("road_decal_left_right", () -> new BlockItem(ModBlocks.ROAD_DECAL_LEFT_RIGHT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> ROAD_DECAL_MERGE_LEFT = ITEMS.register("road_decal_merge_left", () -> new BlockItem(ModBlocks.ROAD_DECAL_MERGE_LEFT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> ROAD_DECAL_MERGE_RIGHT = ITEMS.register("road_decal_merge_right", () -> new BlockItem(ModBlocks.ROAD_DECAL_MERGE_RIGHT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> UTILITY_POLE_WOOD = ITEMS.register("utility_pole_wood", () -> new BlockItem(ModBlocks.UTILITY_POLE_WOOD.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> UTILITY_POLE_METAL = ITEMS.register("utility_pole_metal", () -> new BlockItem(ModBlocks.UTILITY_POLE_METAL.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> UTILITY_WIRE = ITEMS.register("utility_wire", () -> new BlockItem(ModBlocks.UTILITY_WIRE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> UTILITY_GROUND_WIRE = ITEMS.register("utility_ground_wire", () -> new BlockItem(ModBlocks.UTILITY_GROUND_WIRE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> UTILITY_GUY_WIRE = ITEMS.register("utility_guy_wire", () -> new BlockItem(ModBlocks.UTILITY_GUY_WIRE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> POLE_TRANSFORMER = ITEMS.register("pole_transformer", () -> new BlockItem(ModBlocks.POLE_TRANSFORMER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CONSTRUCTION_FLOODLIGHT = ITEMS.register(
            "construction_floodlight", () -> new DescribedBlockItem(
                    ModBlocks.CONSTRUCTION_FLOODLIGHT.get(), new Item.Properties(),
                    "item.trafficcontrol.construction_floodlight.description"));
    public static final DeferredHolder<Item, BlockItem> CONSTRUCTION_MESSAGE_BOARD = ITEMS.register(
            "construction_message_board", () -> new DescribedBlockItem(
                    ModBlocks.CONSTRUCTION_MESSAGE_BOARD.get(), new Item.Properties(),
                    "item.trafficcontrol.construction_message_board.description"));
    public static final DeferredHolder<Item, BlockItem> STREET_LIGHT_SINGLE = ITEMS.register("street_light_single", () -> new BlockItem(ModBlocks.STREET_LIGHT_SINGLE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> LIGHT_SOURCE = ITEMS.register("light_source", () -> new BlockItem(ModBlocks.LIGHT_SOURCE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> STREET_LIGHT_DOUBLE = ITEMS.register("street_light_double", () -> new BlockItem(ModBlocks.STREET_LIGHT_DOUBLE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> STREETLIGHT_ARM_STRAIGHT = ITEMS.register("streetlight_arm_straight", () -> new BlockItem(ModBlocks.STREETLIGHT_ARM_STRAIGHT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> STREETLIGHT_ARM_UPSWEEP = ITEMS.register("streetlight_arm_upsweep", () -> new BlockItem(ModBlocks.STREETLIGHT_ARM_UPSWEEP.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> STREETLIGHT_ARM_CURVED = ITEMS.register("streetlight_arm_curved", () -> new BlockItem(ModBlocks.STREETLIGHT_ARM_CURVED.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> STREET_LIGHT_HPS_M400A2 = ITEMS.register("street_light_hps_m400a2", () -> new BlockItem(ModBlocks.STREET_LIGHT_HPS_M400A2.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> STREET_LIGHT_HPS_M400A2_CUTOFF = ITEMS.register("street_light_hps_m400a2_cutoff", () -> new BlockItem(ModBlocks.STREET_LIGHT_HPS_M400A2_CUTOFF.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> STREET_LIGHT_LED_GCL = ITEMS.register("street_light_led_gcl", () -> new BlockItem(ModBlocks.STREET_LIGHT_LED_GCL.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_LIGHT = ITEMS.register("traffic_light", () -> new BlockItem(ModBlocks.TRAFFIC_LIGHT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_LIGHT_CONTROL_BOX = ITEMS.register("traffic_light_control_box", () -> new BlockItem(ModBlocks.TRAFFIC_LIGHT_CONTROL_BOX.get(), new Item.Properties()));
    public static final DeferredHolder<Item, EngineerWandItem> ENGINEER_WAND = ITEMS.register("engineer_wand", () -> new EngineerWandItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, AdjusterItem> ADJUSTER = ITEMS.register("adjuster", () -> new AdjusterItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, SignalCustomizerItem> SIGNAL_CUSTOMIZER = ITEMS.register("signal_customizer", () -> new SignalCustomizerItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_LIGHT_TURN_LEFT = ITEMS.register("traffic_light_turn_left", () -> new BlockItem(ModBlocks.TRAFFIC_LIGHT_TURN_LEFT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_LIGHT_TURN_RIGHT = ITEMS.register("traffic_light_turn_right", () -> new BlockItem(ModBlocks.TRAFFIC_LIGHT_TURN_RIGHT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_LIGHT_STRAIGHT_ARROW = ITEMS.register("traffic_light_straight_arrow", () -> new BlockItem(ModBlocks.TRAFFIC_LIGHT_STRAIGHT_ARROW.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_LIGHT_U_TURN = ITEMS.register("traffic_light_u_turn", () -> new BlockItem(ModBlocks.TRAFFIC_LIGHT_U_TURN.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_LIGHT_BUS = ITEMS.register("traffic_light_bus", () -> new BlockItem(ModBlocks.TRAFFIC_LIGHT_BUS.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_CAMERA = ITEMS.register("traffic_camera", () -> new BlockItem(ModBlocks.TRAFFIC_CAMERA.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> RED_LIGHT_CAMERA = ITEMS.register("red_light_camera", () -> new BlockItem(ModBlocks.RED_LIGHT_CAMERA.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> LANE_CONTROL_NO_LEFT = ITEMS.register("lane_control_no_left", () -> new BlockItem(ModBlocks.LANE_CONTROL_NO_LEFT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> LANE_CONTROL_NO_RIGHT = ITEMS.register("lane_control_no_right", () -> new BlockItem(ModBlocks.LANE_CONTROL_NO_RIGHT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> LANE_CONTROL_NO_AHEAD = ITEMS.register("lane_control_no_ahead", () -> new BlockItem(ModBlocks.LANE_CONTROL_NO_AHEAD.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> WIG_WAG = ITEMS.register("wig_wag", () -> new BlockItem(ModBlocks.WIG_WAG.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> VERTICAL_WIG_WAG = ITEMS.register("vertical_wig_wag", () -> new BlockItem(ModBlocks.VERTICAL_WIG_WAG.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> SHUNT_BORDER = ITEMS.register("shunt_border", () -> new BlockItem(ModBlocks.SHUNT_BORDER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> SHUNT_ISLAND = ITEMS.register("shunt_island", () -> new BlockItem(ModBlocks.SHUNT_ISLAND.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TYPE_3_BARRIER = ITEMS.register("type_3_barrier", () -> new BlockItem(ModBlocks.TYPE_3_BARRIER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TYPE_3_BARRIER_RIGHT = ITEMS.register("type_3_barrier_right", () -> new BlockItem(ModBlocks.TYPE_3_BARRIER_RIGHT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_RAIL = ITEMS.register("traffic_rail", () -> new BlockItem(ModBlocks.TRAFFIC_RAIL.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> GUARDRAIL_END = ITEMS.register("guardrail_end", () -> new BlockItem(ModBlocks.GUARDRAIL_END.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> GUARDRAIL_LED = ITEMS.register("guardrail_led", () -> new BlockItem(ModBlocks.GUARDRAIL_LED.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> CONCRETE_BARRIER = ITEMS.register("concrete_barrier", () -> new BlockItem(ModBlocks.CONCRETE_BARRIER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> SIGNAL_POLE_SMALL = ITEMS.register("signal_pole_small", () -> new BlockItem(ModBlocks.SIGNAL_POLE_SMALL.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> SIGNAL_POLE_MEDIUM = ITEMS.register("signal_pole_medium", () -> new BlockItem(ModBlocks.SIGNAL_POLE_MEDIUM.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> SIGNAL_POLE_LARGE = ITEMS.register("signal_pole_large", () -> new BlockItem(ModBlocks.SIGNAL_POLE_LARGE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> MAST_ARM_SMALL = ITEMS.register("mast_arm_small", () -> new BlockItem(ModBlocks.MAST_ARM_SMALL.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> MAST_ARM_MEDIUM = ITEMS.register("mast_arm_medium", () -> new BlockItem(ModBlocks.MAST_ARM_MEDIUM.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> MAST_ARM_LARGE = ITEMS.register("mast_arm_large", () -> new BlockItem(ModBlocks.MAST_ARM_LARGE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> FREEWAY_SIGN_POLE = ITEMS.register(
            "freeway_sign_pole", () -> new BlockItem(ModBlocks.FREEWAY_SIGN_POLE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> FREEWAY_SIGN_MAST = ITEMS.register(
            "freeway_sign_mast", () -> new BlockItem(ModBlocks.FREEWAY_SIGN_MAST.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> SIGNAL_ARM = ITEMS.register("signal_arm", () -> new DescribedBlockItem(
            ModBlocks.SIGNAL_ARM.get(), new Item.Properties(), "item.trafficcontrol.signal_arm.description"));
    public static final DeferredHolder<Item, BlockItem> WCH_BELL = ITEMS.register("wch_bell", () -> new BlockItem(ModBlocks.WCH_BELL.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> WCH_MECHANICAL_BELL = ITEMS.register("wch_mechanical_bell", () -> new BlockItem(ModBlocks.WCH_MECHANICAL_BELL.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_SENSOR_LEFT = ITEMS.register("traffic_sensor_left", () -> new DescribedBlockItem(ModBlocks.TRAFFIC_SENSOR_LEFT.get(), new Item.Properties(), "item.trafficcontrol.traffic_sensor_left.description"));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_SENSOR_STRAIGHT = ITEMS.register("traffic_sensor_straight", () -> new DescribedBlockItem(ModBlocks.TRAFFIC_SENSOR_STRAIGHT.get(), new Item.Properties(), "item.trafficcontrol.traffic_sensor_straight.description"));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_SENSOR_BUS = ITEMS.register("traffic_sensor_bus", () -> new DescribedBlockItem(ModBlocks.TRAFFIC_SENSOR_BUS.get(), new Item.Properties(), "item.trafficcontrol.traffic_sensor_bus.description"));
    public static final DeferredHolder<Item, BlockItem> STREET_SIGN = ITEMS.register(
            "street_sign",
            () -> new DescribedBlockItem(
                    ModBlocks.STREET_SIGN.get(),
                    new Item.Properties(),
                    "item.trafficcontrol.street_sign.description"
            )
    );
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_LIGHT_5 = ITEMS.register("traffic_light_5", () -> new BlockItem(ModBlocks.TRAFFIC_LIGHT_5.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_LIGHT_5_RIGHT = ITEMS.register("traffic_light_5_right", () -> new BlockItem(ModBlocks.TRAFFIC_LIGHT_5_RIGHT.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_LIGHT_5_UPPER = ITEMS.register("traffic_light_5_upper", () -> new BlockItem(ModBlocks.TRAFFIC_LIGHT_5_UPPER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_LIGHT_DOGHOUSE = ITEMS.register("traffic_light_doghouse", () -> new BlockItem(ModBlocks.TRAFFIC_LIGHT_DOGHOUSE.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> FLASHER_AMBER = ITEMS.register(
            "flasher_amber", () -> new BlockItem(ModBlocks.FLASHER_AMBER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> FLASHER_RED = ITEMS.register(
            "flasher_red", () -> new BlockItem(ModBlocks.FLASHER_RED.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_LIGHT_1 = ITEMS.register("traffic_light_1", () -> new BlockItem(ModBlocks.TRAFFIC_LIGHT_1.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_LIGHT_2 = ITEMS.register("traffic_light_2", () -> new BlockItem(ModBlocks.TRAFFIC_LIGHT_2.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> RAMP_METER_SIGNAL = ITEMS.register(
            "ramp_meter_signal", () -> new BlockItem(ModBlocks.RAMP_METER_SIGNAL.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_LIGHT_4 = ITEMS.register("traffic_light_4", () -> new BlockItem(ModBlocks.TRAFFIC_LIGHT_4.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_LIGHT_6 = ITEMS.register("traffic_light_6", () -> new BlockItem(ModBlocks.TRAFFIC_LIGHT_6.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> PEDESTRIAN_BUTTON = ITEMS.register("pedestrian_button", () -> new BlockItem(ModBlocks.PEDESTRIAN_BUTTON.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TRAFFIC_SENSOR_RIGHT = ITEMS.register("traffic_sensor_right", () -> new DescribedBlockItem(ModBlocks.TRAFFIC_SENSOR_RIGHT.get(), new Item.Properties(), "item.trafficcontrol.traffic_sensor_right.description"));

    public static final Map<String, DeferredHolder<Item, BlockItem>> STREET_LIGHT_MODELS = new LinkedHashMap<>();

    static {
        ModBlocks.STREET_LIGHT_MODELS.forEach((id, block) -> {
            DeferredHolder<Item, BlockItem> item = ITEMS.register(id, () -> {
                Item.Properties properties = new Item.Properties();
                if ("classic_street_light_bb".equals(id) || "classic_street_light_c".equals(id)) {
                    return new DescribedBlockItem(
                            block.get(),
                            properties,
                            "item.trafficcontrol." + id + ".description"
                    );
                }
                return new BlockItem(block.get(), properties);
            });
            STREET_LIGHT_MODELS.put(id, item);
        });
    }

    private ModItems() {}
}
