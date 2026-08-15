package com.dgtlbrandxn.signalworks.item;

import com.dgtlbrandxn.signalworks.block.AbstractStreetLightBlock;
import com.dgtlbrandxn.signalworks.block.MunicipalStreetSignBlock;
import com.dgtlbrandxn.signalworks.block.SignalArmBlock;
import com.dgtlbrandxn.signalworks.block.StreetLightHeadBlock;
import com.dgtlbrandxn.signalworks.block.TrafficLightBlock;
import com.dgtlbrandxn.signalworks.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/** Rotates infrastructure and changes adjustable signal-arm geometry. */
public final class AdjusterItem extends Item {
    private static final int QUARTER_EIGHTH_TURN = 2; // 2 of 16 states = 45 degrees

    public AdjusterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof SignalArmBlock) {
            return adjustSignalArm(context, state, player);
        }

        IntegerProperty rotationProperty = rotationProperty(state);
        if (rotationProperty == null) {
            if (player != null && !level.isClientSide) {
                player.displayClientMessage(
                        Component.translatable("message.trafficcontrol.adjuster.invalid_target")
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
            }
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        int current = state.getValue(rotationProperty);
        int delta = player != null && player.isShiftKeyDown()
                ? -QUARTER_EIGHTH_TURN
                : QUARTER_EIGHTH_TURN;
        int next = Math.floorMod(current + delta, 16);
        BlockState adjusted = state.setValue(rotationProperty, next);
        level.setBlock(pos, adjusted, Block.UPDATE_ALL);
        playAdjustmentSound(level, pos);

        if (player != null) {
            double degrees = next * 22.5D;
            if (degrees > 180.0D) {
                degrees -= 360.0D;
            }
            String formatted = Math.abs(degrees - Math.rint(degrees)) < 0.001D
                    ? String.format(Locale.ROOT, "%.0f°", degrees)
                    : String.format(Locale.ROOT, "%.1f°", degrees);
            player.displayClientMessage(
                    Component.translatable(
                                    delta > 0
                                            ? "message.trafficcontrol.adjuster.rotated_positive"
                                            : "message.trafficcontrol.adjuster.rotated_negative",
                                    formatted
                            )
                            .withStyle(ChatFormatting.AQUA),
                    true
            );
        }
        return InteractionResult.SUCCESS;
    }


    private static InteractionResult adjustSignalArm(UseOnContext context, BlockState state, @Nullable Player player) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        Direction rightDirection = SignalArmBlock.rightDirection(state);
        Vec3 hit = context.getClickLocation();
        double relativeX = hit.x - (pos.getX() + 0.5D);
        double relativeZ = hit.z - (pos.getZ() + 0.5D);
        double sideAmount = relativeX * rightDirection.getStepX() + relativeZ * rightDirection.getStepZ();

        // The center saddle changes whether attached heads face toward or away from the pole.
        if (Math.abs(sideAmount) < 0.16D) {
            boolean reversed = !state.getValue(SignalArmBlock.REVERSED);
            level.setBlock(pos, state.setValue(SignalArmBlock.REVERSED, reversed), Block.UPDATE_ALL);
            playAdjustmentSound(level, pos);
            if (player != null) {
                player.displayClientMessage(
                        Component.translatable(reversed
                                        ? "message.trafficcontrol.adjuster.arm_facing_away"
                                        : "message.trafficcontrol.adjuster.arm_facing_toward")
                                .withStyle(ChatFormatting.GOLD),
                        true
                );
            }
            return InteractionResult.SUCCESS;
        }

        boolean rightWing = sideAmount > 0.0D;
        IntegerProperty property = rightWing ? SignalArmBlock.RIGHT_LENGTH : SignalArmBlock.LEFT_LENGTH;
        int current = state.getValue(property);
        int delta = player != null && player.isShiftKeyDown() ? -1 : 1;
        int next = current + delta;

        if (next < 0) {
            showArmMessage(player, "message.trafficcontrol.adjuster.arm_minimum", ChatFormatting.GRAY);
            return InteractionResult.SUCCESS;
        }
        if (next > SignalArmBlock.MAX_WING_LENGTH
                || (delta > 0 && SignalArmBlock.slotCount(state) >= SignalArmBlock.MAX_SIGNAL_SLOTS)) {
            showArmMessage(player, "message.trafficcontrol.adjuster.arm_maximum", ChatFormatting.GRAY);
            return InteractionResult.SUCCESS;
        }
        if (delta > 0 && !SignalArmBlock.extensionSpaceIsClear(level, pos, state, rightWing)) {
            showArmMessage(player, "message.trafficcontrol.adjuster.arm_extension_blocked", ChatFormatting.RED);
            return InteractionResult.SUCCESS;
        }
        if (delta < 0 && SignalArmBlock.removedSlotIsOccupied(level, pos, state, rightWing)) {
            showArmMessage(player, "message.trafficcontrol.adjuster.arm_signal_blocked", ChatFormatting.RED);
            return InteractionResult.SUCCESS;
        }

        BlockState adjusted = state.setValue(property, next);
        level.setBlock(pos, adjusted, Block.UPDATE_ALL);
        playAdjustmentSound(level, pos);
        if (player != null) {
            player.displayClientMessage(
                    Component.translatable(
                                    delta > 0
                                            ? "message.trafficcontrol.adjuster.arm_extended"
                                            : "message.trafficcontrol.adjuster.arm_shortened",
                                    Component.translatable(rightWing
                                            ? "side.trafficcontrol.right"
                                            : "side.trafficcontrol.left"),
                                    SignalArmBlock.slotCount(adjusted)
                            )
                            .withStyle(ChatFormatting.AQUA),
                    true
            );
        }
        return InteractionResult.SUCCESS;
    }

    private static void showArmMessage(@Nullable Player player, String key, ChatFormatting color) {
        if (player != null) {
            player.displayClientMessage(Component.translatable(key).withStyle(color), true);
        }
    }

    private static void playAdjustmentSound(Level level, BlockPos pos) {
        level.playSound(
                null,
                pos,
                ModSounds.SCREWDRIVER.get(),
                SoundSource.BLOCKS,
                0.7F,
                0.9F + level.getRandom().nextFloat() * 0.2F
        );
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag
    ) {
        tooltipComponents.add(Component.translatable("item.trafficcontrol.adjuster.description")
                .withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("item.trafficcontrol.adjuster.controls")
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(Component.translatable("item.trafficcontrol.adjuster.arm_controls")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    @Nullable
    private static IntegerProperty rotationProperty(BlockState state) {
        if (state.hasProperty(TrafficLightBlock.ROTATION)) {
            return TrafficLightBlock.ROTATION;
        }
        if (state.getBlock() instanceof StreetLightHeadBlock
                && state.hasProperty(StreetLightHeadBlock.ROTATION)) {
            return StreetLightHeadBlock.ROTATION;
        }
        if (state.getBlock() instanceof AbstractStreetLightBlock
                && state.hasProperty(AbstractStreetLightBlock.ROTATION)) {
            return AbstractStreetLightBlock.ROTATION;
        }
        if (state.getBlock() instanceof MunicipalStreetSignBlock
                && state.hasProperty(MunicipalStreetSignBlock.ROTATION)) {
            return MunicipalStreetSignBlock.ROTATION;
        }
        return null;
    }
}
