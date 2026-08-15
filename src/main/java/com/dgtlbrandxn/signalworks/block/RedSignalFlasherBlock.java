package com.dgtlbrandxn.signalworks.block;

import com.dgtlbrandxn.signalworks.util.TrafficLightBulbType;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class RedSignalFlasherBlock extends SignalFlasherBlock {
    public static final MapCodec<RedSignalFlasherBlock> CODEC = simpleCodec(RedSignalFlasherBlock::new);

    public RedSignalFlasherBlock(BlockBehaviour.Properties properties) {
        super(properties, TrafficLightBulbType.RED);
    }

    @Override
    protected MapCodec<? extends RedSignalFlasherBlock> codec() {
        return CODEC;
    }
}
