package com.dgtlbrandxn.signalworks.item;

import com.dgtlbrandxn.signalworks.block.SignalArmBlock;
import com.dgtlbrandxn.signalworks.block.SignalArmSize;
import com.dgtlbrandxn.signalworks.block.SignalArmType;
import com.dgtlbrandxn.signalworks.block.SignalBackplateStyle;
import com.dgtlbrandxn.signalworks.block.PedestrianSignalStyle;
import com.dgtlbrandxn.signalworks.block.SignalMount;
import com.dgtlbrandxn.signalworks.block.SignalLampTechnology;
import com.dgtlbrandxn.signalworks.block.SignalVisorStyle;
import com.dgtlbrandxn.signalworks.block.TrafficLightBlock;
import com.dgtlbrandxn.signalworks.blockentity.TrafficLightBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/** Configures visual signal hardware without changing phase assignments. */
public final class SignalCustomizerItem extends Item {
    private static final String TAG_MODE = "Mode";

    public SignalCustomizerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        BlockState clickedState = level.getBlockState(context.getClickedPos());
        SignalCustomizerMode mode = mode(context.getItemInHand());
        int step = player.isShiftKeyDown() ? -1 : 1;

        if (clickedState.getBlock() instanceof SignalArmBlock) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }

            BlockState nextState;
            Component value;
            switch (mode) {
                case ARM_SIZE -> {
                    nextState = SignalArmBlock.cycleHeadSize(clickedState, step);
                    SignalArmSize size = nextState.getValue(SignalArmBlock.HEAD_SIZE);
                    value = Component.translatable("style.trafficcontrol.arm_size." + size.getSerializedName());
                }
                case ARM_TYPE -> {
                    nextState = SignalArmBlock.cycleArmType(clickedState, step);
                    SignalArmType type = nextState.getValue(SignalArmBlock.ARM_TYPE);
                    value = Component.translatable("style.trafficcontrol.arm_type." + type.getSerializedName());
                }
                case ARM_MULTI -> {
                    nextState = SignalArmBlock.toggleMulti(clickedState);
                    value = Component.translatable(nextState.getValue(SignalArmBlock.MULTI)
                            ? "style.trafficcontrol.arm_multi.on"
                            : "style.trafficcontrol.arm_multi.off");
                }
                default -> {
                    player.displayClientMessage(
                            Component.translatable("message.trafficcontrol.customizer.arm_mode_required")
                                    .withStyle(ChatFormatting.GRAY),
                            true
                    );
                    return InteractionResult.SUCCESS;
                }
            }

            level.setBlock(context.getClickedPos(), nextState, Block.UPDATE_ALL);
            player.displayClientMessage(
                    Component.translatable(
                                    "message.trafficcontrol.customizer.applied",
                                    Component.translatable("mode.trafficcontrol.customizer." + mode.serializedName()),
                                    value
                            )
                            .withStyle(ChatFormatting.AQUA),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        if (!(level.getBlockEntity(context.getClickedPos()) instanceof TrafficLightBlockEntity signal)) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.translatable("message.trafficcontrol.customizer.invalid_target")
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
            }
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        Component value;

        switch (mode) {
            case BACKPLATE -> {
                SignalBackplateStyle style = signal.cycleBackplate(step);
                value = Component.translatable("style.trafficcontrol.backplate." + style.serializedName());
            }
            case VISOR -> {
                SignalVisorStyle style = signal.cycleVisor(step);
                value = Component.translatable("style.trafficcontrol.visor." + style.serializedName());
            }
            case MOUNT -> {
                BlockState state = signal.getBlockState();
                if (!state.hasProperty(TrafficLightBlock.MOUNT)) {
                    return InteractionResult.PASS;
                }
                SignalMount[] values = SignalMount.values();
                SignalMount current = state.getValue(TrafficLightBlock.MOUNT);
                SignalMount next = values[Math.floorMod(current.ordinal() + step, values.length)];
                level.setBlock(context.getClickedPos(), state.setValue(TrafficLightBlock.MOUNT, next), Block.UPDATE_ALL);
                value = Component.translatable("style.trafficcontrol.mount." + next.getSerializedName());
            }
            case LAMP -> {
                SignalLampTechnology technology = signal.cycleLampTechnology(step);
                value = Component.translatable("style.trafficcontrol.lamp." + technology.serializedName());
            }
            case PEDESTRIAN -> {
                if (!signal.isPedestrianSignal()) {
                    player.displayClientMessage(
                            Component.translatable("message.trafficcontrol.customizer.pedestrian_only")
                                    .withStyle(ChatFormatting.GRAY),
                            true
                    );
                    return InteractionResult.SUCCESS;
                }
                PedestrianSignalStyle style = signal.cyclePedestrianStyle(step);
                value = Component.translatable("style.trafficcontrol.pedestrian." + style.serializedName());
            }
            case ARM_SIZE, ARM_TYPE, ARM_MULTI -> {
                player.displayClientMessage(
                        Component.translatable("message.trafficcontrol.customizer.arm_only")
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
                return InteractionResult.SUCCESS;
            }
            default -> throw new IllegalStateException("Unhandled customizer mode: " + mode);
        }

        player.displayClientMessage(
                Component.translatable(
                                "message.trafficcontrol.customizer.applied",
                                Component.translatable("mode.trafficcontrol.customizer." + mode.serializedName()),
                                value
                        )
                        .withStyle(ChatFormatting.AQUA),
                true
        );
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            int step = player.isShiftKeyDown() ? -1 : 1;
            SignalCustomizerMode next = mode(stack).step(step);
            setMode(stack, next);
            player.displayClientMessage(
                    Component.translatable(
                                    "message.trafficcontrol.customizer.mode",
                                    Component.translatable("mode.trafficcontrol.customizer." + next.serializedName())
                            )
                            .withStyle(ChatFormatting.GOLD),
                    true
            );
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag
    ) {
        tooltipComponents.add(Component.translatable("item.trafficcontrol.signal_customizer.description")
                .withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(
                        "item.trafficcontrol.signal_customizer.mode",
                        Component.translatable("mode.trafficcontrol.customizer." + mode(stack).serializedName())
                )
                .withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("item.trafficcontrol.signal_customizer.controls")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    public static SignalCustomizerMode mode(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains(TAG_MODE)) {
            return SignalCustomizerMode.BACKPLATE;
        }
        try {
            return SignalCustomizerMode.valueOf(tag.getString(TAG_MODE).toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return SignalCustomizerMode.BACKPLATE;
        }
    }

    private static void setMode(ItemStack stack, SignalCustomizerMode mode) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack,
                tag -> tag.putString(TAG_MODE, mode.serializedName()));
    }
}
