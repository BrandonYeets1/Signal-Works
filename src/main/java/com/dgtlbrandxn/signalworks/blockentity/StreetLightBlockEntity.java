package com.dgtlbrandxn.signalworks.blockentity;

import com.dgtlbrandxn.signalworks.block.AbstractStreetLightBlock;
import com.dgtlbrandxn.signalworks.block.StreetLightDoubleBlock;
import com.dgtlbrandxn.signalworks.block.StreetLightHeadBlock;
import com.dgtlbrandxn.signalworks.registry.ModBlockEntities;
import com.dgtlbrandxn.signalworks.runtime.StreetLightIllumination;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


/** Render anchor and invisible-lamp manager for both street-light variants. */
public final class StreetLightBlockEntity extends BlockEntity {
    private static final int CHECK_INTERVAL = 10;

    public StreetLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STREET_LIGHT.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, StreetLightBlockEntity blockEntity) {
        if ((level.getGameTime() + Math.floorMod(pos.asLong(), CHECK_INTERVAL)) % CHECK_INTERVAL != 0L) {
            return;
        }

        boolean shouldBeLit = level.isNight() && !level.hasNeighborSignal(pos);

        // Modular HPS/LED fixtures are often mounted high enough that a light
        // emitted only from the fixture block fades before it reaches the road.
        // Keep the head itself emissive, then place invisible level-15 helpers
        // below and ahead of the optic so the roadway receives useful light.
        if (state.getBlock() instanceof StreetLightHeadBlock) {
            if (state.getValue(StreetLightHeadBlock.LIT) != shouldBeLit) {
                state = state.setValue(StreetLightHeadBlock.LIT, shouldBeLit);
                level.setBlock(pos, state, Block.UPDATE_ALL);
            }
            if (shouldBeLit) {
                ensureLightSources(level, pos, state);
            } else {
                removeLightSources(level, pos, state);
            }
            return;
        }

        if (state.hasProperty(AbstractStreetLightBlock.LIT)
                && state.getValue(AbstractStreetLightBlock.LIT) != shouldBeLit) {
            BlockState updated = state.setValue(AbstractStreetLightBlock.LIT, shouldBeLit);
            level.setBlock(pos, updated, Block.UPDATE_CLIENTS);
            state = updated;
        }

        if (shouldBeLit) {
            ensureLightSources(level, pos, state);
        } else {
            removeLightSources(level, pos, state);
        }
    }

    public static void ensureLightSources(Level level, BlockPos pos, BlockState state) {
        DirectionVector direction = directionVector(state);
        for (FixtureHead fixtureHead : getFixtureHeads(pos, state, direction)) {
            StreetLightIllumination.ensure(level, fixtureHead.pos(), fixtureHead.forwardX(), fixtureHead.forwardZ());
        }
    }

    public static void removeLightSources(Level level, BlockPos pos, BlockState state) {
        DirectionVector direction = directionVector(state);
        for (FixtureHead fixtureHead : getFixtureHeads(pos, state, direction)) {
            StreetLightIllumination.remove(level, fixtureHead.pos(), fixtureHead.forwardX(), fixtureHead.forwardZ());
        }
    }

    private static DirectionVector directionVector(BlockState state) {
        int rotation;
        if (state.hasProperty(AbstractStreetLightBlock.ROTATION)) {
            rotation = state.getValue(AbstractStreetLightBlock.ROTATION);
        } else if (state.hasProperty(StreetLightHeadBlock.ROTATION)) {
            rotation = state.getValue(StreetLightHeadBlock.ROTATION);
        } else {
            rotation = 0;
        }

        double angle = Math.toRadians(rotation * 22.5D);
        int stepX = (int) Math.round(-Math.sin(angle));
        int stepZ = (int) Math.round(Math.cos(angle));
        return new DirectionVector(stepX, stepZ);
    }

    private static FixtureHead[] getFixtureHeads(BlockPos pos, BlockState state, DirectionVector direction) {
        if (state.getBlock() instanceof StreetLightHeadBlock) {
            return new FixtureHead[] {new FixtureHead(pos, direction.x(), direction.z())};
        }

        int dx = direction.x() * 2;
        int dz = direction.z() * 2;
        BlockPos positiveHead = pos.offset(dx, 4, dz);
        if (state.getBlock() instanceof StreetLightDoubleBlock) {
            return new FixtureHead[] {
                    new FixtureHead(positiveHead, direction.x(), direction.z()),
                    new FixtureHead(pos.offset(-dx, 4, -dz), -direction.x(), -direction.z())
            };
        }
        return new FixtureHead[] {new FixtureHead(positiveHead, direction.x(), direction.z())};
    }

    private record DirectionVector(int x, int z) {
    }

    private record FixtureHead(BlockPos pos, int forwardX, int forwardZ) {
    }

}
