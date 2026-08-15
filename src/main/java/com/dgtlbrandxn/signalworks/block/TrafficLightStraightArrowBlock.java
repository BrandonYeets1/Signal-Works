package com.dgtlbrandxn.signalworks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Dedicated red/yellow/green straight-arrow signal head. */
public final class TrafficLightStraightArrowBlock extends SpecialThreeSectionSignalBlock {
    public static final MapCodec<TrafficLightStraightArrowBlock> CODEC = simpleCodec(TrafficLightStraightArrowBlock::new);
    public TrafficLightStraightArrowBlock(BlockBehaviour.Properties properties) { super(properties); }
    @Override protected MapCodec<? extends TrafficLightStraightArrowBlock> codec() { return CODEC; }
}
