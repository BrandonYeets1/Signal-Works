package com.dgtlbrandxn.signalworks.menu;

import com.dgtlbrandxn.signalworks.blockentity.RoadSignBlockEntity;
import com.dgtlbrandxn.signalworks.catalog.RoadSignCatalog;
import com.dgtlbrandxn.signalworks.registry.ModBlocks;
import com.dgtlbrandxn.signalworks.registry.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/** Slotless server-backed selector for the road-sign catalog. */
public final class RoadSignMenu extends AbstractContainerMenu {
    public static final int BUTTON_RELOAD = 0;
    public static final int BUTTON_SELECT_BASE = 1000;

    private final ContainerData data;
    private final ContainerLevelAccess access;
    @Nullable
    private final RoadSignBlockEntity sign;

    public RoadSignMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainerData(RoadSignBlockEntity.DATA_COUNT),
                ContainerLevelAccess.NULL, null);
    }

    public RoadSignMenu(int containerId, Inventory inventory, RoadSignBlockEntity sign) {
        this(containerId, inventory, sign.dataAccess(),
                ContainerLevelAccess.create(sign.getLevel(), sign.getBlockPos()), sign);
    }

    private RoadSignMenu(
            int containerId,
            Inventory inventory,
            ContainerData data,
            ContainerLevelAccess access,
            @Nullable RoadSignBlockEntity sign
    ) {
        super(ModMenus.ROAD_SIGN.get(), containerId);
        this.data = data;
        this.access = access;
        this.sign = sign;
        checkContainerDataCount(data, RoadSignBlockEntity.DATA_COUNT);
        addDataSlots(data);
    }

    public int selectedIndex() {
        return Math.max(0, Math.min(RoadSignCatalog.entries().size() - 1,
                data.get(RoadSignBlockEntity.DATA_SELECTED_INDEX)));
    }

    public int catalogSize() {
        return data.get(RoadSignBlockEntity.DATA_CATALOG_SIZE);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == BUTTON_RELOAD) {
            RoadSignCatalog.reload();
            if (sign != null) {
                sign.setSelectedId(sign.selectedId());
            }
            broadcastChanges();
            return true;
        }
        if (id >= BUTTON_SELECT_BASE) {
            if (sign == null) {
                return false;
            }
            int index = id - BUTTON_SELECT_BASE;
            if (index < 0 || index >= RoadSignCatalog.entries().size()) {
                return false;
            }
            sign.setSelectedIndex(index);
            broadcastChanges();
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.SIGN.get());
    }
}
