package com.dgtlbrandxn.signalworks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Stackable utility pole section used by the wood and metal line families. */
public final class UtilityPoleBlock extends Block {
    private final boolean metal;
    private static final VoxelShape WOOD_SHAPE = box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    private static final VoxelShape METAL_SHAPE = box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);

    public UtilityPoleBlock(boolean metal, BlockBehaviour.Properties properties) {
        super(properties);
        this.metal = metal;
    }

    public boolean isMetal() { return metal; }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return metal ? METAL_SHAPE : WOOD_SHAPE;
    }
}
