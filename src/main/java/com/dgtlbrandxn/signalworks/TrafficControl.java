package com.dgtlbrandxn.signalworks;

import com.dgtlbrandxn.signalworks.registry.ModBlockEntities;
import com.dgtlbrandxn.signalworks.registry.ModBlocks;
import com.dgtlbrandxn.signalworks.registry.ModCreativeTabs;
import com.dgtlbrandxn.signalworks.registry.ModItems;
import com.dgtlbrandxn.signalworks.registry.ModMenus;
import com.dgtlbrandxn.signalworks.registry.ModSounds;
import com.dgtlbrandxn.signalworks.runtime.IntersectionLinkRuntime;
import com.dgtlbrandxn.signalworks.runtime.StreetLightRuntime;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(TrafficControl.MOD_ID)
public final class TrafficControl {
    public static final String MOD_ID = "trafficcontrol";

    public TrafficControl(IEventBus modBus) {
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModSounds.SOUNDS.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);

        NeoForge.EVENT_BUS.addListener(StreetLightRuntime::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(StreetLightRuntime::onChunkUnload);
        NeoForge.EVENT_BUS.addListener(StreetLightRuntime::onLevelTick);
        NeoForge.EVENT_BUS.addListener(IntersectionLinkRuntime::onLevelTick);
    }
}
