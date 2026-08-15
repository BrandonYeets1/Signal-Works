package com.dgtlbrandxn.signalworks.item;

import com.dgtlbrandxn.signalworks.block.DedicatedTurnSignalBlock;
import com.dgtlbrandxn.signalworks.block.SignalMovement;
import com.dgtlbrandxn.signalworks.block.TrafficLight4Block;
import com.dgtlbrandxn.signalworks.block.TrafficLight5Block;
import com.dgtlbrandxn.signalworks.block.TrafficLight2Block;
import com.dgtlbrandxn.signalworks.block.TrafficLightDoghouseBlock;
import com.dgtlbrandxn.signalworks.block.RampMeterSignalBlock;
import com.dgtlbrandxn.signalworks.blockentity.TrafficLightBlockEntity;
import com.dgtlbrandxn.signalworks.blockentity.TrafficLightControllerBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Links signal heads to a specific controller and assigns their movement group. */
public final class EngineerWandItem extends Item {
    private static final String TAG_CONTROLLER_POS = "ControllerPos";
    private static final String TAG_CONTROLLER_DIMENSION = "ControllerDimension";
    private static final String TAG_MOVEMENT = "Movement";

    public EngineerWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();

        if (player == null) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(clickedPos) instanceof TrafficLightControllerBlockEntity controller) {
            setController(stack, level, clickedPos);
            player.displayClientMessage(
                    Component.translatable("message.trafficcontrol.wand.controller_selected", formatPos(clickedPos))
                            .withStyle(ChatFormatting.GREEN),
                    true
            );
            controller.pruneInvalidLinks();
            return InteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(clickedPos) instanceof TrafficLightBlockEntity signal) {
            BlockPos controllerPos = selectedController(stack, level);
            if (controllerPos == null) {
                player.displayClientMessage(
                        Component.translatable("message.trafficcontrol.wand.no_controller")
                                .withStyle(ChatFormatting.RED),
                        true
                );
                return InteractionResult.FAIL;
            }

            if (!(level.getBlockEntity(controllerPos) instanceof TrafficLightControllerBlockEntity controller)) {
                clearController(stack);
                player.displayClientMessage(
                        Component.translatable("message.trafficcontrol.wand.controller_missing")
                                .withStyle(ChatFormatting.RED),
                        true
                );
                return InteractionResult.FAIL;
            }

            if (player.isShiftKeyDown()) {
                boolean removed = controller.unlinkSignal(clickedPos);
                player.displayClientMessage(
                        Component.translatable(
                                        removed
                                                ? "message.trafficcontrol.wand.signal_unlinked"
                                                : "message.trafficcontrol.wand.signal_not_linked",
                                        formatPos(clickedPos)
                                )
                                .withStyle(removed ? ChatFormatting.YELLOW : ChatFormatting.GRAY),
                        true
                );
                return InteractionResult.SUCCESS;
            }

            SignalMovement requested = movement(stack);
            SignalMovement movement = normalizeMovement(signal, requested);
            controller.linkSignal(clickedPos, movement);
            player.displayClientMessage(
                    Component.translatable(
                                    "message.trafficcontrol.wand.signal_linked",
                                    formatPos(clickedPos),
                                    movementLabel(movement)
                            )
                            .withStyle(ChatFormatting.AQUA),
                    true
            );
            return InteractionResult.SUCCESS;
        }

        player.displayClientMessage(
                Component.translatable("message.trafficcontrol.wand.invalid_target")
                        .withStyle(ChatFormatting.GRAY),
                true
        );
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                clearController(stack);
                player.displayClientMessage(
                        Component.translatable("message.trafficcontrol.wand.selection_cleared")
                                .withStyle(ChatFormatting.YELLOW),
                        true
                );
            } else {
                SignalMovement next = movement(stack).next();
                setMovement(stack, next);
                player.displayClientMessage(
                        Component.translatable("message.trafficcontrol.wand.mode", movementLabel(next))
                                .withStyle(ChatFormatting.AQUA),
                        true
                );
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasController(stack) || super.isFoil(stack);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag
    ) {
        tooltipComponents.add(Component.translatable("item.trafficcontrol.engineer_wand.description")
                .withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(
                        "item.trafficcontrol.engineer_wand.mode",
                        movementLabel(movement(stack))
                )
                .withStyle(ChatFormatting.AQUA));

        CompoundTag tag = customData(stack);
        if (tag.contains(TAG_CONTROLLER_POS)) {
            tooltipComponents.add(Component.translatable(
                            "item.trafficcontrol.engineer_wand.controller",
                            formatPos(BlockPos.of(tag.getLong(TAG_CONTROLLER_POS)))
                    )
                    .withStyle(ChatFormatting.GREEN));
        } else {
            tooltipComponents.add(Component.translatable("item.trafficcontrol.engineer_wand.unbound")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
        tooltipComponents.add(Component.translatable("item.trafficcontrol.engineer_wand.controls")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    public static boolean isEngineerWand(ItemStack stack) {
        return stack.getItem() instanceof EngineerWandItem;
    }

    @Nullable
    public static BlockPos selectedController(ItemStack stack, Level level) {
        CompoundTag tag = customData(stack);
        if (!tag.contains(TAG_CONTROLLER_POS) || !tag.contains(TAG_CONTROLLER_DIMENSION)) {
            return null;
        }
        if (!tag.getString(TAG_CONTROLLER_DIMENSION).equals(level.dimension().location().toString())) {
            return null;
        }
        return BlockPos.of(tag.getLong(TAG_CONTROLLER_POS));
    }

    public static SignalMovement movement(ItemStack stack) {
        CompoundTag tag = customData(stack);
        return tag.contains(TAG_MOVEMENT)
                ? SignalMovement.byName(tag.getString(TAG_MOVEMENT))
                : SignalMovement.THROUGH;
    }

    private static boolean hasController(ItemStack stack) {
        return customData(stack).contains(TAG_CONTROLLER_POS);
    }

    private static void setController(ItemStack stack, Level level, BlockPos pos) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putLong(TAG_CONTROLLER_POS, pos.asLong());
            tag.putString(TAG_CONTROLLER_DIMENSION, level.dimension().location().toString());
            if (!tag.contains(TAG_MOVEMENT)) {
                tag.putString(TAG_MOVEMENT, SignalMovement.THROUGH.getSerializedName());
            }
        });
    }

    private static void clearController(ItemStack stack) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.remove(TAG_CONTROLLER_POS);
            tag.remove(TAG_CONTROLLER_DIMENSION);
        });
    }

    private static void setMovement(ItemStack stack, SignalMovement movement) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack,
                tag -> tag.putString(TAG_MOVEMENT, movement.getSerializedName()));
    }

    private static CompoundTag customData(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static SignalMovement normalizeMovement(TrafficLightBlockEntity signal, SignalMovement requested) {
        if (signal.isPedestrianSignal() || signal.getBlockState().getBlock() instanceof TrafficLight2Block) {
            return SignalMovement.PEDESTRIAN;
        }
        if (signal.getBlockState().getBlock() instanceof RampMeterSignalBlock) return SignalMovement.THROUGH;
        if (signal.isBusSignal()) return SignalMovement.BUS;
        if (signal.isStraightArrowSignal()) return SignalMovement.THROUGH;
        if (signal.isUTurnSignal()) return SignalMovement.U_TURN;
        if (signal.getBlockState().getBlock() instanceof DedicatedTurnSignalBlock dedicated) {
            return dedicated.isRightTurn() ? SignalMovement.RIGHT : SignalMovement.LEFT;
        }
        if (signal.getBlockState().getBlock() instanceof TrafficLight4Block
                || signal.getBlockState().getBlock() instanceof TrafficLight5Block
                || signal.getBlockState().getBlock() instanceof TrafficLightDoghouseBlock) {
            // Combination heads default to both through and protected-left operation.
            // The controller map can still override them to THROUGH-only or LEFT-only.
            if (requested == SignalMovement.LEFT || requested == SignalMovement.THROUGH_LEFT) {
                return requested;
            }
            return SignalMovement.THROUGH_LEFT;
        }
        if (requested == SignalMovement.PEDESTRIAN || requested == SignalMovement.THROUGH_LEFT
                || requested == SignalMovement.U_TURN || requested == SignalMovement.BUS) {
            return SignalMovement.THROUGH;
        }
        return requested;
    }

    private static Component movementLabel(SignalMovement movement) {
        return Component.translatable("movement.trafficcontrol." + movement.getSerializedName());
    }

    private static String formatPos(BlockPos pos) {
        return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
    }
}
