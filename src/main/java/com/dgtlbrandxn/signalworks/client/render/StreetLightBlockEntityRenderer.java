package com.dgtlbrandxn.signalworks.client.render;

import com.dgtlbrandxn.signalworks.TrafficControl;
import com.dgtlbrandxn.signalworks.block.AbstractStreetLightBlock;
import com.dgtlbrandxn.signalworks.block.StreetLightDoubleBlock;
import com.dgtlbrandxn.signalworks.block.StreetLightHeadBlock;
import com.dgtlbrandxn.signalworks.blockentity.StreetLightBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Modern renderer for the original four-block-tall single and double street lights.
 * The old mod generated this geometry at runtime because it extends far outside one block.
 */
public final class StreetLightBlockEntityRenderer implements BlockEntityRenderer<StreetLightBlockEntity> {
    private static final ResourceLocation GENERIC = ResourceLocation.fromNamespaceAndPath(
            TrafficControl.MOD_ID, "textures/block/generic.png"
    );
    private static final ResourceLocation ORANGE = ResourceLocation.fromNamespaceAndPath(TrafficControl.MOD_ID, "textures/block/hps_orange.png");
    private static final ResourceLocation WHITE = ResourceLocation.fromNamespaceAndPath(TrafficControl.MOD_ID, "textures/block/led_white.png");
    private static final ResourceLocation YELLOW = ResourceLocation.fromNamespaceAndPath(
            TrafficControl.MOD_ID, "textures/block/yellow.png"
    );

    public StreetLightBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            StreetLightBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        BlockState state = blockEntity.getBlockState();
        if (state.getBlock() instanceof StreetLightHeadBlock) {
            renderHeadOnly(state, poseStack, bufferSource, packedLight, packedOverlay);
            return;
        }
        if (!(state.getBlock() instanceof AbstractStreetLightBlock)) {
            return;
        }

        VertexConsumer metal = bufferSource.getBuffer(RenderType.entityCutoutNoCull(GENERIC));
        String path = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        boolean led = path.contains("led_gcl");
        boolean cutoff = path.contains("cutoff");
        VertexConsumer lamp = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(led ? WHITE : ORANGE));

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.getValue(AbstractStreetLightBlock.ROTATION) * -22.5F));
        poseStack.translate(-0.5D, 0.0D, -0.5D);

        // Four-block-tall telescoping post.
        boxPixels(metal, poseStack.last(), 6, 0, 6, 4, 16, 4, packedLight, packedOverlay);
        boxPixels(metal, poseStack.last(), 6, 16, 6, 4, 16, 4, packedLight, packedOverlay);
        boxPixels(metal, poseStack.last(), 7, 32, 7, 2, 16, 2, packedLight, packedOverlay);
        boxPixels(metal, poseStack.last(), 7, 48, 7, 2, 16, 2, packedLight, packedOverlay);

        int lampLight = state.getValue(AbstractStreetLightBlock.LIT) ? LightTexture.FULL_BRIGHT : packedLight;
        drawPositiveHead(poseStack, metal, lamp, packedLight, lampLight, packedOverlay, led, cutoff);
        if (state.getBlock() instanceof StreetLightDoubleBlock) {
            drawNegativeHead(poseStack, metal, lamp, packedLight, lampLight, packedOverlay);
        }

        poseStack.popPose();
    }

    private static void renderHeadOnly(
            BlockState state,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        String path = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        boolean led = path.contains("led_gcl");
        boolean cutoff = path.contains("cutoff");
        boolean lit = state.getValue(StreetLightHeadBlock.LIT);

        VertexConsumer metal = bufferSource.getBuffer(RenderType.entityCutoutNoCull(GENERIC));
        VertexConsumer lamp = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(led ? WHITE : ORANGE));
        int lampLight = lit ? LightTexture.FULL_BRIGHT : packedLight;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.getValue(StreetLightHeadBlock.ROTATION) * -22.5F));
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        // Short rear tenon. It reaches the block boundary so the fixture sits
        // directly against a modular mast arm instead of looking suspended.
        boxPixels(metal, poseStack.last(), 7.0, 8.0, 0.0, 2.0, 2.0, 4.0, packedLight, packedOverlay);
        boxPixels(metal, poseStack.last(), 6.0, 7.5, 2.5, 4.0, 3.0, 3.0, packedLight, packedOverlay);

        if (led) {
            // GreenCobra-style low-profile LED fixture.
            boxPixels(metal, poseStack.last(), 4.0, 7.0, 4.0, 8.0, 4.0, 11.0, packedLight, packedOverlay);
            boxPixels(metal, poseStack.last(), 5.0, 11.0, 5.0, 6.0, 1.5, 8.5, packedLight, packedOverlay);
            boxPixels(lamp, poseStack.last(), 5.0, 6.75, 5.0, 6.0, 0.35, 9.0, lampLight, OverlayTexture.NO_OVERLAY);
        } else {
            // GE M400A2-style cobrahead. The cutoff version has a flatter lens,
            // while the standard version keeps a slightly dropped refractor.
            boxPixels(metal, poseStack.last(), 3.5, 6.5, 3.5, 9.0, 5.0, 11.5, packedLight, packedOverlay);
            boxPixels(metal, poseStack.last(), 4.5, 11.5, 5.0, 7.0, 2.0, 8.5, packedLight, packedOverlay);
            boxPixels(lamp, poseStack.last(), 5.0, cutoff ? 6.25 : 5.75, 5.0,
                    6.0, cutoff ? 0.35 : 0.85, 9.0, lampLight, OverlayTexture.NO_OVERLAY);
        }

        poseStack.popPose();
    }

    private static void drawPositiveHead(
            PoseStack poseStack,
            VertexConsumer metal,
            VertexConsumer lamp,
            int packedLight,
            int lampLight,
            int packedOverlay,
            boolean led,
            boolean cutoff
    ) {
        // Sloped connector from the top of the post to the luminaire.
        poseStack.pushPose();
        poseStack.translate(7.0 / 16.0, 60.0 / 16.0, 9.0 / 16.0);
        poseStack.mulPose(Axis.XP.rotationDegrees(-20.0F));
        box(metal, poseStack.last(), 0, 0, 0, 2.0 / 16.0, 2.0 / 16.0, 1.0, packedLight, packedOverlay);
        poseStack.popPose();

        if (led) {
            // Low-profile GreenCobra-inspired LED roadway head.
            boxPixels(metal, poseStack.last(), 5, 64, 23, 6, 3, 16, packedLight, packedOverlay);
            boxPixels(lamp, poseStack.last(), 6, 63.8, 25, 4, 0.35, 12, lampLight, OverlayTexture.NO_OVERLAY);
        } else {
            // GE M400A2-inspired cobrahead. Cutoff version uses a flat lower lens.
            boxPixels(metal, poseStack.last(), 4.5, 63, 22, 7, 4, 17, packedLight, packedOverlay);
            boxPixels(metal, poseStack.last(), 5.5, 67, 24, 5, 2, 12, packedLight, packedOverlay);
            boxPixels(lamp, poseStack.last(), 6, cutoff ? 62.8 : 62.3, 25, 4, cutoff ? 0.35 : 0.8, 12, lampLight, OverlayTexture.NO_OVERLAY);
        }
    }

    private static void drawNegativeHead(
            PoseStack poseStack,
            VertexConsumer metal,
            VertexConsumer lamp,
            int packedLight,
            int lampLight,
            int packedOverlay
    ) {
        poseStack.pushPose();
        poseStack.translate(7.0 / 16.0, 60.0 / 16.0, 7.0 / 16.0);
        poseStack.mulPose(Axis.XP.rotationDegrees(20.0F));
        box(metal, poseStack.last(), 0, 0, -1.0, 2.0 / 16.0, 2.0 / 16.0, 1.0, packedLight, packedOverlay);
        poseStack.popPose();

        boxPixels(metal, poseStack.last(), 7, 65.35, -23.2, 2, 2, 16, packedLight, packedOverlay);
        boxPixels(metal, poseStack.last(), 5, 64.35, -23.2, 1, 1, 14, packedLight, packedOverlay);
        boxPixels(metal, poseStack.last(), 10, 64.35, -23.2, 1, 1, 14, packedLight, packedOverlay);
        boxPixels(metal, poseStack.last(), 6, 64.35, -23.2, 4, 1, 1, packedLight, packedOverlay);
        boxPixels(metal, poseStack.last(), 6, 64.35, -10.2, 4, 1, 1, packedLight, packedOverlay);
        boxPixels(metal, poseStack.last(), 6, 65.34, -23.2, 4, 0.10, 14, packedLight, packedOverlay);
        boxPixels(lamp, poseStack.last(), 7, 64.83, -22.2, 2, 0.50, 12, lampLight, OverlayTexture.NO_OVERLAY);
    }

    private static void boxPixels(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            double x,
            double y,
            double z,
            double width,
            double height,
            double depth,
            int packedLight,
            int packedOverlay
    ) {
        box(
                consumer,
                pose,
                x / 16.0,
                y / 16.0,
                z / 16.0,
                width / 16.0,
                height / 16.0,
                depth / 16.0,
                packedLight,
                packedOverlay
        );
    }

    private static void box(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            double x,
            double y,
            double z,
            double width,
            double height,
            double depth,
            int packedLight,
            int packedOverlay
    ) {
        float x0 = (float) Math.min(x, x + width);
        float x1 = (float) Math.max(x, x + width);
        float y0 = (float) Math.min(y, y + height);
        float y1 = (float) Math.max(y, y + height);
        float z0 = (float) Math.min(z, z + depth);
        float z1 = (float) Math.max(z, z + depth);

        // North (-Z)
        quad(consumer, pose, x1, y0, z0, x0, y0, z0, x0, y1, z0, x1, y1, z0, 0, 0, -1, packedLight, packedOverlay);
        // South (+Z)
        quad(consumer, pose, x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1, 0, 0, 1, packedLight, packedOverlay);
        // West (-X)
        quad(consumer, pose, x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0, -1, 0, 0, packedLight, packedOverlay);
        // East (+X)
        quad(consumer, pose, x1, y0, z1, x1, y0, z0, x1, y1, z0, x1, y1, z1, 1, 0, 0, packedLight, packedOverlay);
        // Down (-Y)
        quad(consumer, pose, x0, y0, z1, x0, y0, z0, x1, y0, z0, x1, y0, z1, 0, -1, 0, packedLight, packedOverlay);
        // Up (+Y)
        quad(consumer, pose, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0, 0, 1, 0, packedLight, packedOverlay);
    }

    private static void quad(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            float nx, float ny, float nz,
            int packedLight,
            int packedOverlay
    ) {
        vertex(consumer, pose, x1, y1, z1, 1, 1, nx, ny, nz, packedLight, packedOverlay);
        vertex(consumer, pose, x2, y2, z2, 0, 1, nx, ny, nz, packedLight, packedOverlay);
        vertex(consumer, pose, x3, y3, z3, 0, 0, nx, ny, nz, packedLight, packedOverlay);
        vertex(consumer, pose, x4, y4, z4, 1, 0, nx, ny, nz, packedLight, packedOverlay);
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x, float y, float z,
            float u, float v,
            float nx, float ny, float nz,
            int packedLight,
            int packedOverlay
    ) {
        consumer.addVertex(pose.pose(), x, y, z)
                .setColor(-1)
                .setUv(u, v)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(pose, nx, ny, nz);
    }

    @Override
    public AABB getRenderBoundingBox(StreetLightBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(3.5D, 0.5D, 3.5D).expandTowards(0.0D, 5.0D, 0.0D);
    }

    @Override
    public int getViewDistance() {
        return 512;
    }
}
