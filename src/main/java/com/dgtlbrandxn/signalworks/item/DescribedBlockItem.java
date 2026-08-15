package com.dgtlbrandxn.signalworks.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

/** Block item with a single localized gray description line. */
public final class DescribedBlockItem extends BlockItem {
    private final String descriptionKey;

    public DescribedBlockItem(Block block, Item.Properties properties, String descriptionKey) {
        super(block, properties);
        this.descriptionKey = descriptionKey;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(descriptionKey).withStyle(ChatFormatting.GRAY));
    }
}
