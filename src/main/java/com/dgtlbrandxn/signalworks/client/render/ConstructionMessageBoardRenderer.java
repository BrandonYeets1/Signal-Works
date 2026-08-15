package com.dgtlbrandxn.signalworks.client.render;

import com.dgtlbrandxn.signalworks.block.ConstructionMessageBoardBlock;
import com.dgtlbrandxn.signalworks.block.ConstructionMessageMode;
import com.dgtlbrandxn.signalworks.blockentity.ConstructionMessageBoardBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

/** Procedural amber LED display for the portable changeable-message sign. */
public final class ConstructionMessageBoardRenderer
        implements BlockEntityRenderer<ConstructionMessageBoardBlockEntity> {
    private static final ResourceLocation GENERIC = ResourceLocation.fromNamespaceAndPath(
            "minecraft", "textures/block/white_concrete.png"
    );
    private static final int AMBER_ON = 0xFFFFA915;
    private static final int AMBER_OFF = 0xFF5B3A08;

    public ConstructionMessageBoardRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            ConstructionMessageBoardBlockEntity board,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        BlockState state = board.getBlockState();
        if (!(state.getBlock() instanceof ConstructionMessageBoardBlock)
                || !state.getValue(ConstructionMessageBoardBlock.DEPLOYED)) {
            return;
        }

        int light = state.getValue(ConstructionMessageBoardBlock.LIT)
                ? LightTexture.FULL_BRIGHT : packedLight;
        int color = state.getValue(ConstructionMessageBoardBlock.LIT) ? AMBER_ON : AMBER_OFF;
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(GENERIC));

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotationFor(state.getValue(ConstructionMessageBoardBlock.FACING))));
        poseStack.translate(0.0D, 0.0D, -0.443D);
        // Glyph coordinates are authored in roadway-facing screen space. Do not mirror this layer;
        // the facing rotation already handles all four block orientations.

        if (board.mode() == ConstructionMessageMode.MESSAGE) {
            drawText(consumer, board.line1(), 1.52F, 2.13F, color, poseStack.last(), light);
            drawText(consumer, board.line2(), 1.52F, 1.85F, color, poseStack.last(), light);
            drawText(consumer, board.line3(), 1.52F, 1.57F, color, poseStack.last(), light);
        } else {
            drawProgram(consumer, board.mode(), color, poseStack.last(), light);
        }
        poseStack.popPose();
    }


    @Override
    public boolean shouldRenderOffScreen(ConstructionMessageBoardBlockEntity board) {
        return true;
    }

    private static float rotationFor(Direction facing) {
        return switch (facing) {
            case NORTH -> 0.0F;
            case EAST -> -90.0F;
            case SOUTH -> 180.0F;
            case WEST -> 90.0F;
            default -> 0.0F;
        };
    }

    private static void drawText(
            VertexConsumer consumer,
            String text,
            float maxWidth,
            float centerY,
            int color,
            PoseStack.Pose pose,
            int packedLight
    ) {
        if (text == null || text.isBlank()) {
            return;
        }
        int units = -1;
        for (int index = 0; index < text.length(); index++) {
            units += ConstructionGlyphs.advance(text.charAt(index));
        }
        if (units <= 0) {
            return;
        }
        float basePixel = 0.032F;
        float fit = Math.min(1.0F, maxWidth / Math.max(0.001F, units * basePixel));
        float pixel = basePixel * fit;
        float cursor = -(units * pixel) / 2.0F;
        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);
            for (int row = 0; row < 7; row++) {
                int bits = ConstructionGlyphs.row(character, row);
                for (int column = 0; column < 5; column++) {
                    if ((bits & (1 << (4 - column))) == 0) {
                        continue;
                    }
                    float x0 = cursor + column * pixel;
                    float x1 = x0 + pixel * 0.70F;
                    float y1 = centerY + (3.5F - row) * pixel;
                    float y0 = y1 - pixel * 0.70F;
                    led(consumer, pose, x0, y0, x1, y1, color, packedLight);
                }
            }
            cursor += ConstructionGlyphs.advance(character) * pixel;
        }
    }

    private static void drawProgram(
            VertexConsumer consumer,
            ConstructionMessageMode mode,
            int color,
            PoseStack.Pose pose,
            int packedLight
    ) {
        boolean visuallyLeft = mode == ConstructionMessageMode.LEFT_ARROW
                || mode == ConstructionMessageMode.MERGE_LEFT;
        boolean visuallyRight = mode == ConstructionMessageMode.RIGHT_ARROW
                || mode == ConstructionMessageMode.MERGE_RIGHT;
        if (mode == ConstructionMessageMode.CAUTION) {
            drawCaution(consumer, color, pose, packedLight);
            return;
        }
        drawArrow(consumer, visuallyLeft, visuallyRight,
                mode == ConstructionMessageMode.MERGE_LEFT || mode == ConstructionMessageMode.MERGE_RIGHT,
                color, pose, packedLight);
    }

    private static void drawArrow(
            VertexConsumer consumer,
            boolean left,
            boolean right,
            boolean merge,
            int color,
            PoseStack.Pose pose,
            int packedLight
    ) {
        float pixel = 0.075F;
        float centerY = 1.84F;
        for (int column = 3; column <= 16; column++) {
            dot(consumer, pose, (column - 10) * pixel, centerY, pixel, color, packedLight);
        }
        if (left) {
            for (int step = 0; step < 6; step++) {
                dot(consumer, pose, (-7 + step) * pixel, centerY + (5 - step) * pixel, pixel, color, packedLight);
                dot(consumer, pose, (-7 + step) * pixel, centerY - (5 - step) * pixel, pixel, color, packedLight);
            }
        }
        if (right) {
            for (int step = 0; step < 6; step++) {
                dot(consumer, pose, (7 - step) * pixel, centerY + (5 - step) * pixel, pixel, color, packedLight);
                dot(consumer, pose, (7 - step) * pixel, centerY - (5 - step) * pixel, pixel, color, packedLight);
            }
        }
        if (merge) {
            for (int row = 1; row <= 5; row++) {
                dot(consumer, pose, 0.0F, centerY + row * pixel, pixel, color, packedLight);
            }
        }
    }

    private static void drawCaution(
            VertexConsumer consumer,
            int color,
            PoseStack.Pose pose,
            int packedLight
    ) {
        float pixel = 0.07F;
        float centerY = 1.84F;
        for (int group = -1; group <= 1; group++) {
            float centerX = group * 0.48F;
            for (int row = -5; row <= 5; row++) {
                dot(consumer, pose, centerX, centerY + row * pixel, pixel, color, packedLight);
            }
            for (int column = -3; column <= 3; column++) {
                dot(consumer, pose, centerX + column * pixel, centerY, pixel, color, packedLight);
            }
        }
    }

    private static void dot(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float centerX,
            float centerY,
            float pixel,
            int color,
            int packedLight
    ) {
        float half = pixel * 0.34F;
        led(consumer, pose, centerX - half, centerY - half,
                centerX + half, centerY + half, color, packedLight);
    }

    private static void led(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x0,
            float y0,
            float x1,
            float y1,
            int color,
            int packedLight
    ) {
        vertex(consumer, pose, x0, y0, -0.003F, 0.0F, 1.0F, color, packedLight);
        vertex(consumer, pose, x0, y1, -0.003F, 0.0F, 0.0F, color, packedLight);
        vertex(consumer, pose, x1, y1, -0.003F, 1.0F, 0.0F, color, packedLight);
        vertex(consumer, pose, x1, y0, -0.003F, 1.0F, 1.0F, color, packedLight);
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x,
            float y,
            float z,
            float u,
            float v,
            int color,
            int packedLight
    ) {
        consumer.addVertex(pose.pose(), x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 0.0F, -1.0F);
    }
}
