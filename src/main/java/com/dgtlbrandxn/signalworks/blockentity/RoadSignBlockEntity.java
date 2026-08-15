package com.dgtlbrandxn.signalworks.blockentity;

import com.dgtlbrandxn.signalworks.catalog.RoadSignCatalog;
import com.dgtlbrandxn.signalworks.catalog.RoadSignEntry;
import com.dgtlbrandxn.signalworks.menu.RoadSignMenu;
import com.dgtlbrandxn.signalworks.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** Stores a stable catalog ID rather than copying texture data into world NBT. */
public final class RoadSignBlockEntity extends BlockEntity implements MenuProvider {
    public static final int DATA_SELECTED_INDEX = 0;
    public static final int DATA_CATALOG_SIZE = 1;
    public static final int DATA_COUNT = 2;
    private static final String TAG_SIGN_ID = "CatalogSignId";

    private String selectedId = RoadSignCatalog.entry(0).id();

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_SELECTED_INDEX -> selectedIndex();
                case DATA_CATALOG_SIZE -> RoadSignCatalog.entries().size();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == DATA_SELECTED_INDEX) {
                setSelectedIndex(value);
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public RoadSignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROAD_SIGN.get(), pos, state);
    }

    public ContainerData dataAccess() {
        return dataAccess;
    }

    public RoadSignEntry selectedEntry() {
        return RoadSignCatalog.entry(selectedId);
    }

    public String selectedId() {
        return selectedId;
    }

    public int selectedIndex() {
        return RoadSignCatalog.indexOf(selectedId);
    }

    public void setSelectedIndex(int index) {
        setSelectedId(RoadSignCatalog.entry(index).id());
    }

    public void setSelectedId(String id) {
        RoadSignEntry resolved = RoadSignCatalog.entry(id);
        if (resolved.id().equals(selectedId)) {
            return;
        }
        selectedId = resolved.id();
        sync();
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("menu.trafficcontrol.road_sign_catalog");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new RoadSignMenu(containerId, inventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString(TAG_SIGN_ID, selectedId);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(TAG_SIGN_ID)) {
            selectedId = RoadSignCatalog.entry(tag.getString(TAG_SIGN_ID)).id();
        } else {
            selectedId = RoadSignCatalog.entry(0).id();
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
