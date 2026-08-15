package com.dgtlbrandxn.signalworks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Dedicated U-turn head. Controller scheduling treats it as a protected left-turn movement. */
public final class TrafficLightUTurnBlock extends DedicatedTurnSignalBlock {
    public static final MapCodec<TrafficLightUTurnBlock> CODEC = simpleCodec(TrafficLightUTurnBlock::new);
    public TrafficLightUTurnBlock(BlockBehaviour.Properties properties) { super(properties, false); }
    @Override protected MapCodec<? extends TrafficLightUTurnBlock> codec() { return CODEC; }
}
