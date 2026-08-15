package com.dgtlbrandxn.signalworks.registry;

import com.dgtlbrandxn.signalworks.TrafficControl;
import com.dgtlbrandxn.signalworks.blockentity.StreetLightBlockEntity;
import com.dgtlbrandxn.signalworks.blockentity.ConstructionMessageBoardBlockEntity;
import com.dgtlbrandxn.signalworks.blockentity.MunicipalStreetSignBlockEntity;
import com.dgtlbrandxn.signalworks.blockentity.RoadSignBlockEntity;
import com.dgtlbrandxn.signalworks.blockentity.SignalArmBlockEntity;
import com.dgtlbrandxn.signalworks.blockentity.TrafficLightBlockEntity;
import com.dgtlbrandxn.signalworks.blockentity.TrafficLightControllerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TrafficControl.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrafficLightBlockEntity>> TRAFFIC_LIGHT =
            BLOCK_ENTITIES.register(
                    "traffic_light",
                    () -> BlockEntityType.Builder.of(
                            TrafficLightBlockEntity::new,
                            ModBlocks.TRAFFIC_LIGHT.get(),
                            ModBlocks.TRAFFIC_LIGHT_1.get(),
                            ModBlocks.TRAFFIC_LIGHT_4.get(),
                            ModBlocks.TRAFFIC_LIGHT_5.get(),
                            ModBlocks.TRAFFIC_LIGHT_5_RIGHT.get(),
                            ModBlocks.TRAFFIC_LIGHT_DOGHOUSE.get(),
                            ModBlocks.TRAFFIC_LIGHT_2.get(),
                            ModBlocks.RAMP_METER_SIGNAL.get(),
                            ModBlocks.TRAFFIC_LIGHT_TURN_LEFT.get(),
                            ModBlocks.TRAFFIC_LIGHT_TURN_RIGHT.get(),
                            ModBlocks.TRAFFIC_LIGHT_STRAIGHT_ARROW.get(),
                            ModBlocks.TRAFFIC_LIGHT_U_TURN.get(),
                            ModBlocks.TRAFFIC_LIGHT_BUS.get(),
                            ModBlocks.FLASHER_AMBER.get(),
                            ModBlocks.FLASHER_RED.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrafficLightControllerBlockEntity>> TRAFFIC_LIGHT_CONTROLLER =
            BLOCK_ENTITIES.register(
                    "traffic_light_controller",
                    () -> BlockEntityType.Builder.of(
                            TrafficLightControllerBlockEntity::new,
                            ModBlocks.TRAFFIC_LIGHT_CONTROL_BOX.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StreetLightBlockEntity>> STREET_LIGHT =
            BLOCK_ENTITIES.register(
                    "street_light",
                    () -> BlockEntityType.Builder.of(
                            StreetLightBlockEntity::new,
                            ModBlocks.STREET_LIGHT_SINGLE.get(),
                            ModBlocks.STREET_LIGHT_DOUBLE.get(),
                            ModBlocks.STREET_LIGHT_HPS_M400A2.get(),
                            ModBlocks.STREET_LIGHT_HPS_M400A2_CUTOFF.get(),
                            ModBlocks.STREET_LIGHT_LED_GCL.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SignalArmBlockEntity>> SIGNAL_ARM =
            BLOCK_ENTITIES.register(
                    "signal_arm",
                    () -> BlockEntityType.Builder.of(
                            SignalArmBlockEntity::new,
                            ModBlocks.SIGNAL_ARM.get()
                    ).build(null)
            );


    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RoadSignBlockEntity>> ROAD_SIGN =
            BLOCK_ENTITIES.register(
                    "road_sign",
                    () -> BlockEntityType.Builder.of(
                            RoadSignBlockEntity::new,
                            ModBlocks.SIGN.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MunicipalStreetSignBlockEntity>> MUNICIPAL_STREET_SIGN =
            BLOCK_ENTITIES.register(
                    "municipal_street_sign",
                    () -> BlockEntityType.Builder.of(
                            MunicipalStreetSignBlockEntity::new,
                            ModBlocks.STREET_SIGN.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ConstructionMessageBoardBlockEntity>> CONSTRUCTION_MESSAGE_BOARD =
            BLOCK_ENTITIES.register(
                    "construction_message_board",
                    () -> BlockEntityType.Builder.of(
                            ConstructionMessageBoardBlockEntity::new,
                            ModBlocks.CONSTRUCTION_MESSAGE_BOARD.get()
                    ).build(null)
            );

    private ModBlockEntities() {
    }
}
