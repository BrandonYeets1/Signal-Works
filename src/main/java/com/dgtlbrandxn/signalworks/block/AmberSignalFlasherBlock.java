package com.dgtlbrandxn.signalworks.block;

import com.dgtlbrandxn.signalworks.util.TrafficLightBulbType;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class AmberSignalFlasherBlock extends SignalFlasherBlock {
    public static final MapCodec<AmberSignalFlasherBlock> CODEC = simpleCodec(AmberSignalFlasherBlock::new);

    public AmberSignalFlasherBlock(BlockBehaviour.Properties properties) {
        super(properties, TrafficLightBulbType.YELLOW);
    }

    @Override
    protected MapCodec<? extends AmberSignalFlasherBlock> codec() {
        return CODEC;
    }
}
