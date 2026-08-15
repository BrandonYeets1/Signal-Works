package com.dgtlbrandxn.signalworks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Five-section R/Y/G + yellow/right-arrow + green/right-arrow signal head. */
public final class TrafficLight5RightBlock extends TrafficLight5Block {
    public static final MapCodec<TrafficLight5RightBlock> CODEC = simpleCodec(TrafficLight5RightBlock::new);

    public TrafficLight5RightBlock(BlockBehaviour.Properties properties) {
        super(properties, true);
    }

    @Override
    protected MapCodec<? extends TrafficLight5RightBlock> codec() {
        return CODEC;
    }
}
