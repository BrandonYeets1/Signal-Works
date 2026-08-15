package com.dgtlbrandxn.signalworks.runtime;

import com.dgtlbrandxn.signalworks.block.StreetLightModelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Keeps imported JSON streetlights alive after world reloads.
 *
 * <p>Milestone 2.9.2 relied on a scheduled block tick created during placement.
 * Fixtures that already existed before that update never received the initial
 * scheduled tick, so their {@code lit} state remained false forever. This
 * runtime indexes fixtures when chunks load, then refreshes them once per
 * second without forcing unloaded chunks to load.</p>
 */
public final class StreetLightRuntime {
    private static final int UPDATE_INTERVAL = 20;
    private static final int CHUNKS_SCANNED_PER_TICK = 2;

    private static final Map<ServerLevel, Set<Long>> FIXTURES = new WeakHashMap<>();
    private static final Map<ServerLevel, ArrayDeque<Long>> PENDING_CHUNKS = new WeakHashMap<>();

    private StreetLightRuntime() {
    }

    public static void register(ServerLevel level, BlockPos pos) {
        FIXTURES.computeIfAbsent(level, ignored -> new LinkedHashSet<>()).add(pos.asLong());
    }

    public static void unregister(ServerLevel level, BlockPos pos) {
        Set<Long> positions = FIXTURES.get(level);
        if (positions != null) {
            positions.remove(pos.asLong());
        }
    }

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        long packed = event.getChunk().getPos().toLong();
        ArrayDeque<Long> queue = PENDING_CHUNKS.computeIfAbsent(level, ignored -> new ArrayDeque<>());
        if (!queue.contains(packed)) {
            queue.addLast(packed);
        }
    }

    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ChunkPos unloaded = event.getChunk().getPos();
        Set<Long> positions = FIXTURES.get(level);
        if (positions != null) {
            positions.removeIf(packed -> {
                BlockPos pos = BlockPos.of(packed);
                return (pos.getX() >> 4) == unloaded.x && (pos.getZ() >> 4) == unloaded.z;
            });
        }
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        scanPendingChunks(level);

        if (level.getGameTime() % UPDATE_INTERVAL != 0L) {
            return;
        }

        Set<Long> positions = FIXTURES.get(level);
        if (positions == null || positions.isEmpty()) {
            return;
        }

        Iterator<Long> iterator = positions.iterator();
        while (iterator.hasNext()) {
            BlockPos pos = BlockPos.of(iterator.next());
            if (!level.isLoaded(pos)) {
                continue;
            }

            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof StreetLightModelBlock streetLight) {
                streetLight.refreshLighting(level, pos, state);
            } else {
                iterator.remove();
            }
        }
    }

    private static void scanPendingChunks(ServerLevel level) {
        ArrayDeque<Long> queue = PENDING_CHUNKS.get(level);
        if (queue == null || queue.isEmpty()) {
            return;
        }

        for (int scan = 0; scan < CHUNKS_SCANNED_PER_TICK && !queue.isEmpty(); scan++) {
            ChunkPos chunkPos = new ChunkPos(queue.removeFirst());
            LevelChunk chunk = level.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
            if (chunk != null) {
                scanChunk(level, chunk);
            }
        }
    }

    private static void scanChunk(ServerLevel level, LevelChunk chunk) {
        LevelChunkSection[] sections = chunk.getSections();
        int minSection = level.getMinSection();
        for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
            LevelChunkSection section = sections[sectionIndex];
            if (section.hasOnlyAir()
                    || !section.maybeHas(state -> state.getBlock() instanceof StreetLightModelBlock)) {
                continue;
            }

            int baseY = (minSection + sectionIndex) << 4;
            for (int localY = 0; localY < 16; localY++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    for (int localX = 0; localX < 16; localX++) {
                        BlockState state = section.getBlockState(localX, localY, localZ);
                        if (!(state.getBlock() instanceof StreetLightModelBlock streetLight)) {
                            continue;
                        }

                        BlockPos pos = new BlockPos(
                                chunk.getPos().getMinBlockX() + localX,
                                baseY + localY,
                                chunk.getPos().getMinBlockZ() + localZ
                        );
                        register(level, pos);
                        streetLight.refreshLighting(level, pos, state);
                    }
                }
            }
        }
    }
}
