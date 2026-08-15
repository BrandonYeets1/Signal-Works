package com.dgtlbrandxn.signalworks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Dedicated red/yellow/green right-arrow signal head. */
public final class TrafficLightTurnRightBlock extends DedicatedTurnSignalBlock {
    public static final MapCodec<TrafficLightTurnRightBlock> CODEC = simpleCodec(TrafficLightTurnRightBlock::new);

    public TrafficLightTurnRightBlock(BlockBehaviour.Properties properties) {
        super(properties, true);
    }

    @Override
    protected MapCodec<? extends TrafficLightTurnRightBlock> codec() {
        return CODEC;
    }
}
