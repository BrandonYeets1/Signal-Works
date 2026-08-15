package com.dgtlbrandxn.signalworks.client;

import com.dgtlbrandxn.signalworks.TrafficControl;
import com.dgtlbrandxn.signalworks.block.RotatablePoleBlock;
import com.dgtlbrandxn.signalworks.block.StreetLightArmBlock;
import com.dgtlbrandxn.signalworks.client.render.StreetLightBlockEntityRenderer;
import com.dgtlbrandxn.signalworks.client.render.MunicipalStreetSignBlockEntityRenderer;
import com.dgtlbrandxn.signalworks.client.render.RoadSignBlockEntityRenderer;
import com.dgtlbrandxn.signalworks.client.render.ConstructionMessageBoardRenderer;
import com.dgtlbrandxn.signalworks.client.render.SignalArmBlockEntityRenderer;
import com.dgtlbrandxn.signalworks.client.render.TrafficLightBlockEntityRenderer;
import com.dgtlbrandxn.signalworks.client.screen.TrafficLightControllerScreen;
import com.dgtlbrandxn.signalworks.client.screen.MunicipalStreetSignScreen;
import com.dgtlbrandxn.signalworks.client.screen.RoadSignCatalogScreen;
import com.dgtlbrandxn.signalworks.client.screen.ConstructionMessageBoardScreen;
import com.dgtlbrandxn.signalworks.registry.ModBlockEntities;
import com.dgtlbrandxn.signalworks.registry.ModBlocks;
import com.dgtlbrandxn.signalworks.registry.ModMenus;
import com.dgtlbrandxn.signalworks.registry.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = TrafficControl.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.TRAFFIC_LIGHT_CONTROLLER.get(), TrafficLightControllerScreen::new);
        event.register(ModMenus.MUNICIPAL_STREET_SIGN.get(), MunicipalStreetSignScreen::new);
        event.register(ModMenus.ROAD_SIGN.get(), RoadSignCatalogScreen::new);
        event.register(ModMenus.CONSTRUCTION_MESSAGE_BOARD.get(), ConstructionMessageBoardScreen::new);
    }


    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
                    if (tintIndex != 0 || !state.hasProperty(RotatablePoleBlock.COLOR)) {
                        return 0xFFFFFFFF;
                    }
                    return 0xFF000000 | state.getValue(RotatablePoleBlock.COLOR).rgb();
                },
                ModBlocks.SIGNAL_POLE_SMALL.get(),
                ModBlocks.SIGNAL_POLE_MEDIUM.get(),
                ModBlocks.SIGNAL_POLE_LARGE.get(),
                ModBlocks.MAST_ARM_SMALL.get(),
                ModBlocks.MAST_ARM_MEDIUM.get(),
                ModBlocks.MAST_ARM_LARGE.get(),
                ModBlocks.FREEWAY_SIGN_POLE.get(),
                ModBlocks.FREEWAY_SIGN_MAST.get(),
                ModBlocks.STREETLIGHT_ARM_STRAIGHT.get(),
                ModBlocks.STREETLIGHT_ARM_UPSWEEP.get(),
                ModBlocks.STREETLIGHT_ARM_CURVED.get());
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> tintIndex == 0
                        ? 0xFF000000 | RotatablePoleBlock.SupportColor.GALVANIZED.rgb()
                        : 0xFFFFFFFF,
                ModItems.SIGNAL_POLE_SMALL.get(),
                ModItems.SIGNAL_POLE_MEDIUM.get(),
                ModItems.SIGNAL_POLE_LARGE.get(),
                ModItems.MAST_ARM_SMALL.get(),
                ModItems.MAST_ARM_MEDIUM.get(),
                ModItems.MAST_ARM_LARGE.get(),
                ModItems.FREEWAY_SIGN_POLE.get(),
                ModItems.FREEWAY_SIGN_MAST.get(),
                ModItems.STREETLIGHT_ARM_STRAIGHT.get(),
                ModItems.STREETLIGHT_ARM_UPSWEEP.get(),
                ModItems.STREETLIGHT_ARM_CURVED.get());
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                ModBlockEntities.TRAFFIC_LIGHT.get(),
                TrafficLightBlockEntityRenderer::new
        );
        event.registerBlockEntityRenderer(
                ModBlockEntities.STREET_LIGHT.get(),
                StreetLightBlockEntityRenderer::new
        );
        event.registerBlockEntityRenderer(
                ModBlockEntities.MUNICIPAL_STREET_SIGN.get(),
                MunicipalStreetSignBlockEntityRenderer::new
        );
        event.registerBlockEntityRenderer(
                ModBlockEntities.ROAD_SIGN.get(),
                RoadSignBlockEntityRenderer::new
        );
        event.registerBlockEntityRenderer(
                ModBlockEntities.SIGNAL_ARM.get(),
                SignalArmBlockEntityRenderer::new
        );
        event.registerBlockEntityRenderer(
                ModBlockEntities.CONSTRUCTION_MESSAGE_BOARD.get(),
                ConstructionMessageBoardRenderer::new
        );
    }
}
