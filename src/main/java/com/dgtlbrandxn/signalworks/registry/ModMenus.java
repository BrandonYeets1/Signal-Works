package com.dgtlbrandxn.signalworks.registry;

import com.dgtlbrandxn.signalworks.TrafficControl;
import com.dgtlbrandxn.signalworks.menu.TrafficLightControllerMenu;
import com.dgtlbrandxn.signalworks.menu.MunicipalStreetSignMenu;
import com.dgtlbrandxn.signalworks.menu.ConstructionMessageBoardMenu;
import com.dgtlbrandxn.signalworks.menu.RoadSignMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, TrafficControl.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<TrafficLightControllerMenu>> TRAFFIC_LIGHT_CONTROLLER =
            MENUS.register(
                    "traffic_light_controller",
                    () -> new MenuType<>(TrafficLightControllerMenu::new, FeatureFlags.DEFAULT_FLAGS)
            );


    public static final DeferredHolder<MenuType<?>, MenuType<RoadSignMenu>> ROAD_SIGN =
            MENUS.register(
                    "road_sign",
                    () -> new MenuType<>(RoadSignMenu::new, FeatureFlags.DEFAULT_FLAGS)
            );

    public static final DeferredHolder<MenuType<?>, MenuType<MunicipalStreetSignMenu>> MUNICIPAL_STREET_SIGN =
            MENUS.register(
                    "municipal_street_sign",
                    () -> new MenuType<>(MunicipalStreetSignMenu::new, FeatureFlags.DEFAULT_FLAGS)
            );

    public static final DeferredHolder<MenuType<?>, MenuType<ConstructionMessageBoardMenu>> CONSTRUCTION_MESSAGE_BOARD =
            MENUS.register(
                    "construction_message_board",
                    () -> new MenuType<>(ConstructionMessageBoardMenu::new, FeatureFlags.DEFAULT_FLAGS)
            );

    private ModMenus() {
    }
}
