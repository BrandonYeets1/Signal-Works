package com.dgtlbrandxn.signalworks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class StreetLightSingleBlock extends AbstractStreetLightBlock {
    public static final MapCodec<StreetLightSingleBlock> CODEC = simpleCodec(StreetLightSingleBlock::new);

    public StreetLightSingleBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }
}
