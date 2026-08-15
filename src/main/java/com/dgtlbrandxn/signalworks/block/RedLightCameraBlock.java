package com.dgtlbrandxn.signalworks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Traffic camera that scans only while receiving a redstone "red phase" signal. */
public final class RedLightCameraBlock extends TrafficCameraBlock {
    public static final MapCodec<RedLightCameraBlock> CODEC = simpleCodec(RedLightCameraBlock::new);

    public RedLightCameraBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends RedLightCameraBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean requiresRedSignal() {
        return true;
    }
}
