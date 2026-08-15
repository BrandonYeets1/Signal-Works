package com.dgtlbrandxn.signalworks.client.render;

import com.dgtlbrandxn.signalworks.TrafficControl;
import com.dgtlbrandxn.signalworks.block.SignalArmBlock;
import com.dgtlbrandxn.signalworks.blockentity.SignalArmBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Adds only the merge tube between adjacent compact signal brackets. The primary
 * bracket geometry is the Blockbench model supplied for prop_traffic_arm_3bulb.
 */
public final class SignalArmBlockEntityRenderer implements BlockEntityRenderer<SignalArmBlockEntity> {
    private static final ResourceLocation SUPPORT_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TrafficControl.MOD_ID, "textures/block/prop_traffic_01.png"
    );

    public SignalArmBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            SignalArmBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof SignalArmBlock)) {
            return;
        }

        int left = state.getValue(SignalArmBlock.LEFT_LENGTH);
        int right = state.getValue(SignalArmBlock.RIGHT_LENGTH);
        if (left <= 0 && right <= 0) {
            return;
        }

        Direction facing = state.getValue(SignalArmBlock.FACING);
        Direction run = SignalArmBlock.rightDirection(state);
        float rearX = 8.0F - facing.getStepX() * 8.0F;
        float rearZ = 8.0F - facing.getStepZ() * 8.0F;

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(SUPPORT_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        int color = 0xFF111111;

        // For compact adjacent brackets both blocks know about the shared edge.
        // Render that edge from only one side (the local right connection) to avoid
        // two identical tubes occupying the same pixels. Legacy manually extended
        // wings still render in either direction when no adjacent bracket exists.
        boolean leftIsAdjacentMerge = left == 1
                && blockEntity.getLevel() != null
                && blockEntity.getLevel().getBlockState(blockEntity.getBlockPos().relative(run.getOpposite())).getBlock() instanceof SignalArmBlock;
        if (left > 0 && !leftIsAdjacentMerge) {
            drawRun(consumer, pose, rearX, rearZ, run.getOpposite(), left, packedLight, color);
        }
        if (right > 0) {
            drawRun(consumer, pose, rearX, rearZ, run, right, packedLight, color);
        }
    }

    private static void drawRun(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float startX,
            float startZ,
            Direction direction,
            int blocks,
            int packedLight,
            int color
    ) {
        float endX = startX + direction.getStepX() * blocks * 16.0F;
        float endZ = startZ + direction.getStepZ() * blocks * 16.0F;
        float half = 0.55F;
        drawCuboidPixels(
                consumer, pose,
                Math.min(startX, endX) - half, 17.15F, Math.min(startZ, endZ) - half,
                Math.max(startX, endX) + half, 18.25F, Math.max(startZ, endZ) + half,
                packedLight, color
        );
    }

    @Override
    public AABB getRenderBoundingBox(SignalArmBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(5.5D);
    }

    @Override
    public int getViewDistance() {
        return 512;
    }

    private static void drawCuboidPixels(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float minX,
            float minY,
            float minZ,
            float maxX,
            float maxY,
            float maxZ,
            int packedLight,
            int color
    ) {
        drawCuboid(
                consumer, pose,
                minX / 16.0F, minY / 16.0F, minZ / 16.0F,
                maxX / 16.0F, maxY / 16.0F, maxZ / 16.0F,
                packedLight, color
        );
    }

    private static void drawCuboid(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float minX,
            float minY,
            float minZ,
            float maxX,
            float maxY,
            float maxZ,
            int packedLight,
            int color
    ) {
        int overlay = OverlayTexture.NO_OVERLAY;

        vertex(consumer, pose, maxX, minY, maxZ, 1, 1, 0, 0, 1, color, packedLight, overlay);
        vertex(consumer, pose, maxX, maxY, maxZ, 1, 0, 0, 0, 1, color, packedLight, overlay);
        vertex(consumer, pose, minX, maxY, maxZ, 0, 0, 0, 0, 1, color, packedLight, overlay);
        vertex(consumer, pose, minX, minY, maxZ, 0, 1, 0, 0, 1, color, packedLight, overlay);

        vertex(consumer, pose, minX, minY, minZ, 1, 1, 0, 0, -1, color, packedLight, overlay);
        vertex(consumer, pose, minX, maxY, minZ, 1, 0, 0, 0, -1, color, packedLight, overlay);
        vertex(consumer, pose, maxX, maxY, minZ, 0, 0, 0, 0, -1, color, packedLight, overlay);
        vertex(consumer, pose, maxX, minY, minZ, 0, 1, 0, 0, -1, color, packedLight, overlay);

        vertex(consumer, pose, maxX, minY, minZ, 1, 1, 1, 0, 0, color, packedLight, overlay);
        vertex(consumer, pose, maxX, maxY, minZ, 1, 0, 1, 0, 0, color, packedLight, overlay);
        vertex(consumer, pose, maxX, maxY, maxZ, 0, 0, 1, 0, 0, color, packedLight, overlay);
        vertex(consumer, pose, maxX, minY, maxZ, 0, 1, 1, 0, 0, color, packedLight, overlay);

        vertex(consumer, pose, minX, minY, maxZ, 1, 1, -1, 0, 0, color, packedLight, overlay);
        vertex(consumer, pose, minX, maxY, maxZ, 1, 0, -1, 0, 0, color, packedLight, overlay);
        vertex(consumer, pose, minX, maxY, minZ, 0, 0, -1, 0, 0, color, packedLight, overlay);
        vertex(consumer, pose, minX, minY, minZ, 0, 1, -1, 0, 0, color, packedLight, overlay);

        vertex(consumer, pose, minX, maxY, minZ, 0, 1, 0, 1, 0, color, packedLight, overlay);
        vertex(consumer, pose, minX, maxY, maxZ, 0, 0, 0, 1, 0, color, packedLight, overlay);
        vertex(consumer, pose, maxX, maxY, maxZ, 1, 0, 0, 1, 0, color, packedLight, overlay);
        vertex(consumer, pose, maxX, maxY, minZ, 1, 1, 0, 1, 0, color, packedLight, overlay);

        vertex(consumer, pose, minX, minY, maxZ, 0, 1, 0, -1, 0, color, packedLight, overlay);
        vertex(consumer, pose, minX, minY, minZ, 0, 0, 0, -1, 0, color, packedLight, overlay);
        vertex(consumer, pose, maxX, minY, minZ, 1, 0, 0, -1, 0, color, packedLight, overlay);
        vertex(consumer, pose, maxX, minY, maxZ, 1, 1, 0, -1, 0, color, packedLight, overlay);
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x,
            float y,
            float z,
            float u,
            float v,
            float normalX,
            float normalY,
            float normalZ,
            int color,
            int packedLight,
            int overlay
    ) {
        consumer.addVertex(pose.pose(), x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(packedLight)
                .setNormal(pose, normalX, normalY, normalZ);
    }
}
