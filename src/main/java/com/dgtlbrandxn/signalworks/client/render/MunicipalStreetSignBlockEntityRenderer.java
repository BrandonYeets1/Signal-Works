package com.dgtlbrandxn.signalworks.client.render;

import com.dgtlbrandxn.signalworks.TrafficControl;
import com.dgtlbrandxn.signalworks.block.MunicipalSignFont;
import com.dgtlbrandxn.signalworks.block.MunicipalSignShape;
import com.dgtlbrandxn.signalworks.block.MunicipalSignStyle;
import com.dgtlbrandxn.signalworks.block.MunicipalTextSize;
import com.dgtlbrandxn.signalworks.block.MunicipalStreetSignBlock;
import com.dgtlbrandxn.signalworks.block.RotatablePoleBlock;
import com.dgtlbrandxn.signalworks.block.SignalArmBlock;
import com.dgtlbrandxn.signalworks.blockentity.MunicipalStreetSignBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

/** Renders post-mounted crossing blades or an automatic mast-arm hanging blade. */
public final class MunicipalStreetSignBlockEntityRenderer
        implements BlockEntityRenderer<MunicipalStreetSignBlockEntity> {
    private static final ResourceLocation GENERIC = ResourceLocation.fromNamespaceAndPath(
            TrafficControl.MOD_ID, "textures/block/generic.png"
    );

    public MunicipalStreetSignBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            MunicipalStreetSignBlockEntity sign,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        BlockState state = sign.getBlockState();
        if (!(state.getBlock() instanceof MunicipalStreetSignBlock)
                || !state.hasProperty(MunicipalStreetSignBlock.ROTATION)) {
            return;
        }

        VertexConsumer bodyConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(GENERIC));
        VertexConsumer textConsumer = bufferSource.getBuffer(RenderType.entityCutout(GENERIC));
        HangingMount hanging = findHangingMount(sign);
        if (hanging != null) {
            renderHanging(sign, hanging, poseStack, bodyConsumer, textConsumer, packedLight, packedOverlay);
        } else {
            renderPostAssembly(sign, state, poseStack, bodyConsumer, textConsumer, packedLight, packedOverlay);
        }
    }

    private void renderPostAssembly(
            MunicipalStreetSignBlockEntity sign,
            BlockState state,
            PoseStack poseStack,
            VertexConsumer consumer,
            VertexConsumer textConsumer,
            int packedLight,
            int packedOverlay
    ) {
        MunicipalSignStyle style = sign.style();
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.getValue(MunicipalStreetSignBlock.ROTATION) * -22.5F));
        poseStack.translate(-0.5D, 0.0D, -0.5D);

        box(consumer, poseStack.last(), 0.4375F, 0.0F, 0.4375F, 0.125F, 0.72F, 0.125F,
                0xFF4B5157, packedLight, packedOverlay);
        box(consumer, poseStack.last(), 0.375F, 0.68F, 0.375F, 0.25F, 0.12F, 0.25F,
                0xFF626970, packedLight, packedOverlay);

        bladeLayer(consumer, poseStack.last(), sign.shape(), true,
                -0.44F, 0.73F, 0.455F, 1.88F, 0.285F, 0.09F,
                0xFF000000 | style.borderColor(), packedLight, packedOverlay);
        bladeLayer(consumer, poseStack.last(), sign.shape(), true,
                -0.415F, 0.755F, 0.447F, 1.83F, 0.235F, 0.106F,
                0xFF000000 | style.backgroundColor(), packedLight, packedOverlay);

        bladeLayer(consumer, poseStack.last(), sign.shape(), false,
                0.455F, 1.03F, -0.44F, 0.09F, 0.285F, 1.88F,
                0xFF000000 | style.borderColor(), packedLight, packedOverlay);
        bladeLayer(consumer, poseStack.last(), sign.shape(), false,
                0.447F, 1.055F, -0.415F, 0.106F, 0.235F, 1.83F,
                0xFF000000 | style.backgroundColor(), packedLight, packedOverlay);

        int textLight = sign.reflective() || sign.backlit() ? LightTexture.FULL_BRIGHT : packedLight;
        renderBladeText(textConsumer, sign.primaryStreet(), sign.district(), sign.blockNumber(),
                style.textColor(), sign.fontProfile(), sign.textSize(), poseStack, textLight,
                0.5F, 0.935F, 0.438F, 0.0F, false);
        if (sign.doubleSided()) {
            renderBladeText(textConsumer, sign.primaryStreet(), sign.district(), sign.blockNumber(),
                    style.textColor(), sign.fontProfile(), sign.textSize(), poseStack, textLight,
                    0.5F, 0.935F, 0.562F, 180.0F, false);
        }

        renderBladeText(textConsumer, sign.crossStreet(), sign.district(), "",
                style.textColor(), sign.fontProfile(), sign.textSize(), poseStack, textLight,
                0.438F, 1.235F, 0.5F, 90.0F, true);
        if (sign.doubleSided()) {
            renderBladeText(textConsumer, sign.crossStreet(), sign.district(), "",
                    style.textColor(), sign.fontProfile(), sign.textSize(), poseStack, textLight,
                    0.562F, 1.235F, 0.5F, -90.0F, true);
        }
        poseStack.popPose();
    }

    private void renderHanging(
            MunicipalStreetSignBlockEntity sign,
            HangingMount mount,
            PoseStack poseStack,
            VertexConsumer consumer,
            VertexConsumer textConsumer,
            int packedLight,
            int packedOverlay
    ) {
        MunicipalSignStyle style = sign.style();
        int faceLight = sign.backlit() ? LightTexture.FULL_BRIGHT : packedLight;
        int textLight = sign.backlit() || sign.reflective() ? LightTexture.FULL_BRIGHT : packedLight;
        float depth = sign.backlit() ? 0.19F : 0.055F;
        float panelHeight = sign.backlit() ? 0.36F : 0.31F;
        float panelWidth = 1.84F;
        float mastCenter = mount.heightBlocks() + 0.5F;
        float mastUnderside = mastCenter - mount.widthPixels() / 32.0F;
        float clampBottom = mastUnderside - 0.035F;
        float panelTop = mastUnderside - (sign.backlit() ? 0.14F : 0.10F);
        float panelBottom = panelTop - panelHeight;
        float outerDepth = depth;
        float innerDepth = Math.max(0.018F, depth - 0.025F);

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        if (mount.axis() == Direction.Axis.Z) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        }

        int housing = sign.backlit() ? mount.color() : (0xFF000000 | style.borderColor());
        box(consumer, poseStack.last(), -panelWidth / 2.0F, panelBottom, -outerDepth / 2.0F,
                panelWidth, panelHeight, outerDepth, housing, packedLight, packedOverlay);
        bladeLayer(consumer, poseStack.last(), sign.shape(), true,
                -panelWidth / 2.0F + 0.025F, panelBottom + 0.025F, -innerDepth / 2.0F,
                panelWidth - 0.05F, panelHeight - 0.05F, innerDepth,
                0xFF000000 | style.backgroundColor(), faceLight, packedOverlay);

        float strapWidth = sign.backlit() ? 0.045F : 0.026F;
        for (float hangerX : new float[]{-0.55F, 0.55F}) {
            box(consumer, poseStack.last(), hangerX - strapWidth / 2.0F,
                    panelTop, -strapWidth / 2.0F,
                    strapWidth, clampBottom - panelTop, strapWidth,
                    mount.color(), packedLight, packedOverlay);
            box(consumer, poseStack.last(), hangerX - 0.075F, clampBottom, -0.075F,
                    0.15F, mastUnderside - clampBottom, 0.15F,
                    mount.color(), packedLight, packedOverlay);
        }

        renderHangingFace(textConsumer, sign, poseStack, textLight,
                panelBottom, panelHeight, -outerDepth / 2.0F - 0.004F, 0.0F);
        if (sign.doubleSided()) {
            renderHangingFace(textConsumer, sign, poseStack, textLight,
                    panelBottom, panelHeight, outerDepth / 2.0F + 0.004F, 180.0F);
        }
        poseStack.popPose();
    }

    private void renderHangingFace(
            VertexConsumer consumer,
            MunicipalStreetSignBlockEntity sign,
            PoseStack poseStack,
            int packedLight,
            float panelBottom,
            float panelHeight,
            float z,
            float rotationY
    ) {
        String footer = sign.district();
        if (!sign.blockNumber().isBlank()) {
            footer = footer.isBlank() ? sign.blockNumber() : footer + "  " + sign.blockNumber();
        }
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));
        poseStack.scale(-1.0F, 1.0F, 1.0F);
        float mainY = panelBottom + panelHeight * 0.62F;
        float footerY = panelBottom + panelHeight * 0.27F;
        drawGeometryText(consumer, sign.primaryStreet(), 1.54F, mainY,
                sign.style().textColor(), sign.fontProfile(), sign.textSize(), 1.0F,
                poseStack.last(), packedLight);
        drawGeometryText(consumer, footer, 1.48F, footerY,
                sign.style().textColor(), sign.fontProfile(), sign.textSize(), 0.58F,
                poseStack.last(), packedLight);
        poseStack.popPose();
    }

    private void renderBladeText(
            VertexConsumer consumer,
            String street,
            String district,
            String block,
            int color,
            MunicipalSignFont fontProfile,
            MunicipalTextSize textSize,
            PoseStack poseStack,
            int packedLight,
            float x,
            float y,
            float z,
            float rotationY,
            boolean crossBlade
    ) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));
        poseStack.scale(-1.0F, 1.0F, 1.0F);
        drawGeometryText(consumer, street, crossBlade ? 1.38F : 1.44F, 0.025F,
                color, fontProfile, textSize, 0.88F,
                poseStack.last(), packedLight);

        String footer = district == null ? "" : district;
        if (block != null && !block.isBlank()) {
            footer = footer.isBlank() ? block : footer + "  " + block;
        }
        drawGeometryText(consumer, footer, crossBlade ? 1.30F : 1.36F, -0.072F,
                color, fontProfile, textSize, 0.46F, poseStack.last(), packedLight);
        poseStack.popPose();
    }

    /** Draws lettering as colored geometry so it survives shader and font render-layer changes. */
    private static void drawGeometryText(
            VertexConsumer consumer,
            String text,
            float maxWidth,
            float centerY,
            int color,
            MunicipalSignFont fontProfile,
            MunicipalTextSize textSize,
            float lineMultiplier,
            PoseStack.Pose pose,
            int packedLight
    ) {
        if (text == null || text.isBlank()) {
            return;
        }
        int units = -1;
        for (int index = 0; index < text.length(); index++) {
            units += MunicipalGlyphs.advance(text.charAt(index));
        }
        if (units <= 0) {
            return;
        }

        float basePixel = 0.0175F * textSize.scale() * lineMultiplier;
        float pixelWidth = basePixel * fontProfile.widthScale();
        float naturalWidth = units * pixelWidth;
        float fit = Math.min(1.0F, maxWidth / Math.max(0.001F, naturalWidth));
        float px = pixelWidth * fit;
        float py = basePixel * fit;
        float totalWidth = units * px;
        float cursor = -totalWidth / 2.0F;
        int argb = 0xFF000000 | color;

        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);
            for (int row = 0; row < 7; row++) {
                int bits = MunicipalGlyphs.row(character, row);
                for (int column = 0; column < 5; column++) {
                    if ((bits & (1 << (4 - column))) == 0) {
                        continue;
                    }
                    float x0 = cursor + column * px;
                    float x1 = x0 + px * 0.82F;
                    float y1 = centerY + (3.5F - row) * py;
                    float y0 = y1 - py * 0.82F;
                    textQuad(consumer, pose, x0, y0, x1, y1, -0.003F, argb, packedLight);
                    if (fontProfile.bold()) {
                        textQuad(consumer, pose, x0 + px * 0.18F, y0, x1 + px * 0.18F, y1,
                                -0.0034F, argb, packedLight);
                    }
                }
            }
            cursor += MunicipalGlyphs.advance(character) * px;
        }
    }

    private static void textQuad(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x0,
            float y0,
            float x1,
            float y1,
            float z,
            int color,
            int packedLight
    ) {
        quad(consumer, pose,
                x0, y0, z,
                x0, y1, z,
                x1, y1, z,
                x1, y0, z,
                0, 0, -1, color, packedLight, OverlayTexture.NO_OVERLAY);
    }

    @Nullable
    private static HangingMount findHangingMount(MunicipalStreetSignBlockEntity sign) {
        Level level = sign.getLevel();
        if (level == null) {
            return null;
        }
        BlockPos pos = sign.getBlockPos();
        SignalArmBlock.ArmMount signalArm = SignalArmBlock.findArmAbove(level, pos);
        if (signalArm != null) {
            Direction.Axis axis = SignalArmBlock.rightDirection(signalArm.state()).getAxis();
            return new HangingMount(axis, signalArm.heightBlocks(), signalArm.armWidthPixels(), signalArm.color());
        }

        for (int height = 1; height <= 4; height++) {
            BlockState candidate = level.getBlockState(pos.above(height));
            if (candidate.getBlock() instanceof RotatablePoleBlock support && support.isMastArm()) {
                int color = candidate.hasProperty(RotatablePoleBlock.COLOR)
                        ? 0xFF000000 | candidate.getValue(RotatablePoleBlock.COLOR).rgb()
                        : 0xFFB9C0C5;
                float widthPixels = (float) support.widthPixels();
                return new HangingMount(candidate.getValue(RotatablePoleBlock.FACING).getAxis(), height, widthPixels, color);
            }
        }
        return null;
    }

    private record HangingMount(Direction.Axis axis, int heightBlocks, float widthPixels, int color) {
    }

    private static void bladeLayer(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            MunicipalSignShape shape,
            boolean alongX,
            float x,
            float y,
            float z,
            float width,
            float height,
            float depth,
            int color,
            int packedLight,
            int packedOverlay
    ) {
        switch (shape) {
            case RECTANGLE -> box(consumer, pose, x, y, z, width, height, depth,
                    color, packedLight, packedOverlay);
            case ROUNDED -> {
                float endCut = alongX ? Math.min(0.055F, width * 0.08F) : Math.min(0.055F, depth * 0.08F);
                float verticalCut = Math.min(0.055F, height * 0.22F);
                if (alongX) {
                    box(consumer, pose, x + endCut, y, z, width - endCut * 2.0F, height, depth,
                            color, packedLight, packedOverlay);
                    box(consumer, pose, x, y + verticalCut, z, width, height - verticalCut * 2.0F, depth,
                            color, packedLight, packedOverlay);
                } else {
                    box(consumer, pose, x, y, z + endCut, width, height, depth - endCut * 2.0F,
                            color, packedLight, packedOverlay);
                    box(consumer, pose, x, y + verticalCut, z, width, height - verticalCut * 2.0F, depth,
                            color, packedLight, packedOverlay);
                }
            }
            case CLIPPED -> {
                float endCut = alongX ? Math.min(0.085F, width * 0.12F) : Math.min(0.085F, depth * 0.12F);
                float band = height / 3.0F;
                if (alongX) {
                    box(consumer, pose, x + endCut, y, z, width - endCut * 2.0F, band, depth,
                            color, packedLight, packedOverlay);
                    box(consumer, pose, x, y + band, z, width, height - band * 2.0F, depth,
                            color, packedLight, packedOverlay);
                    box(consumer, pose, x + endCut, y + height - band, z, width - endCut * 2.0F, band, depth,
                            color, packedLight, packedOverlay);
                } else {
                    box(consumer, pose, x, y, z + endCut, width, band, depth - endCut * 2.0F,
                            color, packedLight, packedOverlay);
                    box(consumer, pose, x, y + band, z, width, height - band * 2.0F, depth,
                            color, packedLight, packedOverlay);
                    box(consumer, pose, x, y + height - band, z + endCut, width, band, depth - endCut * 2.0F,
                            color, packedLight, packedOverlay);
                }
            }
        }
    }

    private static void box(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x,
            float y,
            float z,
            float width,
            float height,
            float depth,
            int color,
            int packedLight,
            int packedOverlay
    ) {
        float x0 = Math.min(x, x + width);
        float x1 = Math.max(x, x + width);
        float y0 = Math.min(y, y + height);
        float y1 = Math.max(y, y + height);
        float z0 = Math.min(z, z + depth);
        float z1 = Math.max(z, z + depth);

        quad(consumer, pose, x0, y0, z0, x1, y0, z0, x1, y1, z0, x0, y1, z0,
                0, 0, -1, color, packedLight, packedOverlay);
        quad(consumer, pose, x1, y0, z1, x0, y0, z1, x0, y1, z1, x1, y1, z1,
                0, 0, 1, color, packedLight, packedOverlay);
        quad(consumer, pose, x0, y0, z1, x0, y0, z0, x0, y1, z0, x0, y1, z1,
                -1, 0, 0, color, packedLight, packedOverlay);
        quad(consumer, pose, x1, y0, z0, x1, y0, z1, x1, y1, z1, x1, y1, z0,
                1, 0, 0, color, packedLight, packedOverlay);
        quad(consumer, pose, x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1,
                0, 1, 0, color, packedLight, packedOverlay);
        quad(consumer, pose, x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0,
                0, -1, 0, color, packedLight, packedOverlay);
    }

    private static void quad(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            float nx, float ny, float nz,
            int color,
            int packedLight,
            int packedOverlay
    ) {
        vertex(consumer, pose, x1, y1, z1, 1, 1, nx, ny, nz, color, packedLight, packedOverlay);
        vertex(consumer, pose, x2, y2, z2, 0, 1, nx, ny, nz, color, packedLight, packedOverlay);
        vertex(consumer, pose, x3, y3, z3, 0, 0, nx, ny, nz, color, packedLight, packedOverlay);
        vertex(consumer, pose, x4, y4, z4, 1, 0, nx, ny, nz, color, packedLight, packedOverlay);
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x,
            float y,
            float z,
            float u,
            float v,
            float nx,
            float ny,
            float nz,
            int color,
            int packedLight,
            int packedOverlay
    ) {
        consumer.addVertex(pose.pose(), x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(pose, nx, ny, nz);
    }

    @Override
    public AABB getRenderBoundingBox(MunicipalStreetSignBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(2.5D, 5.0D, 2.5D);
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
