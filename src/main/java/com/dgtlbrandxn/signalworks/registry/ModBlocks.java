package com.dgtlbrandxn.signalworks.registry;

import com.dgtlbrandxn.signalworks.TrafficControl;
import com.dgtlbrandxn.signalworks.block.AbstractStreetLightBlock;
import com.dgtlbrandxn.signalworks.block.SignagePropBlock;
import com.dgtlbrandxn.signalworks.block.RedSignalFlasherBlock;
import com.dgtlbrandxn.signalworks.block.AmberSignalFlasherBlock;
import com.dgtlbrandxn.signalworks.block.LightSourceBlock;
import com.dgtlbrandxn.signalworks.block.ConcreteBarrierBlock;
import com.dgtlbrandxn.signalworks.block.ConstructionFloodlightBlock;
import com.dgtlbrandxn.signalworks.block.ConstructionMessageBoardBlock;
import com.dgtlbrandxn.signalworks.block.PedestrianButtonBlock;
import com.dgtlbrandxn.signalworks.block.RampMeterSignalBlock;
import com.dgtlbrandxn.signalworks.block.FreewaySignPanelBlock;
import com.dgtlbrandxn.signalworks.block.GuardrailBlock;
import com.dgtlbrandxn.signalworks.block.RotatablePoleBlock;
import com.dgtlbrandxn.signalworks.block.SignalArmBlock;
import com.dgtlbrandxn.signalworks.block.StreetLightDoubleBlock;
import com.dgtlbrandxn.signalworks.block.StreetLightSingleBlock;
import com.dgtlbrandxn.signalworks.block.StreetLightHeadBlock;
import com.dgtlbrandxn.signalworks.block.StreetLightModelBlock;
import com.dgtlbrandxn.signalworks.block.StreetLightArmBlock;
import com.dgtlbrandxn.signalworks.block.TrafficLight1Block;
import com.dgtlbrandxn.signalworks.block.TrafficLight2Block;
import com.dgtlbrandxn.signalworks.block.TrafficLight4Block;
import com.dgtlbrandxn.signalworks.block.TrafficLight5Block;
import com.dgtlbrandxn.signalworks.block.TrafficLight5RightBlock;
import com.dgtlbrandxn.signalworks.block.TrafficLightBlock;
import com.dgtlbrandxn.signalworks.block.TrafficLightDoghouseBlock;
import com.dgtlbrandxn.signalworks.block.TrafficLightTurnLeftBlock;
import com.dgtlbrandxn.signalworks.block.TrafficLightTurnRightBlock;
import com.dgtlbrandxn.signalworks.block.TrafficLightUTurnBlock;
import com.dgtlbrandxn.signalworks.block.TrafficLightBusBlock;
import com.dgtlbrandxn.signalworks.block.TrafficLightStraightArrowBlock;
import com.dgtlbrandxn.signalworks.block.TrafficSensorBlock;
import com.dgtlbrandxn.signalworks.block.TrafficLightControllerBlock;
import com.dgtlbrandxn.signalworks.block.TrafficCameraBlock;
import com.dgtlbrandxn.signalworks.block.RedLightCameraBlock;
import com.dgtlbrandxn.signalworks.block.RoadSignBlock;
import com.dgtlbrandxn.signalworks.block.RoadFlareBlock;
import com.dgtlbrandxn.signalworks.block.RoadDecalBlock;
import com.dgtlbrandxn.signalworks.block.UtilityPoleBlock;
import com.dgtlbrandxn.signalworks.block.UtilityWireBlock;
import com.dgtlbrandxn.signalworks.block.UtilityGroundWireBlock;
import com.dgtlbrandxn.signalworks.block.UtilityGuyWireBlock;
import com.dgtlbrandxn.signalworks.block.PoleTransformerBlock;
import com.dgtlbrandxn.signalworks.block.LaneControlSignBlock;
import com.dgtlbrandxn.signalworks.block.MunicipalStreetSignBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, TrafficControl.MOD_ID);

    public static final DeferredHolder<Block, Block> CROSSING_GATE_BASE = BLOCKS.register("crossing_gate_base", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> STAND = BLOCKS.register("stand", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> CROSSING_GATE_GATE = BLOCKS.register("crossing_gate_gate", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> CROSSING_GATE_LAMPS = BLOCKS.register("crossing_gate_lamps", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> CROSSING_GATE_POLE = BLOCKS.register("crossing_gate_pole", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> CROSSING_GATE_CROSSBUCK = BLOCKS.register("crossing_gate_crossbuck", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> SAFETRAN_TYPE_3 = BLOCKS.register("safetran_type_3", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> CROSSING_RELAY_SE = BLOCKS.register("crossing_relay_se", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> CROSSING_RELAY_SW = BLOCKS.register("crossing_relay_sw", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> CROSSING_RELAY_NW = BLOCKS.register("crossing_relay_nw", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> CROSSING_RELAY_NE = BLOCKS.register("crossing_relay_ne", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> CROSSING_RELAY_TOP_SW = BLOCKS.register("crossing_relay_top_sw", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> CROSSING_RELAY_TOP_SE = BLOCKS.register("crossing_relay_top_se", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> CROSSING_RELAY_TOP_NW = BLOCKS.register("crossing_relay_top_nw", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> CROSSING_RELAY_TOP_NE = BLOCKS.register("crossing_relay_top_ne", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> OVERHEAD_POLE = BLOCKS.register("overhead_pole", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> OVERHEAD = BLOCKS.register("overhead", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> OVERHEAD_LAMPS = BLOCKS.register("overhead_lamps", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> OVERHEAD_CROSSBUCK = BLOCKS.register("overhead_crossbuck", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> SAFETRAN_MECHANICAL = BLOCKS.register("safetran_mechanical", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, SignagePropBlock> PROP_SIGN_SIGNALAHEAD = BLOCKS.register(
            "prop_sign_signalahead",
            () -> new SignagePropBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion().noCollission())
    );
    public static final DeferredHolder<Block, FreewaySignPanelBlock> FREEWAY_GUIDE_SIGN = BLOCKS.register(
            "freeway_guide_sign",
            () -> new FreewaySignPanelBlock(BlockBehaviour.Properties.of().strength(2.5F).noOcclusion().noCollission())
    );
    public static final DeferredHolder<Block, RoadSignBlock> SIGN = BLOCKS.register(
            "sign", () -> new RoadSignBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> CONE = BLOCKS.register("cone", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));

    /**
     * Hidden compatibility alias for worlds that used the original single channelizer.
     * New builds should use CHANNELIZER_ORANGE or CHANNELIZER_GREY.
     */
    @Deprecated(forRemoval = false)
    public static final DeferredHolder<Block, Block> CHANNELIZER = BLOCKS.register("channelizer", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> CHANNELIZER_ORANGE = BLOCKS.register("channelizer_orange", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> CHANNELIZER_GREY = BLOCKS.register("channelizer_grey", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> DRUM = BLOCKS.register("drum", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, RoadFlareBlock> ROAD_FLARE = BLOCKS.register(
            "road_flare", () -> new RoadFlareBlock(BlockBehaviour.Properties.of()
                    .strength(0.25F).noOcclusion().noCollission()
                    .lightLevel(state -> state.getValue(RoadFlareBlock.LIT) ? 7 : 0)));
    // 48 model units = 3 Minecraft blocks: designed for three-block-wide lanes.
    public static final DeferredHolder<Block, RoadDecalBlock> ROAD_DECAL_LEFT_ONLY = roadDecal("road_decal_left_only");
    public static final DeferredHolder<Block, RoadDecalBlock> ROAD_DECAL_RIGHT_ONLY = roadDecal("road_decal_right_only");
    public static final DeferredHolder<Block, RoadDecalBlock> ROAD_DECAL_STRAIGHT_ONLY = roadDecal("road_decal_straight_only");
    public static final DeferredHolder<Block, RoadDecalBlock> ROAD_DECAL_LEFT_STRAIGHT = roadDecal("road_decal_left_straight");
    public static final DeferredHolder<Block, RoadDecalBlock> ROAD_DECAL_RIGHT_STRAIGHT = roadDecal("road_decal_right_straight");
    public static final DeferredHolder<Block, RoadDecalBlock> ROAD_DECAL_LEFT_RIGHT = roadDecal("road_decal_left_right");
    public static final DeferredHolder<Block, RoadDecalBlock> ROAD_DECAL_MERGE_LEFT = roadDecal("road_decal_merge_left");
    public static final DeferredHolder<Block, RoadDecalBlock> ROAD_DECAL_MERGE_RIGHT = roadDecal("road_decal_merge_right");

    // Signal Works Utilities: modular poles, conductors and pole hardware.
    public static final DeferredHolder<Block, UtilityPoleBlock> UTILITY_POLE_WOOD = BLOCKS.register(
            "utility_pole_wood", () -> new UtilityPoleBlock(false, BlockBehaviour.Properties.of().strength(3.0F).noOcclusion()));
    public static final DeferredHolder<Block, UtilityPoleBlock> UTILITY_POLE_METAL = BLOCKS.register(
            "utility_pole_metal", () -> new UtilityPoleBlock(true, BlockBehaviour.Properties.of().strength(4.0F).noOcclusion()));
    public static final DeferredHolder<Block, UtilityWireBlock> UTILITY_WIRE = BLOCKS.register(
            "utility_wire", () -> new UtilityWireBlock(BlockBehaviour.Properties.of().strength(0.2F).noOcclusion().noCollission()));
    public static final DeferredHolder<Block, UtilityGroundWireBlock> UTILITY_GROUND_WIRE = BLOCKS.register(
            "utility_ground_wire", () -> new UtilityGroundWireBlock(BlockBehaviour.Properties.of().strength(0.2F).noOcclusion().noCollission()));
    public static final DeferredHolder<Block, UtilityGuyWireBlock> UTILITY_GUY_WIRE = BLOCKS.register(
            "utility_guy_wire", () -> new UtilityGuyWireBlock(BlockBehaviour.Properties.of().strength(0.3F).noOcclusion().noCollission()));
    public static final DeferredHolder<Block, PoleTransformerBlock> POLE_TRANSFORMER = BLOCKS.register(
            "pole_transformer", () -> new PoleTransformerBlock(BlockBehaviour.Properties.of().strength(3.0F).noOcclusion()));

    public static final DeferredHolder<Block, ConstructionFloodlightBlock> CONSTRUCTION_FLOODLIGHT = BLOCKS.register(
            "construction_floodlight",
            () -> new ConstructionFloodlightBlock(BlockBehaviour.Properties.of()
                    .strength(2.5F)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(ConstructionFloodlightBlock.DEPLOYED)
                            && state.getValue(ConstructionFloodlightBlock.LIT) ? 15 : 0))
    );
    public static final DeferredHolder<Block, ConstructionMessageBoardBlock> CONSTRUCTION_MESSAGE_BOARD = BLOCKS.register(
            "construction_message_board",
            () -> new ConstructionMessageBoardBlock(BlockBehaviour.Properties.of()
                    .strength(2.5F)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(ConstructionMessageBoardBlock.DEPLOYED)
                            && state.getValue(ConstructionMessageBoardBlock.LIT) ? 7 : 0))
    );
    public static final DeferredHolder<Block, StreetLightSingleBlock> STREET_LIGHT_SINGLE = BLOCKS.register(
            "street_light_single",
            () -> new StreetLightSingleBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(AbstractStreetLightBlock.LIT) ? 7 : 0))
    );
    public static final DeferredHolder<Block, LightSourceBlock> LIGHT_SOURCE = BLOCKS.register(
            "light_source",
            () -> new LightSourceBlock(BlockBehaviour.Properties.of()
                    .strength(0.0F)
                    .noCollission()
                    .noOcclusion()
                    .replaceable()
                    .lightLevel(state -> 15))
    );
    public static final DeferredHolder<Block, StreetLightDoubleBlock> STREET_LIGHT_DOUBLE = BLOCKS.register(
            "street_light_double",
            () -> new StreetLightDoubleBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(AbstractStreetLightBlock.LIT) ? 7 : 0))
    );
    public static final DeferredHolder<Block, StreetLightArmBlock> STREETLIGHT_ARM_STRAIGHT = BLOCKS.register(
            "streetlight_arm_straight", () -> new StreetLightArmBlock(
                    BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, StreetLightArmBlock> STREETLIGHT_ARM_UPSWEEP = BLOCKS.register(
            "streetlight_arm_upsweep", () -> new StreetLightArmBlock(
                    BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, StreetLightArmBlock> STREETLIGHT_ARM_CURVED = BLOCKS.register(
            "streetlight_arm_curved", () -> new StreetLightArmBlock(
                    BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, StreetLightHeadBlock> STREET_LIGHT_HPS_M400A2 = BLOCKS.register(
            "street_light_hps_m400a2", () -> new StreetLightHeadBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F).noOcclusion()
                    .lightLevel(state -> state.getValue(StreetLightHeadBlock.LIT) ? 12 : 0)));
    public static final DeferredHolder<Block, StreetLightHeadBlock> STREET_LIGHT_HPS_M400A2_CUTOFF = BLOCKS.register(
            "street_light_hps_m400a2_cutoff", () -> new StreetLightHeadBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F).noOcclusion()
                    .lightLevel(state -> state.getValue(StreetLightHeadBlock.LIT) ? 12 : 0)));
    public static final DeferredHolder<Block, StreetLightHeadBlock> STREET_LIGHT_LED_GCL = BLOCKS.register(
            "street_light_led_gcl", () -> new StreetLightHeadBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F).noOcclusion()
                    .lightLevel(state -> state.getValue(StreetLightHeadBlock.LIT) ? 15 : 0)));

    /** JSON-model fixture catalog imported from the supplied streetlights pack. */
    public static final Map<String, DeferredHolder<Block, StreetLightModelBlock>> STREET_LIGHT_MODELS = new LinkedHashMap<>();

    static {
        registerStreetLightModel("dim_street_light_2", 9);
        registerStreetLightModel("dim_street_light_3", 9);
        registerStreetLightModel("dim_street_light_4", 9);
        registerStreetLightModel("dim_street_light_5", 9);
        registerStreetLightModel("dim_street_light_6", 9);
        registerStreetLightModel("led_street_light_10", 15);
        registerStreetLightModel("led_street_light_1b", 15);
        registerStreetLightModel("led_street_light_2", 15);
        registerStreetLightModel("led_street_light_3", 15);
        registerStreetLightModel("led_street_light_4", 15);
        registerStreetLightModel("led_street_light_4b", 15);
        registerStreetLightModel("led_street_light_5", 15);
        registerStreetLightModel("led_street_light_6", 15);
        registerStreetLightModel("led_street_light_7", 15);
        registerStreetLightModel("led_street_light_8", 15);
        registerStreetLightModel("led_street_light_9", 15);
        registerStreetLightModel("standard_street_light_10", 12);
        registerStreetLightModel("standard_street_light_11", 12);
        registerStreetLightModel("standard_street_light_12", 12);
        registerStreetLightModel("standard_street_light_13", 12);
        registerStreetLightModel("standard_street_light_14", 12);
        registerStreetLightModel("standard_street_light_15", 12);
        registerStreetLightModel("standard_street_light_3a", 12);
        registerStreetLightModel("standard_street_light_3b", 12);
        registerStreetLightModel("standard_street_light_4", 12);
        registerStreetLightModel("standard_street_light_5", 12);
        registerStreetLightModel("standard_street_light_6", 12);
        registerStreetLightModel("standard_street_light_7", 12);
        registerStreetLightModel("standard_street_light_8a", 12);
        registerStreetLightModel("standard_street_light_8b", 12);
        registerStreetLightModel("standard_street_light_9", 12);
        registerStreetLightModel("standard_street_light_9b", 12);
        registerStreetLightModel("classic_street_light_aa", 12);
        registerStreetLightModel("classic_street_light_ab", 12);
        registerStreetLightModel("classic_street_light_ba", 12);
        registerStreetLightModel("classic_street_light_bb", 12);
        registerStreetLightModel("classic_street_light_c", 12);
        registerStreetLightModel("classic_street_light_da", 12);
        registerStreetLightModel("classic_street_light_db", 12);
    }

    private static void registerStreetLightModel(String id, int lightLevel) {
        STREET_LIGHT_MODELS.put(id, BLOCKS.register(id, () -> new StreetLightModelBlock(
                BlockBehaviour.Properties.of()
                        .strength(2.0F)
                        .noOcclusion()
                        .lightLevel(state -> state.getValue(StreetLightModelBlock.LIT) ? lightLevel : 0))));
    }

    public static final DeferredHolder<Block, TrafficLightBlock> TRAFFIC_LIGHT = BLOCKS.register(
            "traffic_light",
            () -> new TrafficLightBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(TrafficLightBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, TrafficLightControllerBlock> TRAFFIC_LIGHT_CONTROL_BOX = BLOCKS.register(
            "traffic_light_control_box",
            () -> new TrafficLightControllerBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion())
    );
    public static final DeferredHolder<Block, TrafficLightTurnLeftBlock> TRAFFIC_LIGHT_TURN_LEFT = BLOCKS.register(
            "traffic_light_turn_left",
            () -> new TrafficLightTurnLeftBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()
                    .lightLevel(state -> state.getValue(TrafficLightBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, TrafficLightTurnRightBlock> TRAFFIC_LIGHT_TURN_RIGHT = BLOCKS.register(
            "traffic_light_turn_right",
            () -> new TrafficLightTurnRightBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()
                    .lightLevel(state -> state.getValue(TrafficLightBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, TrafficLightStraightArrowBlock> TRAFFIC_LIGHT_STRAIGHT_ARROW = BLOCKS.register(
            "traffic_light_straight_arrow",
            () -> new TrafficLightStraightArrowBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()
                    .lightLevel(state -> state.getValue(TrafficLightBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, TrafficLightUTurnBlock> TRAFFIC_LIGHT_U_TURN = BLOCKS.register(
            "traffic_light_u_turn",
            () -> new TrafficLightUTurnBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()
                    .lightLevel(state -> state.getValue(TrafficLightBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, TrafficLightBusBlock> TRAFFIC_LIGHT_BUS = BLOCKS.register(
            "traffic_light_bus",
            () -> new TrafficLightBusBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()
                    .lightLevel(state -> state.getValue(TrafficLightBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, TrafficCameraBlock> TRAFFIC_CAMERA = BLOCKS.register(
            "traffic_camera",
            () -> new TrafficCameraBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion())
    );
    public static final DeferredHolder<Block, RedLightCameraBlock> RED_LIGHT_CAMERA = BLOCKS.register(
            "red_light_camera",
            () -> new RedLightCameraBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion())
    );
    public static final DeferredHolder<Block, LaneControlSignBlock> LANE_CONTROL_NO_LEFT = BLOCKS.register(
            "lane_control_no_left",
            () -> new LaneControlSignBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()
                    .lightLevel(state -> state.getValue(LaneControlSignBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, LaneControlSignBlock> LANE_CONTROL_NO_RIGHT = BLOCKS.register(
            "lane_control_no_right",
            () -> new LaneControlSignBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()
                    .lightLevel(state -> state.getValue(LaneControlSignBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, LaneControlSignBlock> LANE_CONTROL_NO_AHEAD = BLOCKS.register(
            "lane_control_no_ahead",
            () -> new LaneControlSignBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()
                    .lightLevel(state -> state.getValue(LaneControlSignBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, Block> WIG_WAG = BLOCKS.register("wig_wag", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> VERTICAL_WIG_WAG = BLOCKS.register("vertical_wig_wag", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> SHUNT_BORDER = BLOCKS.register("shunt_border", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> SHUNT_ISLAND = BLOCKS.register("shunt_island", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> TYPE_3_BARRIER = BLOCKS.register("type_3_barrier", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> TYPE_3_BARRIER_RIGHT = BLOCKS.register("type_3_barrier_right", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    /** Existing registry id retained as the normal connected guardrail middle section. */
    public static final DeferredHolder<Block, GuardrailBlock> TRAFFIC_RAIL = BLOCKS.register(
            "traffic_rail", () -> new GuardrailBlock(BlockBehaviour.Properties.of().strength(2.5F).noOcclusion()));
    public static final DeferredHolder<Block, GuardrailBlock> GUARDRAIL_END = BLOCKS.register(
            "guardrail_end", () -> new GuardrailBlock(BlockBehaviour.Properties.of().strength(2.5F).noOcclusion()));
    public static final DeferredHolder<Block, GuardrailBlock> GUARDRAIL_LED = BLOCKS.register(
            "guardrail_led", () -> new GuardrailBlock(BlockBehaviour.Properties.of().strength(2.5F).noOcclusion()
                    .lightLevel(state -> 5)));
    public static final DeferredHolder<Block, ConcreteBarrierBlock> CONCRETE_BARRIER = BLOCKS.register(
            "concrete_barrier", () -> new ConcreteBarrierBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> HORIZONTAL_POLE = BLOCKS.register("horizontal_pole", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, RotatablePoleBlock> SIGNAL_POLE_SMALL = BLOCKS.register(
            "signal_pole_small", () -> new RotatablePoleBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion(), 2.0D, 0.0D));
    public static final DeferredHolder<Block, RotatablePoleBlock> SIGNAL_POLE_MEDIUM = BLOCKS.register(
            "signal_pole_medium", () -> new RotatablePoleBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion(), 4.0D, 0.0D));
    public static final DeferredHolder<Block, RotatablePoleBlock> SIGNAL_POLE_LARGE = BLOCKS.register(
            "signal_pole_large", () -> new RotatablePoleBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion(), 6.0D, 0.0D));
    public static final DeferredHolder<Block, RotatablePoleBlock> MAST_ARM_SMALL = BLOCKS.register(
            "mast_arm_small", () -> new RotatablePoleBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion(), 2.0D, 16.0D));
    public static final DeferredHolder<Block, RotatablePoleBlock> MAST_ARM_MEDIUM = BLOCKS.register(
            "mast_arm_medium", () -> new RotatablePoleBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion(), 4.0D, 16.0D));
    public static final DeferredHolder<Block, RotatablePoleBlock> MAST_ARM_LARGE = BLOCKS.register(
            "mast_arm_large", () -> new RotatablePoleBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion(), 6.0D, 16.0D));
    public static final DeferredHolder<Block, RotatablePoleBlock> FREEWAY_SIGN_POLE = BLOCKS.register(
            "freeway_sign_pole", () -> new RotatablePoleBlock(BlockBehaviour.Properties.of().strength(3.0F).noOcclusion(), 6.0D, 0.0D));
    public static final DeferredHolder<Block, RotatablePoleBlock> FREEWAY_SIGN_MAST = BLOCKS.register(
            "freeway_sign_mast", () -> new RotatablePoleBlock(BlockBehaviour.Properties.of().strength(3.0F).noOcclusion(), 6.0D, 16.0D));
    public static final DeferredHolder<Block, SignalArmBlock> SIGNAL_ARM = BLOCKS.register(
            "signal_arm", () -> new SignalArmBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> WCH_BELL = BLOCKS.register("wch_bell", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, Block> WCH_MECHANICAL_BELL = BLOCKS.register("wch_mechanical_bell", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, TrafficSensorBlock> TRAFFIC_SENSOR_LEFT = BLOCKS.register("traffic_sensor_left", () -> new TrafficSensorBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion().noCollission()));
    public static final DeferredHolder<Block, TrafficSensorBlock> TRAFFIC_SENSOR_STRAIGHT = BLOCKS.register("traffic_sensor_straight", () -> new TrafficSensorBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion().noCollission()));
    public static final DeferredHolder<Block, TrafficSensorBlock> TRAFFIC_SENSOR_BUS = BLOCKS.register("traffic_sensor_bus", () -> new TrafficSensorBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion().noCollission()));
    public static final DeferredHolder<Block, MunicipalStreetSignBlock> STREET_SIGN = BLOCKS.register(
            "street_sign",
            () -> new MunicipalStreetSignBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion())
    );
    public static final DeferredHolder<Block, TrafficLight5Block> TRAFFIC_LIGHT_5 = BLOCKS.register(
            "traffic_light_5",
            () -> new TrafficLight5Block(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(TrafficLightBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, TrafficLight5RightBlock> TRAFFIC_LIGHT_5_RIGHT = BLOCKS.register(
            "traffic_light_5_right",
            () -> new TrafficLight5RightBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(TrafficLightBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, Block> TRAFFIC_LIGHT_5_UPPER = BLOCKS.register("traffic_light_5_upper", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, TrafficLightDoghouseBlock> TRAFFIC_LIGHT_DOGHOUSE = BLOCKS.register(
            "traffic_light_doghouse",
            () -> new TrafficLightDoghouseBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(TrafficLightBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, AmberSignalFlasherBlock> FLASHER_AMBER = BLOCKS.register(
            "flasher_amber",
            () -> new AmberSignalFlasherBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F).noOcclusion()
                    .lightLevel(state -> state.getValue(TrafficLightBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, RedSignalFlasherBlock> FLASHER_RED = BLOCKS.register(
            "flasher_red",
            () -> new RedSignalFlasherBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F).noOcclusion()
                    .lightLevel(state -> state.getValue(TrafficLightBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, TrafficLight1Block> TRAFFIC_LIGHT_1 = BLOCKS.register(
            "traffic_light_1",
            () -> new TrafficLight1Block(BlockBehaviour.Properties.of()
                    .strength(2.0F).noOcclusion()
                    .lightLevel(state -> state.getValue(TrafficLightBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, TrafficLight2Block> TRAFFIC_LIGHT_2 = BLOCKS.register(
            "traffic_light_2",
            () -> new TrafficLight2Block(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(TrafficLightBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, RampMeterSignalBlock> RAMP_METER_SIGNAL = BLOCKS.register(
            "ramp_meter_signal",
            () -> new RampMeterSignalBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(TrafficLightBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, TrafficLight4Block> TRAFFIC_LIGHT_4 = BLOCKS.register(
            "traffic_light_4",
            () -> new TrafficLight4Block(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(TrafficLightBlock.LIT) ? 8 : 0))
    );
    public static final DeferredHolder<Block, Block> TRAFFIC_LIGHT_6 = BLOCKS.register("traffic_light_6", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, PedestrianButtonBlock> PEDESTRIAN_BUTTON = BLOCKS.register("pedestrian_button", () -> new PedestrianButtonBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion()));
    public static final DeferredHolder<Block, TrafficSensorBlock> TRAFFIC_SENSOR_RIGHT = BLOCKS.register("traffic_sensor_right", () -> new TrafficSensorBlock(BlockBehaviour.Properties.of().strength(2.0F).noOcclusion().noCollission()));

    private static DeferredHolder<Block, RoadDecalBlock> roadDecal(String id) {
        return BLOCKS.register(id, () -> new RoadDecalBlock(BlockBehaviour.Properties.of()
                .strength(0.15F).noOcclusion().noCollission()));
    }

    private ModBlocks() {}
}
