package com.dgtlbrandxn.signalworks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class StreetLightDoubleBlock extends AbstractStreetLightBlock {
    public static final MapCodec<StreetLightDoubleBlock> CODEC = simpleCodec(StreetLightDoubleBlock::new);

    public StreetLightDoubleBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }
}
