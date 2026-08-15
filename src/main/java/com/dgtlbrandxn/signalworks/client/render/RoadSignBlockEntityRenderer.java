package com.dgtlbrandxn.signalworks.client.render;

import com.dgtlbrandxn.signalworks.TrafficControl;
import com.dgtlbrandxn.signalworks.block.RoadSignBlock;
import com.dgtlbrandxn.signalworks.blockentity.RoadSignBlockEntity;
import com.dgtlbrandxn.signalworks.catalog.RoadSignEntry;
import com.dgtlbrandxn.signalworks.catalog.RoadSignShape;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Renders a reusable galvanized post and the selected catalog sign face. */
public final class RoadSignBlockEntityRenderer implements BlockEntityRenderer<RoadSignBlockEntity> {
    private static final ResourceLocation METAL = ResourceLocation.fromNamespaceAndPath(
            TrafficControl.MOD_ID, "textures/block/generic.png");
    private static final ResourceLocation DEFAULT_BACK = ResourceLocation.fromNamespaceAndPath(
            TrafficControl.MOD_ID, "textures/block/prop_sign_back_01.png");

    /**
     * Square and diamond catalog signs follow the GTA-style prop convention supplied by the project:
     * one front polygon for the selected artwork and one rear polygon for the metal backing.
     */
    private static final float TEMPLATE_SIDE = 1.0F;
    private static final float TEMPLATE_GAP = 0.00625F;
    private static final float SQRT_TWO = 1.41421356F;

    public RoadSignBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            RoadSignBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof RoadSignBlock) || !state.hasProperty(RoadSignBlock.FACING)) {
            return;
        }
        RoadSignEntry entry = blockEntity.selectedEntry();
        RoadSignShape shape = entry.shape();
        boolean templateShape = shape == RoadSignShape.SQUARE || shape == RoadSignShape.DIAMOND;

        float width = templateShape ? TEMPLATE_SIDE : shape.width();
        float height = templateShape ? TEMPLATE_SIDE : shape.height();
        float projectedHeight = shape == RoadSignShape.DIAMOND ? TEMPLATE_SIDE * SQRT_TWO : height;
        float panelCenterY = 1.28F;
        float panelBottom = panelCenterY - projectedHeight / 2.0F;
        float depth = 0.055F;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.getValue(RoadSignBlock.FACING).toYRot()));

        VertexConsumer metal = bufferSource.getBuffer(RenderType.entityCutoutNoCull(METAL));
        box(metal, poseStack.last(), -0.055F, 0.0F, -0.055F,
                0.11F, Math.max(0.9F, panelBottom + 0.12F), 0.11F,
                0xFF7C858B, packedLight, packedOverlay);

        float lowerClampY;
        float upperClampY;
        if (templateShape) {
            float clampSpread = shape == RoadSignShape.DIAMOND ? 0.31F : 0.28F;
            lowerClampY = panelCenterY - clampSpread;
            upperClampY = panelCenterY + clampSpread;
        } else {
            lowerClampY = panelBottom + 0.10F;
            upperClampY = panelBottom + height - 0.155F;
        }
        box(metal, poseStack.last(), -0.18F, lowerClampY, -0.075F,
                0.36F, 0.055F, 0.15F, 0xFF90999E, packedLight, packedOverlay);
        box(metal, poseStack.last(), -0.18F, upperClampY, -0.075F,
                0.36F, 0.055F, 0.15F, 0xFF90999E, packedLight, packedOverlay);

        ResourceLocation frontTexture = RoadSignTextureManager.front(entry);
        ResourceLocation backTexture = entry.hasBackTexture()
                ? RoadSignTextureManager.back(entry)
                : DEFAULT_BACK;

        if (templateShape) {
            renderTwoPolygonTemplate(shape, poseStack, bufferSource, frontTexture, backTexture,
                    panelCenterY, packedLight);
        } else {
            plate(shape, metal, poseStack.last(), -width / 2.0F, panelCenterY - height / 2.0F,
                    -depth / 2.0F, width, height, depth, 0xFF9CA4A8, packedLight, packedOverlay);

            VertexConsumer front = bufferSource.getBuffer(RenderType.entityCutoutNoCull(frontTexture));
            texturedFace(front, poseStack.last(), -width / 2.0F, panelCenterY - height / 2.0F,
                    width, height, -depth / 2.0F - 0.002F, false, packedLight);

            VertexConsumer back = bufferSource.getBuffer(RenderType.entityCutoutNoCull(backTexture));
            texturedFace(back, poseStack.last(), -width / 2.0F, panelCenterY - height / 2.0F,
                    width, height, depth / 2.0F + 0.002F, true, packedLight);
        }
        poseStack.popPose();
    }

    private static void renderTwoPolygonTemplate(
            RoadSignShape shape,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            ResourceLocation frontTexture,
            ResourceLocation backTexture,
            float centerY,
            int packedLight
    ) {
        poseStack.pushPose();
        poseStack.translate(0.0D, centerY, 0.0D);
        if (shape == RoadSignShape.DIAMOND) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
        }

        float half = TEMPLATE_SIDE / 2.0F;
        float halfGap = TEMPLATE_GAP / 2.0F;

        VertexConsumer front = bufferSource.getBuffer(RenderType.entityCutoutNoCull(frontTexture));
        texturedFace(front, poseStack.last(), -half, -half,
                TEMPLATE_SIDE, TEMPLATE_SIDE, -halfGap, false, packedLight);

        VertexConsumer back = bufferSource.getBuffer(RenderType.entityCutoutNoCull(backTexture));
        texturedFace(back, poseStack.last(), -half, -half,
                TEMPLATE_SIDE, TEMPLATE_SIDE, halfGap, true, packedLight);
        poseStack.popPose();
    }

    private static void plate(
            RoadSignShape shape,
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x,
            float y,
            float z,
            float width,
            float height,
            float depth,
            int color,
            int light,
            int overlay
    ) {
        switch (shape) {
            case SQUARE, DIAMOND, RECTANGLE -> box(consumer, pose, x, y, z, width, height, depth,
                    color, light, overlay);
            case OTHER -> {
                float cut = height * 0.12F;
                box(consumer, pose, x + cut, y, z, width - cut * 2.0F, height, depth,
                        color, light, overlay);
                box(consumer, pose, x, y + cut, z, width, height - cut * 2.0F, depth,
                        color, light, overlay);
            }
            case CIRCLE -> bandedPlate(consumer, pose, x, y, z, width, height, depth,
                    color, light, overlay, new float[]{0.42F, 0.72F, 0.90F, 1.00F, 0.90F, 0.72F, 0.42F});
            case TRIANGLE -> bandedPlate(consumer, pose, x, y, z, width, height, depth,
                    color, light, overlay, new float[]{0.12F, 0.28F, 0.44F, 0.60F, 0.74F, 0.88F, 1.00F});
        }
    }

    private static void bandedPlate(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x,
            float y,
            float z,
            float width,
            float height,
            float depth,
            int color,
            int light,
            int overlay,
            float[] widths
    ) {
        float bandHeight = height / widths.length;
        for (int row = 0; row < widths.length; row++) {
            float bandWidth = width * widths[row];
            box(consumer, pose, x + (width - bandWidth) / 2.0F,
                    y + row * bandHeight, z, bandWidth, bandHeight + 0.002F, depth,
                    color, light, overlay);
        }
    }

    private static void texturedFace(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x,
            float y,
            float width,
            float height,
            float z,
            boolean reverse,
            int light
    ) {
        if (reverse) {
            vertex(consumer, pose, x + width, y, z, 1, 1, 0, 0, 1, light);
            vertex(consumer, pose, x, y, z, 0, 1, 0, 0, 1, light);
            vertex(consumer, pose, x, y + height, z, 0, 0, 0, 0, 1, light);
            vertex(consumer, pose, x + width, y + height, z, 1, 0, 0, 0, 1, light);
        } else {
            vertex(consumer, pose, x, y, z, 1, 1, 0, 0, -1, light);
            vertex(consumer, pose, x, y + height, z, 1, 0, 0, 0, -1, light);
            vertex(consumer, pose, x + width, y + height, z, 0, 0, 0, 0, -1, light);
            vertex(consumer, pose, x + width, y, z, 0, 1, 0, 0, -1, light);
        }
    }

    private static void box(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x, float y, float z,
            float width, float height, float depth,
            int color, int light, int overlay
    ) {
        float x0 = Math.min(x, x + width);
        float x1 = Math.max(x, x + width);
        float y0 = Math.min(y, y + height);
        float y1 = Math.max(y, y + height);
        float z0 = Math.min(z, z + depth);
        float z1 = Math.max(z, z + depth);
        quad(consumer, pose, x0,y0,z0, x1,y0,z0, x1,y1,z0, x0,y1,z0, 0,0,-1,color,light,overlay);
        quad(consumer, pose, x1,y0,z1, x0,y0,z1, x0,y1,z1, x1,y1,z1, 0,0,1,color,light,overlay);
        quad(consumer, pose, x0,y0,z1, x0,y0,z0, x0,y1,z0, x0,y1,z1, -1,0,0,color,light,overlay);
        quad(consumer, pose, x1,y0,z0, x1,y0,z1, x1,y1,z1, x1,y1,z0, 1,0,0,color,light,overlay);
        quad(consumer, pose, x0,y1,z0, x1,y1,z0, x1,y1,z1, x0,y1,z1, 0,1,0,color,light,overlay);
        quad(consumer, pose, x0,y0,z1, x1,y0,z1, x1,y0,z0, x0,y0,z0, 0,-1,0,color,light,overlay);
    }

    private static void quad(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x1,float y1,float z1, float x2,float y2,float z2,
            float x3,float y3,float z3, float x4,float y4,float z4,
            float nx,float ny,float nz, int color,int light,int overlay
    ) {
        vertex(consumer, pose, x1,y1,z1, 1,1,nx,ny,nz,color,light,overlay);
        vertex(consumer, pose, x2,y2,z2, 0,1,nx,ny,nz,color,light,overlay);
        vertex(consumer, pose, x3,y3,z3, 0,0,nx,ny,nz,color,light,overlay);
        vertex(consumer, pose, x4,y4,z4, 1,0,nx,ny,nz,color,light,overlay);
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x,float y,float z,float u,float v,float nx,float ny,float nz,int light
    ) {
        vertex(consumer, pose, x,y,z,u,v,nx,ny,nz,0xFFFFFFFF,light,OverlayTexture.NO_OVERLAY);
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x,float y,float z,float u,float v,float nx,float ny,float nz,
            int color,int light,int overlay
    ) {
        consumer.addVertex(pose.pose(), x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }

    @Override
    public AABB getRenderBoundingBox(RoadSignBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(1.5D, 2.5D, 1.5D);
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
