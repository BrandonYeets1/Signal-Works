package com.dgtlbrandxn.signalworks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Dedicated three-section white transit signal for bus queue-jump phases. */
public final class TrafficLightBusBlock extends SpecialThreeSectionSignalBlock {
    public static final MapCodec<TrafficLightBusBlock> CODEC = simpleCodec(TrafficLightBusBlock::new);
    public TrafficLightBusBlock(BlockBehaviour.Properties properties) { super(properties); }
    @Override protected MapCodec<? extends TrafficLightBusBlock> codec() { return CODEC; }
}
