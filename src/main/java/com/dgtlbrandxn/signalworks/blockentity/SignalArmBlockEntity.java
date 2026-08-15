package com.dgtlbrandxn.signalworks.blockentity;

import com.dgtlbrandxn.signalworks.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Render anchor for the state-driven adjustable signal arm. */
public final class SignalArmBlockEntity extends BlockEntity {
    public SignalArmBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGNAL_ARM.get(), pos, state);
    }
}
