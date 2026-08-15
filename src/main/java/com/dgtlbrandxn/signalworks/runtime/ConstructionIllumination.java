package com.dgtlbrandxn.signalworks.runtime;

import com.dgtlbrandxn.signalworks.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedHashSet;
import java.util.Set;

/** Maintains the broad, invisible pavement light pool produced by portable floodlight towers. */
public final class ConstructionIllumination {
    private ConstructionIllumination() {
    }

    public static void ensure(Level level, BlockPos towerPos, Direction facing) {
        for (BlockPos sourcePos : sourcePositions(towerPos, facing)) {
            if (!level.isLoaded(sourcePos)) {
                continue;
            }
            BlockState state = level.getBlockState(sourcePos);
            if (state.is(ModBlocks.LIGHT_SOURCE.get())) {
                continue;
            }
            if (state.isAir()) {
                level.setBlock(sourcePos, ModBlocks.LIGHT_SOURCE.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    public static void remove(Level level, BlockPos towerPos, Direction facing) {
        for (BlockPos sourcePos : sourcePositions(towerPos, facing)) {
            if (level.isLoaded(sourcePos) && level.getBlockState(sourcePos).is(ModBlocks.LIGHT_SOURCE.get())) {
                level.removeBlock(sourcePos, false);
            }
        }
    }

    private static Set<BlockPos> sourcePositions(BlockPos origin, Direction facing) {
        int fx = facing.getStepX();
        int fz = facing.getStepZ();
        int sx = -fz;
        int sz = fx;
        int[][] offsets = {
                {0, 1, 0},
                {fx * 2, 1, fz * 2},
                {fx * 4, 1, fz * 4},
                {fx * 6, 1, fz * 6},
                {fx * 2 + sx * 2, 1, fz * 2 + sz * 2},
                {fx * 2 - sx * 2, 1, fz * 2 - sz * 2},
                {fx * 4 + sx * 3, 1, fz * 4 + sz * 3},
                {fx * 4 - sx * 3, 1, fz * 4 - sz * 3},
                {fx * 6 + sx * 4, 1, fz * 6 + sz * 4},
                {fx * 6 - sx * 4, 1, fz * 6 - sz * 4},
                {sx * 3, 1, sz * 3},
                {-sx * 3, 1, -sz * 3}
        };
        Set<BlockPos> result = new LinkedHashSet<>();
        for (int[] offset : offsets) {
            result.add(origin.offset(offset[0], offset[1], offset[2]));
        }
        return result;
    }
}
