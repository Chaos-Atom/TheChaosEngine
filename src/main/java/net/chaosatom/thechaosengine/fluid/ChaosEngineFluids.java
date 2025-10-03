package net.chaosatom.thechaosengine.fluid;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.ChaosEngineBlocks;
import net.chaosatom.thechaosengine.item.ChaosEngineItems;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class ChaosEngineFluids {
    public static final ResourceLocation SUSPENSION_FLUID_STILL = ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID,
            "block/suspension_fluid_still");
    public static final ResourceLocation SUSPENSION_FLUID_FLOWING = ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID,
            "block/suspension_fluid_flow");
    public static final ResourceLocation SUSPENSION_FLUID_OVERLAY = ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID,
            "block/suspension_fluid_overlay");

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, TheChaosEngine.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, TheChaosEngine.MOD_ID);
    public static final DeferredRegister<Item> BUCKET_ITEM = DeferredRegister.createItems(TheChaosEngine.MOD_ID);
    public static final DeferredRegister<Block> SOURCE_BLOCK = DeferredRegister.createBlocks(TheChaosEngine.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> LAPIS_SUSPENSION_FLUID_TYPE = FLUID_TYPES.register("lapis_suspension",
            () -> new FluidType(FluidType.Properties.create()
                    .canSwim(true).canDrown(true).canPushEntity(true)
                    .supportsBoating(true).motionScale(0.012)));
    public static final DeferredHolder<Fluid, FlowingFluid> LAPIS_SUSPENSION_SOURCE = FLUIDS.register("lapis_suspension_source",
            () -> new BaseFlowingFluid.Source(liquidProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> LAPIS_SUSPENSION_FLOWING = FLUIDS.register("lapis_suspension_flowing",
            () -> new BaseFlowingFluid.Flowing(liquidProperties()));

    private static BaseFlowingFluid.Properties liquidProperties() {
        return new BaseFlowingFluid.Properties(LAPIS_SUSPENSION_FLUID_TYPE, LAPIS_SUSPENSION_SOURCE, LAPIS_SUSPENSION_FLOWING)
                .bucket(ChaosEngineItems.LAPIS_SUSPENSION_BUCKET).block(ChaosEngineBlocks.LAPIS_SUSPENSION_BLOCK);
    }

    private static final IClientFluidTypeExtensions getClientFluidTypeExtensions = new IClientFluidTypeExtensions() {
        @Override
        public int getTintColor() {
            return 0xEF345EC3;
        }

        @Override
        public ResourceLocation getStillTexture() {
            return SUSPENSION_FLUID_STILL;
        }

        @Override
        public ResourceLocation getFlowingTexture() {
            return SUSPENSION_FLUID_FLOWING;
        }

        @Override
        public @Nullable ResourceLocation getOverlayTexture() {
            return SUSPENSION_FLUID_OVERLAY;
        }

        @Override
        public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
            return new Vector3f(52f / 255f, 94f / 255f, 195f / 255f);
        }

        @Override
        public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
            RenderSystem.setShaderFogStart(1f);
            RenderSystem.setShaderFogEnd(4f);
        }
    };

    private static void clientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(getClientFluidTypeExtensions, LAPIS_SUSPENSION_FLUID_TYPE);
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
        BUCKET_ITEM.register(eventBus);
        SOURCE_BLOCK.register(eventBus);
        eventBus.addListener(ChaosEngineFluids::clientExtensions);
    }
}
