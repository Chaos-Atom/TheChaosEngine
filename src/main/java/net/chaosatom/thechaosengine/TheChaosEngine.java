package net.chaosatom.thechaosengine;

import net.chaosatom.thechaosengine.block.ChaosEngineBlocks;
import net.chaosatom.thechaosengine.block.entity.ChaosEngineBlockEntities;
import net.chaosatom.thechaosengine.client.renderer.AtmosphericCondenserBlockEntityRenderer;
import net.chaosatom.thechaosengine.client.renderer.CompactInductionFoundryBlockEntityRenderer;
import net.chaosatom.thechaosengine.item.ChaosEngineCreativeModeTabs;
import net.chaosatom.thechaosengine.item.ChaosEngineItems;
import net.chaosatom.thechaosengine.recipe.ChaosEngineRecipes;
import net.chaosatom.thechaosengine.screen.ChaosEngineMenuTypes;
import net.chaosatom.thechaosengine.screen.custom.AtmosphericCondenserScreen;
import net.chaosatom.thechaosengine.screen.custom.CompactCoalGeneratorScreen;
import net.chaosatom.thechaosengine.screen.custom.CompactInductionFoundryScreen;
import net.chaosatom.thechaosengine.screen.custom.CompactPulverizerScreen;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(TheChaosEngine.MOD_ID)
public class TheChaosEngine {
    public static final String MOD_ID = "thechaosengine";
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public TheChaosEngine(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        ChaosEngineCreativeModeTabs.register(modEventBus);

        ChaosEngineItems.register(modEventBus);
        ChaosEngineBlocks.register(modEventBus);

        ChaosEngineBlockEntities.register(modEventBus);
        ChaosEngineMenuTypes.register(modEventBus);

        ChaosEngineRecipes.register(modEventBus);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    static class ClientModEvents {
        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {
        }

        @SubscribeEvent
        public static void registerBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ChaosEngineBlockEntities.COMPACT_INDUCTION_FOUNDRY_BE.get(), CompactInductionFoundryBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ChaosEngineBlockEntities.ATMOSPHERIC_CONDENSER_BE.get(), AtmosphericCondenserBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ChaosEngineMenuTypes.COMPACT_COAL_GENERATOR_MENU.get(), CompactCoalGeneratorScreen::new);
            event.register(ChaosEngineMenuTypes.COMPACT_PULVERIZER_MENU.get(), CompactPulverizerScreen::new);
            event.register(ChaosEngineMenuTypes.COMPACT_INDUCTION_FOUNDRY_MENU.get(), CompactInductionFoundryScreen::new);
            event.register(ChaosEngineMenuTypes.ATMOSPHERIC_CONDENSER_MENU.get(), AtmosphericCondenserScreen::new);
        }
    }
}