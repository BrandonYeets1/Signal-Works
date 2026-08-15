package com.dgtlbrandxn.signalworks.runtime;

import com.dgtlbrandxn.signalworks.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Places invisible street-light emitters near the roadway instead of leaving
 * every light source at fixture height.
 *
 * <p>A level-15 source mounted eight or ten blocks above the road can be almost
 * invisible at pavement level. This helper projects several source positions
 * downward until it finds a sturdy surface, then places the source two blocks
 * above that surface. The result is a broad pool of light while the visible
 * fixture remains the apparent origin.</p>
 */
public final class StreetLightIllumination {
    private static final int MAX_DROP = 20;

    private StreetLightIllumination() {
    }

    public static void ensure(Level level, BlockPos fixturePos, int forwardX, int forwardZ) {
        for (BlockPos sourcePos : desiredSources(level, fixturePos, forwardX, forwardZ)) {
            if (!level.isLoaded(sourcePos)) {
                continue;
            }

            BlockState existing = level.getBlockState(sourcePos);
            if (existing.is(ModBlocks.LIGHT_SOURCE.get())) {
                continue;
            }
            if (existing.isAir()) {
                level.setBlock(sourcePos, ModBlocks.LIGHT_SOURCE.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    public static void remove(Level level, BlockPos fixturePos, int forwardX, int forwardZ) {
        for (BlockPos sourcePos : removableSources(level, fixturePos, forwardX, forwardZ)) {
            if (level.isLoaded(sourcePos) && level.getBlockState(sourcePos).is(ModBlocks.LIGHT_SOURCE.get())) {
                level.removeBlock(sourcePos, false);
            }
        }
    }

    private static Set<BlockPos> desiredSources(Level level, BlockPos fixturePos, int forwardX, int forwardZ) {
        int normalizedForwardX = Integer.compare(forwardX, 0);
        int normalizedForwardZ = Integer.compare(forwardZ, 0);
        if (normalizedForwardX == 0 && normalizedForwardZ == 0) {
            normalizedForwardZ = 1;
        }

        int sideX = -normalizedForwardZ;
        int sideZ = normalizedForwardX;
        // A single level-15 source high above a roadway fades much faster than
        // glowstone placed at eye level. Use a denser, forward-biased pool so
        // HPS and LED fixtures illuminate the pavement like actual luminaires.
        int[][] offsets = {
                {0, 0},
                {normalizedForwardX * 2, normalizedForwardZ * 2},
                {normalizedForwardX * 4, normalizedForwardZ * 4},
                {normalizedForwardX * 6, normalizedForwardZ * 6},
                {normalizedForwardX * 2 + sideX * 2, normalizedForwardZ * 2 + sideZ * 2},
                {normalizedForwardX * 2 - sideX * 2, normalizedForwardZ * 2 - sideZ * 2},
                {normalizedForwardX * 4 + sideX * 2, normalizedForwardZ * 4 + sideZ * 2},
                {normalizedForwardX * 4 - sideX * 2, normalizedForwardZ * 4 - sideZ * 2},
                {normalizedForwardX * 5 + sideX * 4, normalizedForwardZ * 5 + sideZ * 4},
                {normalizedForwardX * 5 - sideX * 4, normalizedForwardZ * 5 - sideZ * 4}
        };

        Set<BlockPos> result = new LinkedHashSet<>();
        for (int[] offset : offsets) {
            BlockPos columnTop = fixturePos.offset(offset[0], -1, offset[1]);
            BlockPos projected = projectToRoad(level, columnTop);
            if (projected != null) {
                BlockPos available = findAvailable(level, projected);
                if (available != null) {
                    result.add(available);
                }
            }
        }
        return result;
    }

    /**
     * Includes both the new road-projected locations and every legacy source
     * location used by earlier milestones so old invisible emitters do not linger.
     */
    private static Set<BlockPos> removableSources(Level level, BlockPos fixturePos, int forwardX, int forwardZ) {
        Set<BlockPos> result = new LinkedHashSet<>(desiredSources(level, fixturePos, forwardX, forwardZ));

        int normalizedForwardX = Integer.compare(forwardX, 0);
        int normalizedForwardZ = Integer.compare(forwardZ, 0);
        if (normalizedForwardX == 0 && normalizedForwardZ == 0) {
            normalizedForwardZ = 1;
        }

        BlockPos[] legacyTargets = {
                fixturePos,
                fixturePos.below(),
                fixturePos.offset(normalizedForwardX * 2, -2, normalizedForwardZ * 2),
                fixturePos.offset(normalizedForwardX * 4, -3, normalizedForwardZ * 4)
        };
        for (BlockPos target : legacyTargets) {
            addCandidateCloud(result, target);
        }
        return result;
    }

    @Nullable
    private static BlockPos projectToRoad(Level level, BlockPos columnTop) {
        BlockPos.MutableBlockPos cursor = columnTop.mutable();
        int minimumY = level.getMinBuildHeight();
        for (int drop = 0; drop <= MAX_DROP && cursor.getY() > minimumY; drop++) {
            BlockState state = level.getBlockState(cursor);
            if (drop >= 3 && state.isFaceSturdy(level, cursor, Direction.UP)) {
                // Keep the invisible emitter one block above the surface. This
                // matches the apparent brightness of a level-15 lamp such as a
                // redstone lamp while remaining hidden inside the light pool.
                BlockPos preferred = cursor.above().immutable();
                if (isAvailable(level, preferred)) {
                    return preferred;
                }
                BlockPos upper = cursor.above(2).immutable();
                if (isAvailable(level, upper)) {
                    return upper;
                }
            }
            cursor.move(Direction.DOWN);
        }

        // No floor was found nearby. Keep a fallback source several blocks below
        // the fixture so elevated lights still produce visible illumination.
        BlockPos fallback = columnTop.below(4).immutable();
        return isAvailable(level, fallback) ? fallback : null;
    }

    @Nullable
    private static BlockPos findAvailable(Level level, BlockPos preferred) {
        if (isAvailable(level, preferred)) {
            return preferred;
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos shifted = preferred.relative(direction);
            if (isAvailable(level, shifted)) {
                return shifted;
            }
        }
        return null;
    }

    private static boolean isAvailable(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.is(ModBlocks.LIGHT_SOURCE.get());
    }

    private static void addCandidateCloud(Set<BlockPos> positions, BlockPos target) {
        positions.add(target);
        positions.add(target.below());
        positions.add(target.above());
        positions.add(target.north());
        positions.add(target.south());
        positions.add(target.east());
        positions.add(target.west());
        positions.add(target.north().below());
        positions.add(target.south().below());
        positions.add(target.east().below());
        positions.add(target.west().below());
    }
}
