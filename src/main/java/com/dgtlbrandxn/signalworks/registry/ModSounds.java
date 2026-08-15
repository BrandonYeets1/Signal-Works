package com.dgtlbrandxn.signalworks.registry;

import com.dgtlbrandxn.signalworks.TrafficControl;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, TrafficControl.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> GATE = SOUNDS.register("gate", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TrafficControl.MOD_ID, "gate")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SAFETRAN_TYPE_3 = SOUNDS.register("safetran_type_3", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TrafficControl.MOD_ID, "safetran_type_3")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SAFETRAN_MECHANICAL = SOUNDS.register("safetran_mechanical", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TrafficControl.MOD_ID, "safetran_mechanical")));
    public static final DeferredHolder<SoundEvent, SoundEvent> WCH = SOUNDS.register("wch", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TrafficControl.MOD_ID, "wch")));
    public static final DeferredHolder<SoundEvent, SoundEvent> PED_BUTTON = SOUNDS.register("ped_button", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TrafficControl.MOD_ID, "ped_button")));
    public static final DeferredHolder<SoundEvent, SoundEvent> WIGWAG = SOUNDS.register("wigwag", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TrafficControl.MOD_ID, "wigwag")));
    public static final DeferredHolder<SoundEvent, SoundEvent> WCH_MECHANICAL_BELL = SOUNDS.register("wch_mechanical_bell", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TrafficControl.MOD_ID, "wch_mechanical_bell")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SCREWDRIVER = SOUNDS.register("screwdriver", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TrafficControl.MOD_ID, "screwdriver")));

    private ModSounds() {}
}
