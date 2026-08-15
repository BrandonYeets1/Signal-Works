package com.dgtlbrandxn.signalworks.client.render;

import com.dgtlbrandxn.signalworks.TrafficControl;
import com.dgtlbrandxn.signalworks.block.TrafficLight1Block;
import com.dgtlbrandxn.signalworks.block.TrafficLight2Block;
import com.dgtlbrandxn.signalworks.block.TrafficLight4Block;
import com.dgtlbrandxn.signalworks.block.TrafficLight5Block;
import com.dgtlbrandxn.signalworks.block.TrafficLightBlock;
import com.dgtlbrandxn.signalworks.block.TrafficLightDoghouseBlock;
import com.dgtlbrandxn.signalworks.block.DedicatedTurnSignalBlock;
import com.dgtlbrandxn.signalworks.block.RotatablePoleBlock;
import com.dgtlbrandxn.signalworks.block.SignalArmBlock;
import com.dgtlbrandxn.signalworks.block.SignalFlasherBlock;
import com.dgtlbrandxn.signalworks.block.SignalLampTechnology;
import com.dgtlbrandxn.signalworks.block.PedestrianSignalStyle;
import com.dgtlbrandxn.signalworks.block.RampMeterSignalBlock;
import com.dgtlbrandxn.signalworks.block.SignalMount;
import com.dgtlbrandxn.signalworks.block.SignalBackplateStyle;
import com.dgtlbrandxn.signalworks.block.SignalVisorStyle;
import com.dgtlbrandxn.signalworks.block.SpecialThreeSectionSignalBlock;
import com.dgtlbrandxn.signalworks.blockentity.TrafficLightBlockEntity;
import com.dgtlbrandxn.signalworks.util.TrafficLightBulbType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Draws signal bodies, live bulbs, conditional support hardware and a soft full-bright glow layer. */
public final class TrafficLightBlockEntityRenderer implements BlockEntityRenderer<TrafficLightBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    // Los Angeles-style pedestrian housing: two broad square indication windows
    // inside a compact black clamshell case rather than two traffic-signal lenses.
    private static final float[] TWO_BULB_X = {3.6F / 16.0F, 3.6F / 16.0F};
    private static final float[] TWO_BULB_Y = {8.35F / 16.0F, 2.75F / 16.0F};
    private static final float[] SINGLE_BULB_X = {4.7F / 16.0F};
    private static final float[] SINGLE_BULB_Y = {7.3F / 16.0F};
    private static final float[] RAMP_BULB_X = {5.2F / 16.0F, 5.2F / 16.0F};
    private static final float[] RAMP_BULB_Y = {9.0F / 16.0F, 2.5F / 16.0F};
    private static final float[] FLASHER_BULB_X = {5.2F / 16.0F};
    private static final float[] FLASHER_BULB_Y = {5.2F / 16.0F};
    private static final float[] THREE_BULB_X = {5.2F / 16.0F, 5.2F / 16.0F, 5.2F / 16.0F};
    private static final float[] THREE_BULB_Y = {9.0F / 16.0F, 2.5F / 16.0F, -4.0F / 16.0F};
    private static final float[] FOUR_BULB_X = {5.2F / 16.0F, 5.2F / 16.0F, 5.2F / 16.0F, 5.2F / 16.0F};
    private static final float[] FOUR_BULB_Y = {22.0F / 16.0F, 15.5F / 16.0F, 9.0F / 16.0F, 2.5F / 16.0F};
    private static final float[] FIVE_BULB_X = {5.2F / 16.0F, 5.2F / 16.0F, 5.2F / 16.0F, 5.2F / 16.0F, 5.2F / 16.0F};
    private static final float[] FIVE_BULB_Y = {22.0F / 16.0F, 15.5F / 16.0F, 9.0F / 16.0F, 2.5F / 16.0F, -4.0F / 16.0F};

    // Doghouse through indications are on the viewer's right; protected-left arrows are on the viewer's left.
    private static final float[] DOGHOUSE_BULB_X = {5.2F / 16.0F, 11.2F / 16.0F, 11.2F / 16.0F, -0.8F / 16.0F, -0.8F / 16.0F};
    private static final float[] DOGHOUSE_BULB_Y = {11.0F / 16.0F, 2.5F / 16.0F, -4.0F / 16.0F, 2.5F / 16.0F, -4.0F / 16.0F};

    private static final float BULB_WIDTH = 5.6F / 16.0F;
    private static final float SINGLE_BULB_WIDTH = 6.6F / 16.0F;
    private static final float SINGLE_BULB_HEIGHT = 4.0F / 16.0F;
    private static final float BULB_HEIGHT = 5.5F / 16.0F;
    private static final float PED_BULB_WIDTH = 8.8F / 16.0F;
    private static final float PED_BULB_HEIGHT = 4.85F / 16.0F;
    private static final float SIDE_FLUSH_SHIFT_PIXELS = 8.0F;
    private static final float FLASHER_BULB_Z = 10.105F / 16.0F;
    private static final float STANDARD_BULB_Z = 10.105F / 16.0F;
    private static final float SINGLE_BULB_Z = 10.205F / 16.0F;
    private static final float RAMP_BULB_Z = 10.205F / 16.0F;
    private static final float FIVE_BULB_Z = 10.305F / 16.0F;
    private static final float DOGHOUSE_BULB_Z = 10.305F / 16.0F;
    private static final float PED_BULB_Z = 11.60F / 16.0F;
    private static final float GLOW_PADDING = 1.75F / 16.0F;
    private static final float SOFT_GLOW_PADDING = 3.25F / 16.0F;

    private static final ResourceLocation BLACK_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TrafficControl.MOD_ID, "textures/block/black.png"
    );
    private static final ResourceLocation GLOW_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TrafficControl.MOD_ID, "textures/block/signal_glow.png"
    );
    private static final ResourceLocation LED_GRID_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TrafficControl.MOD_ID, "textures/block/signal_led_grid.png"
    );
    private static final ResourceLocation HALOGEN_HOTSPOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TrafficControl.MOD_ID, "textures/block/signal_halogen_hotspot.png"
    );
    private static final ResourceLocation SUPPORT_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TrafficControl.MOD_ID, "textures/block/generic.png"
    );
    private static final ResourceLocation PIG_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/pig/pig.png");
    private static final ResourceLocation PED_WALK_WHITE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TrafficControl.MOD_ID, "textures/block/ped_walk_white.png"
    );
    private static final ResourceLocation PED_HAND_ORANGE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TrafficControl.MOD_ID, "textures/block/ped_hand_orange.png"
    );
    private static final ResourceLocation PED_WALK_TEXT_WHITE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TrafficControl.MOD_ID, "textures/block/ped_walk_text_white.png"
    );
    private static final ResourceLocation PED_DONT_WALK_TEXT_ORANGE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TrafficControl.MOD_ID, "textures/block/ped_dont_walk_text_orange.png"
    );

    public TrafficLightBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(
            TrafficLightBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        BlockState state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof TrafficLightBlock)
                && !(state.getBlock() instanceof TrafficLight1Block)
                && !(state.getBlock() instanceof TrafficLight4Block)
                && !(state.getBlock() instanceof TrafficLight5Block)
                && !(state.getBlock() instanceof TrafficLightDoghouseBlock)
                && !(state.getBlock() instanceof TrafficLight2Block)
                && !(state.getBlock() instanceof RampMeterSignalBlock)
                && !(state.getBlock() instanceof DedicatedTurnSignalBlock)
                && !(state.getBlock() instanceof SpecialThreeSectionSignalBlock)
                && !(state.getBlock() instanceof SignalFlasherBlock)) {
            return;
        }

        Level level = blockEntity.getLevel();
        MountSelection mountSelection = resolveMount(blockEntity, state);
        MastMount mastMount = mountSelection.overhead();
        int mastHeightBlocks = mastMount == null ? 0 : mastMount.heightBlocks();
        boolean mastMounted = mastMount != null;
        Direction sideSupportDirection = mountSelection.poleMount() != null
                ? mountSelection.poleMount().direction() : mountSelection.mastDirection();
        float bodyLiftPixels = mastMounted ? mastBodyLiftPixels(blockEntity, mastMount) : 0.0F;
        float bodyRearShiftPixels = sideSupportDirection == null ? 0.0F
                : SIDE_FLUSH_SHIFT_PIXELS * sideMountAlignment(
                        state.getValue(TrafficLightBlock.ROTATION), sideSupportDirection
                );

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.getValue(TrafficLightBlock.ROTATION) * -22.5F));
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        // Real signal heads are mounted close to their structure. Move side-mounted
        // heads toward the support and raise overhead heads toward the mast underside;
        // all lenses, visors and backplates share the same transform.
        poseStack.translate(0.0D, bodyLiftPixels / 16.0D, -bodyRearShiftPixels / 16.0D);

        if (mastMounted) {
            VertexConsumer supportConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(SUPPORT_TEXTURE));
            renderFlushMastMount(
                    blockEntity,
                    mastMount.widthPixels(),
                    mastHeightBlocks,
                    bodyLiftPixels,
                    supportConsumer,
                    poseStack.last(),
                    packedLight,
                    mastMount.color()
            );
        }

        renderBackplate(blockEntity, poseStack, bufferSource, packedLight);

        BakedModel bodyModel = blockRenderer.getBlockModel(state);
        VertexConsumer bodyConsumer = bufferSource.getBuffer(ItemBlockRenderTypes.getRenderType(state, false));
        blockRenderer.getModelRenderer().renderModel(
                poseStack.last(), bodyConsumer, state, bodyModel,
                1.0F, 1.0F, 1.0F, packedLight, packedOverlay
        );

        float[] bulbX;
        float[] bulbY;
        float bulbZ;
        if (blockEntity.isFlasher()) {
            bulbX = FLASHER_BULB_X;
            bulbY = FLASHER_BULB_Y;
            bulbZ = FLASHER_BULB_Z;
        } else if (blockEntity.isDoghouse()) {
            bulbX = DOGHOUSE_BULB_X;
            bulbY = DOGHOUSE_BULB_Y;
            bulbZ = DOGHOUSE_BULB_Z;
        } else if (blockEntity.isFourSection()) {
            bulbX = FOUR_BULB_X;
            bulbY = FOUR_BULB_Y;
            bulbZ = FIVE_BULB_Z;
        } else if (blockEntity.isFiveSection()) {
            bulbX = FIVE_BULB_X;
            bulbY = FIVE_BULB_Y;
            bulbZ = FIVE_BULB_Z;
        } else if (blockEntity.isSingleSection()) {
            bulbX = SINGLE_BULB_X;
            bulbY = SINGLE_BULB_Y;
            bulbZ = SINGLE_BULB_Z;
        } else if (blockEntity.isPedestrianSignal()) {
            bulbX = TWO_BULB_X;
            bulbY = TWO_BULB_Y;
            bulbZ = PED_BULB_Z;
        } else if (blockEntity.isRampMeterSignal()) {
            bulbX = RAMP_BULB_X;
            bulbY = RAMP_BULB_Y;
            bulbZ = RAMP_BULB_Z;
        } else {
            bulbX = THREE_BULB_X;
            bulbY = THREE_BULB_Y;
            bulbZ = STANDARD_BULB_Z;
        }

        if (!blockEntity.isPedestrianSignal() && blockEntity.visorStyle() == SignalVisorStyle.TUNNEL) {
            renderTunnelVisors(
                    blockEntity,
                    bulbX,
                    bulbY,
                    bulbZ,
                    poseStack,
                    bufferSource,
                    packedLight
            );
        }

        for (int slot = 0; slot < blockEntity.getBulbCount(); slot++) {
            boolean illuminated = blockEntity.isVisible(slot, partialTick);
            TrafficLightBulbType bulbType = blockEntity.getBulbType(slot);

            ResourceLocation texture;
            int faceLight;
            if (!illuminated || bulbType == null) {
                texture = BLACK_TEXTURE;
                faceLight = packedLight;
            } else {
                texture = illuminatedTexture(blockEntity, bulbType);
                faceLight = LightTexture.FULL_BRIGHT;
            }

            float minX = bulbX[slot];
            float minY = bulbY[slot];
            float bulbWidth = blockEntity.isSingleSection() ? SINGLE_BULB_WIDTH
                    : (blockEntity.isPedestrianSignal() ? PED_BULB_WIDTH : BULB_WIDTH);
            float bulbHeight = blockEntity.isSingleSection() ? SINGLE_BULB_HEIGHT
                    : (blockEntity.isPedestrianSignal() ? PED_BULB_HEIGHT : BULB_HEIGHT);
            float maxX = minX + bulbWidth;
            float maxY = minY + bulbHeight;

            VertexConsumer faceConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
            drawBulbFace(
                    faceConsumer, poseStack.last(), minX, minY, bulbZ, maxX, maxY,
                    faceLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF
            );

            if (illuminated && bulbType != null) {
                // Re-render the actual lens texture on an emissive layer. This
                // preserves arrow/hand shapes while giving shaders a stronger
                // bright source than the cutout face alone.
                SignalLampTechnology technology = blockEntity.lampTechnology();
                int lensTint = technology == SignalLampTechnology.HALOGEN_HPS
                        ? 0xD8FFF0D2 : (technology == SignalLampTechnology.LED ? 0xFFFFFFFF : 0xE8FFFFFF);
                float glowPadding = technology == SignalLampTechnology.LED
                        ? GLOW_PADDING * 0.72F : (technology == SignalLampTechnology.HALOGEN_HPS ? GLOW_PADDING * 1.30F : GLOW_PADDING);
                float softPadding = technology == SignalLampTechnology.LED
                        ? SOFT_GLOW_PADDING * 0.70F : (technology == SignalLampTechnology.HALOGEN_HPS ? SOFT_GLOW_PADDING * 1.35F : SOFT_GLOW_PADDING);

                VertexConsumer lensEmissive = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(texture));
                drawBulbFace(
                        lensEmissive, poseStack.last(), minX, minY, bulbZ + 0.0015F, maxX, maxY,
                        LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, lensTint
                );

                renderLampTechnologyOverlay(
                        technology, bulbType, poseStack.last(), bufferSource,
                        minX, minY, bulbZ + 0.0022F, maxX, maxY
                );

                VertexConsumer glowConsumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(GLOW_TEXTURE));
                drawBulbFace(
                        glowConsumer,
                        poseStack.last(),
                        minX - glowPadding,
                        minY - glowPadding,
                        bulbZ + 0.0025F,
                        maxX + glowPadding,
                        maxY + glowPadding,
                        LightTexture.FULL_BRIGHT,
                        OverlayTexture.NO_OVERLAY,
                        glowColor(blockEntity, bulbType)
                );
                drawBulbFace(
                        glowConsumer,
                        poseStack.last(),
                        minX - softPadding,
                        minY - softPadding,
                        bulbZ + 0.0035F,
                        maxX + softPadding,
                        maxY + softPadding,
                        LightTexture.FULL_BRIGHT,
                        OverlayTexture.NO_OVERLAY,
                        softGlowColor(blockEntity, bulbType)
                );
            }
        }

        poseStack.popPose();

        // Support hardware is rendered in world/block space so side brackets can
        // reach cardinally adjacent poles even when the signal head is rotated 45 degrees.
        renderSupportConnections(blockEntity, state, mountSelection, poseStack, bufferSource, packedLight);
    }


    private static void renderBackplate(
            TrafficLightBlockEntity blockEntity,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        SignalBackplateStyle style = blockEntity.backplateStyle();
        if (style == SignalBackplateStyle.NONE) {
            return;
        }

        float minX = 2.0F;
        float maxX = 14.0F;
        float minY = -6.0F;
        float maxY = 17.0F;
        if (blockEntity.isDoghouse()) {
            minX = -2.0F;
            maxX = 18.0F;
            maxY = 18.0F;
        } else if (blockEntity.isFourSection() || blockEntity.isFiveSection()) {
            maxY = 31.0F;
        } else if (blockEntity.isPedestrianSignal() || blockEntity.isRampMeterSignal()) {
            minY = 0.0F;
        }

        int color = style == SignalBackplateStyle.YELLOW ? 0xFFFFD13B : 0xFF111414;
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(SUPPORT_TEXTURE));
        drawCuboidPixels(
                consumer,
                poseStack.last(),
                minX, minY, 6.10F,
                maxX, maxY, 6.85F,
                packedLight,
                color
        );
    }

    private static void renderTunnelVisors(
            TrafficLightBlockEntity blockEntity,
            float[] bulbX,
            float[] bulbY,
            float bulbZ,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(SUPPORT_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        int color = 0xFF0D0F10;
        float depthStart = bulbZ * 16.0F + 0.4F;
        float depthEnd = depthStart + 3.4F;

        for (int slot = 0; slot < blockEntity.getBulbCount(); slot++) {
            float minX = bulbX[slot] * 16.0F;
            float minY = bulbY[slot] * 16.0F;
            float maxX = minX + BULB_WIDTH * 16.0F;
            float maxY = minY + BULB_HEIGHT * 16.0F;

            drawCuboidPixels(consumer, pose,
                    minX - 0.9F, maxY - 0.15F, depthStart,
                    maxX + 0.9F, maxY + 0.9F, depthEnd,
                    packedLight, color);
            drawCuboidPixels(consumer, pose,
                    minX - 0.9F, minY, depthStart,
                    minX + 0.05F, maxY, depthEnd,
                    packedLight, color);
            drawCuboidPixels(consumer, pose,
                    maxX - 0.05F, minY, depthStart,
                    maxX + 0.9F, maxY, depthEnd,
                    packedLight, color);
        }
    }

    private static MountSelection resolveMount(TrafficLightBlockEntity blockEntity, BlockState state) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return MountSelection.NONE;
        }

        BlockPos pos = blockEntity.getBlockPos();
        int rotation = state.getValue(TrafficLightBlock.ROTATION);
        PoleArmMount poleMount = findPoleArmMount(level, pos, rotation);
        Direction mastDirection = findAdjacentMastDirection(level, pos, rotation);
        MastMount overhead = findOverheadMount(blockEntity);
        SignalMount requested = state.hasProperty(TrafficLightBlock.MOUNT)
                ? state.getValue(TrafficLightBlock.MOUNT)
                : SignalMount.AUTO;

        return switch (requested) {
            case TOP -> overhead == null ? MountSelection.NONE : new MountSelection(overhead, null, null);
            case SIDE -> {
                if (poleMount != null) {
                    yield new MountSelection(null, poleMount, null);
                }
                if (mastDirection != null) {
                    yield new MountSelection(null, null, mastDirection);
                }
                yield MountSelection.NONE;
            }
            case AUTO -> {
                if (poleMount != null) {
                    yield new MountSelection(null, poleMount, null);
                }
                if (mastDirection != null) {
                    yield new MountSelection(null, null, mastDirection);
                }
                yield overhead == null ? MountSelection.NONE : new MountSelection(overhead, null, null);
            }
        };
    }

    private static void renderSupportConnections(
            TrafficLightBlockEntity blockEntity,
            BlockState signalState,
            MountSelection mountSelection,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }

        BlockPos pos = blockEntity.getBlockPos();

        // A signal suspended from a mast above already has a drop-pipe assembly.
        // Do not also generate side/front standoffs toward the shared pole; those
        // extra beams overlap around multi-arm intersections and visually connect
        // neighboring signal heads to one another.
        if (mountSelection.overhead() != null) {
            return;
        }

        VertexConsumer supportConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(SUPPORT_TEXTURE));
        PoseStack.Pose pose = poseStack.last();

        // Signals placed directly on top of a modular pole receive a collar across the block seam.
        BlockState belowState = level.getBlockState(pos.below());
        if (isSignalPole(belowState)) {
            float poleWidth = supportWidthPixels(belowState);
            float collarHalf = poleWidth / 2.0F + 1.0F;
            drawCuboidPixels(supportConsumer, pose,
                    8.0F - collarHalf, -2.0F, 8.0F - collarHalf,
                    8.0F + collarHalf, 3.0F, 8.0F + collarHalf,
                    packedLight,
                    supportColor(belowState));
        }

        // Side-mounted signals connect to the best adjacent pole regardless of
        // whether the signal itself is cardinal, 22.5-degree, or 45-degree rotated.
        int rotation = signalState.getValue(TrafficLightBlock.ROTATION);
        PoleArmMount poleMount = mountSelection.poleMount();
        if (poleMount != null) {
            renderFlushSideMount(
                    blockEntity,
                    poleMount.direction(),
                    poleMount.distanceBlocks(),
                    poleMount.state(),
                    supportConsumer,
                    pose,
                    packedLight
            );
        }

        // A head placed in front of a mast segment uses the same twin-arm bracket
        // language as a pole-mounted head, but with a one-block support distance.
        Direction mastDirection = mountSelection.mastDirection();
        if (mastDirection != null) {
            BlockState mastState = level.getBlockState(pos.relative(mastDirection));
            renderFlushSideMount(
                    blockEntity,
                    mastDirection,
                    1,
                    mastState,
                    supportConsumer,
                    pose,
                    packedLight
            );
        }
    }

    /**
     * Draws a realistic US/Los-Angeles side mounting assembly: two compact arms,
     * a rear mounting spine, pole collars and a small terminal box. Signal bodies
     * are shifted toward this assembly by {@link #SIDE_FLUSH_SHIFT_PIXELS}.
     */
    private static void renderFlushSideMount(
            TrafficLightBlockEntity blockEntity,
            Direction direction,
            int distanceBlocks,
            BlockState supportState,
            VertexConsumer consumer,
            PoseStack.Pose pose,
            int packedLight
    ) {
        float poleWidth = supportWidthPixels(supportState);
        float poleHalf = poleWidth / 2.0F;
        float collarHalf = poleHalf + 1.0F;
        int color = supportColor(supportState);

        float headTop = signalHeadTopPixels(blockEntity);
        float headBottom = signalHeadBottomPixels(blockEntity);
        float upperY = headTop - 2.75F;
        float lowerY = headBottom + 3.0F;
        if (upperY - lowerY < 5.0F) {
            lowerY = upperY - 5.0F;
        }

        float targetPlane = direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 0.9F : 15.1F;
        float poleCenter = 8.0F + direction.getStepX() * distanceBlocks * 16.0F
                + direction.getStepZ() * distanceBlocks * 16.0F;
        float step = direction.getAxis() == Direction.Axis.X ? direction.getStepX() : direction.getStepZ();
        float poleSurface = poleCenter - step * poleHalf;

        // Rear galvanized spine that is almost flush with the signal housing.
        if (direction.getAxis() == Direction.Axis.Z) {
            drawCuboidPixels(consumer, pose,
                    5.75F, lowerY - 1.0F, targetPlane - 0.65F,
                    10.25F, upperY + 1.0F, targetPlane + 0.65F,
                    packedLight, color);
        } else {
            drawCuboidPixels(consumer, pose,
                    targetPlane - 0.65F, lowerY - 1.0F, 5.75F,
                    targetPlane + 0.65F, upperY + 1.0F, 10.25F,
                    packedLight, color);
        }

        drawSideArm(consumer, pose, direction, targetPlane, poleSurface, upperY, packedLight, color);
        drawSideArm(consumer, pose, direction, targetPlane, poleSurface, lowerY, packedLight, color);
        drawPoleCollar(consumer, pose, direction, poleCenter, upperY, collarHalf, packedLight, color);
        drawPoleCollar(consumer, pose, direction, poleCenter, lowerY, collarHalf, packedLight, color);

        // Compact weatherproof terminal box on the signal-facing side of the pole.
        float boxCenter = poleSurface - step * 1.9F;
        float boxBottom = Math.max(lowerY + 1.2F, Math.min(upperY - 5.7F, 5.0F));
        int boxColor = 0xFF60676A;
        if (direction.getAxis() == Direction.Axis.Z) {
            drawCuboidPixels(consumer, pose,
                    5.7F, boxBottom, boxCenter - 1.65F,
                    10.3F, boxBottom + 5.4F, boxCenter + 1.65F,
                    packedLight, boxColor);
        } else {
            drawCuboidPixels(consumer, pose,
                    boxCenter - 1.65F, boxBottom, 5.7F,
                    boxCenter + 1.65F, boxBottom + 5.4F, 10.3F,
                    packedLight, boxColor);
        }
    }

    private static void drawSideArm(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            Direction direction,
            float signalPlane,
            float supportSurface,
            float y,
            int packedLight,
            int color
    ) {
        float min = Math.min(signalPlane, supportSurface);
        float max = Math.max(signalPlane, supportSurface);
        if (direction.getAxis() == Direction.Axis.Z) {
            drawCuboidPixels(consumer, pose, 7.0F, y - 0.8F, min, 9.0F, y + 0.8F, max, packedLight, color);
        } else {
            drawCuboidPixels(consumer, pose, min, y - 0.8F, 7.0F, max, y + 0.8F, 9.0F, packedLight, color);
        }
    }

    private static void drawPoleCollar(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            Direction direction,
            float poleCenter,
            float y,
            float half,
            int packedLight,
            int color
    ) {
        if (direction.getAxis() == Direction.Axis.Z) {
            drawCuboidPixels(consumer, pose,
                    8.0F - half, y - 1.3F, poleCenter - half,
                    8.0F + half, y + 1.3F, poleCenter + half,
                    packedLight, color);
        } else {
            drawCuboidPixels(consumer, pose,
                    poleCenter - half, y - 1.3F, 8.0F - half,
                    poleCenter + half, y + 1.3F, 8.0F + half,
                    packedLight, color);
        }
    }

    private static float sideMountAlignment(int rotation, Direction supportDirection) {
        double angle = Math.toRadians(rotation * 22.5D);
        double rearX = Math.sin(angle);
        double rearZ = -Math.cos(angle);
        double alignment = supportDirection.getStepX() * rearX + supportDirection.getStepZ() * rearZ;
        return (float) Math.max(0.55D, Math.min(1.0D, alignment));
    }

    private static float mastBodyLiftPixels(TrafficLightBlockEntity blockEntity, MastMount mount) {
        float mastCenter = mount.heightBlocks() * 16.0F + 8.0F;
        float mastBottom = mastCenter - mount.widthPixels() / 2.0F;
        float openGap = mastBottom - signalHeadTopPixels(blockEntity);
        return Math.max(0.0F, Math.min(6.0F, openGap - 1.0F));
    }

    private static void renderFlushMastMount(
            TrafficLightBlockEntity blockEntity,
            float mastWidth,
            int mastHeightBlocks,
            float bodyLiftPixels,
            VertexConsumer consumer,
            PoseStack.Pose pose,
            int packedLight,
            int color
    ) {
        float headTop = signalHeadTopPixels(blockEntity);
        // The pose has already moved the signal upward, so express the stationary
        // world mast in the translated signal coordinate system.
        float mastCenterY = mastHeightBlocks * 16.0F + 8.0F - bodyLiftPixels;
        float mastBottomY = mastCenterY - mastWidth / 2.0F;

        // Compact swivel yoke seated directly into the top/rear of the housing.
        drawCuboidPixels(consumer, pose,
                5.4F, headTop - 1.8F, 7.15F,
                10.6F, headTop + 0.65F, 10.55F,
                packedLight, color);
        float swivelTop = Math.max(headTop + 0.65F, Math.min(headTop + 2.6F, mastBottomY));
        drawCuboidPixels(consumer, pose,
                6.15F, headTop + 0.35F, 6.15F,
                9.85F, swivelTop, 9.85F,
                packedLight, color);

        // Short galvanized drop nipple. With adjacent mast arms this is only a
        // few pixels tall; deliberate air gaps still receive a visible connector.
        float tubeBottom = Math.min(headTop + 2.1F, mastBottomY - 0.25F);
        if (mastBottomY > tubeBottom) {
            drawCuboidPixels(consumer, pose,
                    7.0F, tubeBottom, 7.0F,
                    9.0F, mastBottomY + 0.35F, 9.0F,
                    packedLight, color);
        }

        // Width-matched saddle and lower transition collar gripping the mast arm.
        float clampHalf = mastWidth / 2.0F + 1.0F;
        drawCuboidPixels(consumer, pose,
                8.0F - clampHalf, mastCenterY - 1.75F, 8.0F - clampHalf,
                8.0F + clampHalf, mastCenterY + 1.75F, 8.0F + clampHalf,
                packedLight, color);
        drawCuboidPixels(consumer, pose,
                6.4F, mastBottomY - 0.8F, 6.4F,
                9.6F, mastBottomY + 0.8F, 9.6F,
                packedLight, color);
    }

    /** Finds either a virtual slot on an adjustable arm or a legacy mast directly overhead. */
    private static MastMount findOverheadMount(TrafficLightBlockEntity blockEntity) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return null;
        }

        SignalArmBlock.ArmMount adjustableArm = SignalArmBlock.findArmAbove(level, blockEntity.getBlockPos());
        if (adjustableArm != null) {
            return new MastMount(
                    adjustableArm.heightBlocks(),
                    adjustableArm.armWidthPixels(),
                    adjustableArm.color()
            );
        }

        // Five-section heads are taller, but all heads may be suspended with one or
        // more air blocks between the housing and mast. Four blocks covers normal
        // US-style signal hangers without accidentally attaching to distant hardware.
        for (int height = 1; height <= 4; height++) {
            BlockState candidate = level.getBlockState(blockEntity.getBlockPos().above(height));
            if (isMastArm(candidate)) {
                return new MastMount(height, supportWidthPixels(candidate), supportColor(candidate));
            }
        }
        return null;
    }

    private record MastMount(int heightBlocks, float widthPixels, int color) {
    }

    private record PoleArmMount(Direction direction, int distanceBlocks, BlockState state) {
    }

    private record MountSelection(MastMount overhead, PoleArmMount poleMount, Direction mastDirection) {
        private static final MountSelection NONE = new MountSelection(null, null, null);
    }

    private static boolean isMastArm(BlockState state) {
        return state.getBlock() instanceof RotatablePoleBlock support && support.isMastArm();
    }

    private static boolean isSignalPole(BlockState state) {
        return state.getBlock() instanceof RotatablePoleBlock support && !support.isMastArm();
    }

    private static int supportColor(BlockState state) {
        if (state != null && state.hasProperty(RotatablePoleBlock.COLOR)) {
            return 0xFF000000 | state.getValue(RotatablePoleBlock.COLOR).rgb();
        }
        return 0xFFFFFFFF;
    }

    private static float supportWidthPixels(BlockState state) {
        if (state.getBlock() instanceof RotatablePoleBlock support) {
            return (float) support.widthPixels();
        }
        return 2.0F;
    }

    private static float signalHeadTopPixels(TrafficLightBlockEntity blockEntity) {
        if (blockEntity.isFourSection() || blockEntity.isFiveSection()) {
            return 29.5F;
        }
        if (blockEntity.isDoghouse()) {
            return 18.5F;
        }
        if (blockEntity.isPedestrianSignal() || blockEntity.isRampMeterSignal()) {
            return 16.0F;
        }
        return 16.5F;
    }

    private static float signalHeadBottomPixels(TrafficLightBlockEntity blockEntity) {
        if (blockEntity.isFourSection() || blockEntity.isPedestrianSignal() || blockEntity.isRampMeterSignal()) {
            return 0.5F;
        }
        return -5.0F;
    }

    private static Direction findAdjacentMastDirection(Level level, BlockPos pos, int rotation) {
        Direction bestDirection = null;
        double bestScore = -Double.MAX_VALUE;

        double angle = Math.toRadians(rotation * 22.5D);
        double rearX = Math.sin(angle);
        double rearZ = -Math.cos(angle);

        Direction[] horizontal = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        for (Direction direction : horizontal) {
            BlockState candidate = level.getBlockState(pos.relative(direction));
            if (!isMastArm(candidate)) {
                continue;
            }

            // A center-mounted signal sits in front of the mast, while the mast's
            // straight axis continues left/right behind the housing.
            Direction mastFacing = candidate.getValue(RotatablePoleBlock.FACING);
            if (mastFacing.getAxis() == direction.getAxis()) {
                continue;
            }

            double score = direction.getStepX() * rearX + direction.getStepZ() * rearZ;
            if (score > bestScore) {
                bestScore = score;
                bestDirection = direction;
            }
        }

        // Avoid attaching to a mast that is beside, rather than behind, the signal.
        return bestScore >= 0.5D ? bestDirection : null;
    }

    /**
     * Finds a vertical support behind the signal and treats every block of
     * horizontal distance as a deterministic arm slot. The resulting support
     * tube grows to the exact distance instead of requiring the signal to be
     * directly adjacent to the pole.
     */
    private static PoleArmMount findPoleArmMount(Level level, BlockPos pos, int rotation) {
        Direction bestDirection = null;
        BlockState bestState = null;
        int bestDistance = 0;
        double bestScore = -Double.MAX_VALUE;

        // Default signal front is +Z and the renderer rotates it by
        // -rotation * 22.5 degrees. Prefer supports behind the housing.
        double angle = Math.toRadians(rotation * 22.5D);
        double rearX = Math.sin(angle);
        double rearZ = -Math.cos(angle);

        Direction[] horizontal = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        for (Direction direction : horizontal) {
            double facingScore = direction.getStepX() * rearX + direction.getStepZ() * rearZ;
            if (facingScore < 0.45D) {
                continue;
            }

            for (int distance = 1; distance <= 4; distance++) {
                BlockPos candidatePos = pos.relative(direction, distance);
                BlockState candidate = level.getBlockState(candidatePos);
                if (!isSignalPole(candidate)) {
                    continue;
                }
                if (!armPathIsClear(level, pos, direction, distance)) {
                    continue;
                }

                // Direction alignment is more important than distance, but when
                // two poles are equally valid, use the nearer one.
                double score = facingScore * 10.0D - distance * 0.2D;
                if (score > bestScore) {
                    bestScore = score;
                    bestDirection = direction;
                    bestState = candidate;
                    bestDistance = distance;
                }
            }
        }

        return bestDirection == null ? null : new PoleArmMount(bestDirection, bestDistance, bestState);
    }

    private static boolean armPathIsClear(Level level, BlockPos signalPos, Direction direction, int distance) {
        for (int step = 1; step < distance; step++) {
            BlockState intermediate = level.getBlockState(signalPos.relative(direction, step));
            if (!intermediate.isAir()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(TrafficLightBlockEntity blockEntity) {
        MastMount mastMount = resolveMount(blockEntity, blockEntity.getBlockState()).overhead();
        double upwardReach = mastMount == null
                ? ((blockEntity.isFourSection() || blockEntity.isFiveSection()) ? 2.5D : 1.0D)
                : mastMount.heightBlocks() + 1.0D;

        return new AABB(blockEntity.getBlockPos())
                .inflate(5.0D)
                .expandTowards(0.0D, -1.0D, 0.0D)
                .expandTowards(0.0D, upwardReach, 0.0D);
    }

    @Override
    public int getViewDistance() {
        return 512;
    }

    private static ResourceLocation illuminatedTexture(
            TrafficLightBlockEntity blockEntity,
            TrafficLightBulbType bulbType
    ) {
        if (blockEntity.isPigAbove()) {
            return PIG_TEXTURE;
        }
        if (blockEntity.isPedestrianSignal()) {
            if (blockEntity.pedestrianSignalStyle() == PedestrianSignalStyle.US_CA) {
                if (bulbType == TrafficLightBulbType.CROSS) {
                    return PED_WALK_WHITE_TEXTURE;
                }
                if (bulbType == TrafficLightBulbType.DONT_CROSS) {
                    return PED_HAND_ORANGE_TEXTURE;
                }
            } else {
                if (bulbType == TrafficLightBulbType.CROSS) {
                    return PED_WALK_TEXT_WHITE_TEXTURE;
                }
                if (bulbType == TrafficLightBulbType.DONT_CROSS) {
                    return PED_DONT_WALK_TEXT_ORANGE_TEXTURE;
                }
            }
        }
        return bulbType.texture();
    }

    private static int glowColor(TrafficLightBlockEntity blockEntity, TrafficLightBulbType bulbType) {
        if (blockEntity.isPedestrianSignal()) {
            if (bulbType == TrafficLightBulbType.CROSS) {
                return 0xDCEBFAFF;
            }
            if (bulbType == TrafficLightBulbType.DONT_CROSS) {
                return 0xDCFF7014;
            }
        }
        int defaultColor = switch (bulbType) {
            case RED, RED_ARROW_LEFT, RED_ARROW_RIGHT, RED_ARROW_U_TURN, STRAIGHT_RED, DONT_CROSS,
                    NO_RIGHT_TURN, NO_LEFT_TURN -> 0xDCFF2A20;
            case YELLOW, YELLOW_ARROW_LEFT, YELLOW_ARROW_RIGHT, YELLOW_ARROW_U_TURN, STRAIGHT_YELLOW -> 0xDCFFD230;
            case GREEN, GREEN_ARROW_LEFT, GREEN_ARROW_RIGHT, GREEN_ARROW_U_TURN, STRAIGHT_GREEN, CROSS -> 0xDC40FF62;
            case BUS_STOP, BUS_CAUTION, BUS_GO -> 0xDCF8F4E8;
        };
        return technologyColor(blockEntity.lampTechnology(), bulbType, defaultColor, false);
    }

    private static int softGlowColor(TrafficLightBlockEntity blockEntity, TrafficLightBulbType bulbType) {
        if (blockEntity.isPedestrianSignal()) {
            if (bulbType == TrafficLightBulbType.CROSS) {
                return 0x58EBFAFF;
            }
            if (bulbType == TrafficLightBulbType.DONT_CROSS) {
                return 0x58FF7014;
            }
        }
        int defaultColor = switch (bulbType) {
            case RED, RED_ARROW_LEFT, RED_ARROW_RIGHT, RED_ARROW_U_TURN, STRAIGHT_RED, DONT_CROSS,
                    NO_RIGHT_TURN, NO_LEFT_TURN -> 0x58FF2A20;
            case YELLOW, YELLOW_ARROW_LEFT, YELLOW_ARROW_RIGHT, YELLOW_ARROW_U_TURN, STRAIGHT_YELLOW -> 0x58FFD230;
            case GREEN, GREEN_ARROW_LEFT, GREEN_ARROW_RIGHT, GREEN_ARROW_U_TURN, STRAIGHT_GREEN, CROSS -> 0x5840FF62;
            case BUS_STOP, BUS_CAUTION, BUS_GO -> 0x58F8F4E8;
        };
        return technologyColor(blockEntity.lampTechnology(), bulbType, defaultColor, true);
    }

    private static int technologyColor(
            SignalLampTechnology technology,
            TrafficLightBulbType bulbType,
            int defaultColor,
            boolean soft
    ) {
        if (technology == SignalLampTechnology.DEFAULT) {
            return defaultColor;
        }
        if (bulbType == TrafficLightBulbType.BUS_STOP
                || bulbType == TrafficLightBulbType.BUS_CAUTION
                || bulbType == TrafficLightBulbType.BUS_GO) {
            return soft ? 0x58F8F4E8 : 0xDCF8F4E8;
        }
        boolean red = switch (bulbType) {
            case RED, RED_ARROW_LEFT, RED_ARROW_RIGHT, RED_ARROW_U_TURN, STRAIGHT_RED, DONT_CROSS, NO_RIGHT_TURN, NO_LEFT_TURN -> true;
            default -> false;
        };
        boolean yellow = switch (bulbType) {
            case YELLOW, YELLOW_ARROW_LEFT, YELLOW_ARROW_RIGHT, YELLOW_ARROW_U_TURN, STRAIGHT_YELLOW -> true;
            default -> false;
        };
        if (technology == SignalLampTechnology.LED) {
            if (soft) {
                return red ? 0x34FF2018 : yellow ? 0x34FFE128 : 0x3438FF58;
            }
            return red ? 0xF2FF2018 : yellow ? 0xF2FFE128 : 0xF238FF58;
        }
        // Legacy incandescent/halogen optics have a warmer filament core and a broader halo.
        if (soft) {
            return red ? 0x70FF5A2A : yellow ? 0x70FFB018 : 0x70A8D85A;
        }
        return red ? 0xD8FF5A2A : yellow ? 0xD8FFB018 : 0xD8A8D85A;
    }

    private static void renderLampTechnologyOverlay(
            SignalLampTechnology technology,
            TrafficLightBulbType bulbType,
            PoseStack.Pose pose,
            MultiBufferSource bufferSource,
            float minX,
            float minY,
            float z,
            float maxX,
            float maxY
    ) {
        if (technology == SignalLampTechnology.DEFAULT || !isCircularLampFace(bulbType)) {
            return;
        }
        ResourceLocation overlay = technology == SignalLampTechnology.LED
                ? LED_GRID_TEXTURE : HALOGEN_HOTSPOT_TEXTURE;
        int tint = technology == SignalLampTechnology.LED
                ? lampOverlayColor(bulbType, 0xF4)
                : 0xE8FFF1C6;
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(overlay));
        drawBulbFace(
                consumer, pose, minX, minY, z, maxX, maxY,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, tint
        );
    }

    private static boolean isCircularLampFace(TrafficLightBulbType bulbType) {
        return switch (bulbType) {
            case RED, YELLOW, GREEN, STRAIGHT_RED, STRAIGHT_YELLOW, STRAIGHT_GREEN, BUS_STOP, BUS_CAUTION, BUS_GO -> true;
            default -> false;
        };
    }

    private static int lampOverlayColor(TrafficLightBulbType bulbType, int alpha) {
        int rgb = switch (bulbType) {
            case RED, RED_ARROW_LEFT, RED_ARROW_RIGHT, RED_ARROW_U_TURN, STRAIGHT_RED, DONT_CROSS,
                    NO_RIGHT_TURN, NO_LEFT_TURN -> 0xFF241C;
            case YELLOW, YELLOW_ARROW_LEFT, YELLOW_ARROW_RIGHT, YELLOW_ARROW_U_TURN, STRAIGHT_YELLOW -> 0xFFE02C;
            case GREEN, GREEN_ARROW_LEFT, GREEN_ARROW_RIGHT, GREEN_ARROW_U_TURN, STRAIGHT_GREEN, CROSS -> 0x42FF62;
            case BUS_STOP, BUS_CAUTION, BUS_GO -> 0xF8F4E8;
        };
        return (alpha << 24) | rgb;
    }

    private static void drawBulbFace(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float minX,
            float minY,
            float z,
            float maxX,
            float maxY,
            int packedLight,
            int packedOverlay,
            int color
    ) {
        consumer.addVertex(pose.pose(), maxX, minY, z)
                .setColor(color).setUv(1.0F, 1.0F).setOverlay(packedOverlay).setLight(packedLight)
                .setNormal(pose, 0.0F, 0.0F, 1.0F);
        consumer.addVertex(pose.pose(), maxX, maxY, z)
                .setColor(color).setUv(1.0F, 0.0F).setOverlay(packedOverlay).setLight(packedLight)
                .setNormal(pose, 0.0F, 0.0F, 1.0F);
        consumer.addVertex(pose.pose(), minX, maxY, z)
                .setColor(color).setUv(0.0F, 0.0F).setOverlay(packedOverlay).setLight(packedLight)
                .setNormal(pose, 0.0F, 0.0F, 1.0F);
        consumer.addVertex(pose.pose(), minX, minY, z)
                .setColor(color).setUv(0.0F, 1.0F).setOverlay(packedOverlay).setLight(packedLight)
                .setNormal(pose, 0.0F, 0.0F, 1.0F);
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
            int packedLight
    ) {
        drawCuboidPixels(consumer, pose, minX, minY, minZ, maxX, maxY, maxZ, packedLight, 0xFFFFFFFF);
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
                packedLight,
                color
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

        // South (+Z)
        vertex(consumer, pose, maxX, minY, maxZ, 1, 1, 0, 0, 1, color, packedLight, overlay);
        vertex(consumer, pose, maxX, maxY, maxZ, 1, 0, 0, 0, 1, color, packedLight, overlay);
        vertex(consumer, pose, minX, maxY, maxZ, 0, 0, 0, 0, 1, color, packedLight, overlay);
        vertex(consumer, pose, minX, minY, maxZ, 0, 1, 0, 0, 1, color, packedLight, overlay);

        // North (-Z)
        vertex(consumer, pose, minX, minY, minZ, 1, 1, 0, 0, -1, color, packedLight, overlay);
        vertex(consumer, pose, minX, maxY, minZ, 1, 0, 0, 0, -1, color, packedLight, overlay);
        vertex(consumer, pose, maxX, maxY, minZ, 0, 0, 0, 0, -1, color, packedLight, overlay);
        vertex(consumer, pose, maxX, minY, minZ, 0, 1, 0, 0, -1, color, packedLight, overlay);

        // East (+X)
        vertex(consumer, pose, maxX, minY, minZ, 1, 1, 1, 0, 0, color, packedLight, overlay);
        vertex(consumer, pose, maxX, maxY, minZ, 1, 0, 1, 0, 0, color, packedLight, overlay);
        vertex(consumer, pose, maxX, maxY, maxZ, 0, 0, 1, 0, 0, color, packedLight, overlay);
        vertex(consumer, pose, maxX, minY, maxZ, 0, 1, 1, 0, 0, color, packedLight, overlay);

        // West (-X)
        vertex(consumer, pose, minX, minY, maxZ, 1, 1, -1, 0, 0, color, packedLight, overlay);
        vertex(consumer, pose, minX, maxY, maxZ, 1, 0, -1, 0, 0, color, packedLight, overlay);
        vertex(consumer, pose, minX, maxY, minZ, 0, 0, -1, 0, 0, color, packedLight, overlay);
        vertex(consumer, pose, minX, minY, minZ, 0, 1, -1, 0, 0, color, packedLight, overlay);

        // Up (+Y)
        vertex(consumer, pose, minX, maxY, minZ, 0, 1, 0, 1, 0, color, packedLight, overlay);
        vertex(consumer, pose, minX, maxY, maxZ, 0, 0, 0, 1, 0, color, packedLight, overlay);
        vertex(consumer, pose, maxX, maxY, maxZ, 1, 0, 0, 1, 0, color, packedLight, overlay);
        vertex(consumer, pose, maxX, maxY, minZ, 1, 1, 0, 1, 0, color, packedLight, overlay);

        // Down (-Y)
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
            int packedOverlay
    ) {
        consumer.addVertex(pose.pose(), x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(pose, normalX, normalY, normalZ);
    }
}
