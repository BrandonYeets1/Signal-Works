package com.dgtlbrandxn.signalworks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Dedicated red/yellow/green left-arrow signal head. */
public final class TrafficLightTurnLeftBlock extends DedicatedTurnSignalBlock {
    public static final MapCodec<TrafficLightTurnLeftBlock> CODEC = simpleCodec(TrafficLightTurnLeftBlock::new);

    public TrafficLightTurnLeftBlock(BlockBehaviour.Properties properties) {
        super(properties, false);
    }

    @Override
    protected MapCodec<? extends TrafficLightTurnLeftBlock> codec() {
        return CODEC;
    }
}
