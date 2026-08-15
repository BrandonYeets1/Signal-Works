package com.dgtlbrandxn.signalworks.runtime;

import com.dgtlbrandxn.signalworks.block.SignalMovement;
import com.dgtlbrandxn.signalworks.blockentity.TrafficLightControllerBlockEntity;
import com.dgtlbrandxn.signalworks.item.EngineerWandItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.joml.Vector3f;

import java.util.Map;

/** Server-side link visualization while an engineer wand is held. */
public final class IntersectionLinkRuntime {
    private static final int OVERLAY_INTERVAL = 10;
    private static final double MAX_OVERLAY_DISTANCE_SQR = 96.0D * 96.0D;

    private IntersectionLinkRuntime() {
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || level.getGameTime() % OVERLAY_INTERVAL != 0L) {
            return;
        }

        for (ServerPlayer player : level.players()) {
            ItemStack wand = heldWand(player);
            if (wand.isEmpty()) {
                continue;
            }

            BlockPos controllerPos = EngineerWandItem.selectedController(wand, level);
            if (controllerPos == null
                    || player.distanceToSqr(Vec3.atCenterOf(controllerPos)) > MAX_OVERLAY_DISTANCE_SQR
                    || !(level.getBlockEntity(controllerPos) instanceof TrafficLightControllerBlockEntity controller)) {
                continue;
            }

            controller.pruneInvalidLinks();
            Vec3 origin = Vec3.atCenterOf(controllerPos).add(0.0D, 0.75D, 0.0D);
            spawnPoint(level, origin, new DustParticleOptions(new Vector3f(0.2F, 1.0F, 0.45F), 1.15F));

            for (Map.Entry<BlockPos, SignalMovement> entry : controller.linkedSignals().entrySet()) {
                BlockPos signalPos = entry.getKey();
                if (!level.isLoaded(signalPos)) {
                    continue;
                }
                Vec3 target = Vec3.atCenterOf(signalPos);
                renderLine(level, origin, target, particleFor(entry.getValue()));
            }
        }
    }

    private static ItemStack heldWand(ServerPlayer player) {
        if (EngineerWandItem.isEngineerWand(player.getMainHandItem())) {
            return player.getMainHandItem();
        }
        if (EngineerWandItem.isEngineerWand(player.getOffhandItem())) {
            return player.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }

    private static void renderLine(ServerLevel level, Vec3 start, Vec3 end, DustParticleOptions particle) {
        Vec3 delta = end.subtract(start);
        double distance = delta.length();
        int steps = Math.max(2, (int) Math.ceil(distance * 2.0D));
        for (int step = 0; step <= steps; step++) {
            double progress = step / (double) steps;
            Vec3 point = start.add(delta.scale(progress));
            spawnPoint(level, point, particle);
        }
    }

    private static void spawnPoint(ServerLevel level, Vec3 point, DustParticleOptions particle) {
        level.sendParticles(particle, point.x, point.y, point.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    private static DustParticleOptions particleFor(SignalMovement movement) {
        Vector3f color = switch (movement) {
            case THROUGH -> new Vector3f(0.1F, 0.65F, 1.0F);
            case LEFT -> new Vector3f(0.2F, 1.0F, 0.35F);
            case RIGHT -> new Vector3f(1.0F, 0.65F, 0.1F);
            case THROUGH_LEFT -> new Vector3f(0.75F, 0.25F, 1.0F);
            case THROUGH_RIGHT -> new Vector3f(0.2F, 0.9F, 0.8F);
            case PEDESTRIAN -> new Vector3f(1.0F, 1.0F, 1.0F);
            case U_TURN -> new Vector3f(1.0F, 0.25F, 0.12F);
            case BUS -> new Vector3f(0.95F, 0.95F, 0.85F);
        };
        return new DustParticleOptions(color, 0.85F);
    }
}
