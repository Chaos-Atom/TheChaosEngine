package net.chaosatom.thechaosengine;

import net.chaosatom.thechaosengine.block.ChaosEngineBlocks;
import net.chaosatom.thechaosengine.block.entity.ChaosEngineBlockEntities;
import net.chaosatom.thechaosengine.block.entity.custom.DeployableSolarBlockEntity;
import net.chaosatom.thechaosengine.client.renderer.block.AtmosphericCondenserBlockEntityRenderer;
import net.chaosatom.thechaosengine.client.renderer.block.CompactInductionFoundryBlockEntityRenderer;
import net.chaosatom.thechaosengine.client.renderer.block.DeployableSolarBlockEntityRenderer;
import net.chaosatom.thechaosengine.client.renderer.block.SuspensionMixerBlockEntityRenderer;
import net.chaosatom.thechaosengine.fluid.ChaosEngineFluids;
import net.chaosatom.thechaosengine.item.ChaosEngineCreativeModeTabs;
import net.chaosatom.thechaosengine.item.ChaosEngineItems;
import net.chaosatom.thechaosengine.recipe.ChaosEngineRecipes;
import net.chaosatom.thechaosengine.screen.ChaosEngineMenuTypes;
import net.chaosatom.thechaosengine.screen.custom.*;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
import software.bernie.geckolib.loading.math.MolangQueries;

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

        ChaosEngineFluids.register(modEventBus);

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
    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Unused due to inability to get working properly, left here in case of revisit
            /*
            MolangQueries.<DeployableSolarBlockEntity>setActorVariable("query.chaosengine_solar_rotate_to_sun",
                    actor -> actor.animatable().getSunFacingAngle());
             */

            event.enqueueWork(() -> {
                    ItemBlockRenderTypes.setRenderLayer(ChaosEngineFluids.LAPIS_SUSPENSION_SOURCE.get(), RenderType.translucent());
                    ItemBlockRenderTypes.setRenderLayer(ChaosEngineFluids.LAPIS_SUSPENSION_FLOWING.get(), RenderType.translucent());
            });
        }

        @SubscribeEvent
        public static void registerBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ChaosEngineBlockEntities.COMPACT_INDUCTION_FOUNDRY_BE.get(), CompactInductionFoundryBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ChaosEngineBlockEntities.ATMOSPHERIC_CONDENSER_BE.get(), AtmosphericCondenserBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ChaosEngineBlockEntities.SUSPENSION_MIXER_BE.get(), SuspensionMixerBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ChaosEngineBlockEntities.DEPLOYABLE_SOLAR_BE.get(), DeployableSolarBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ChaosEngineMenuTypes.COMPACT_COAL_GENERATOR_MENU.get(), CompactCoalGeneratorScreen::new);
            event.register(ChaosEngineMenuTypes.COMPACT_PULVERIZER_MENU.get(), CompactPulverizerScreen::new);
            event.register(ChaosEngineMenuTypes.COMPACT_INDUCTION_FOUNDRY_MENU.get(), CompactInductionFoundryScreen::new);
            event.register(ChaosEngineMenuTypes.ATMOSPHERIC_CONDENSER_MENU.get(), AtmosphericCondenserScreen::new);
            event.register(ChaosEngineMenuTypes.SUSPENSION_MIXER_MENU.get(), SuspensionMixerScreen::new);
            event.register(ChaosEngineMenuTypes.DEPLOYABLE_SOLAR_MENU.get(), DeployableSolarScreen::new);
        }
    }
}