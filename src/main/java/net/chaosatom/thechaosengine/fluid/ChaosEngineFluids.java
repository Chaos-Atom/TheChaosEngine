package net.chaosatom.thechaosengine.fluid;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.ChaosEngineBlocks;
import net.chaosatom.thechaosengine.item.ChaosEngineItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ChaosEngineFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, TheChaosEngine.MOD_ID);

    public static final Supplier<FlowingFluid> SOURCE_LAPIS_SUSPENSION_FLUID = FLUIDS.register("source_lapis_suspension",
            () -> new BaseFlowingFluid.Source(ChaosEngineFluids.LAPIS_SUSPENSION_PROPERTIES));
    public static final Supplier<FlowingFluid> FLOWING_LAPIS_SUSPENSION_FLUID = FLUIDS.register("flowing_lapis_suspension",
            () -> new BaseFlowingFluid.Flowing(ChaosEngineFluids.LAPIS_SUSPENSION_PROPERTIES));

    public static final DeferredBlock<LiquidBlock> LAPIS_SUSPENSION_FLUID_BLOCK = ChaosEngineBlocks.BLOCKS.register("lapis_suspension_fluid_block",
            () -> new LiquidBlock(ChaosEngineFluids.SOURCE_LAPIS_SUSPENSION_FLUID.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final DeferredItem<Item> LAPIS_SUSPENSION_FLUID_BUCKET = ChaosEngineItems.ITEMS.registerItem("lapis_suspension_fluid_bucket",
            properties -> new BucketItem(ChaosEngineFluids.SOURCE_LAPIS_SUSPENSION_FLUID.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final BaseFlowingFluid.Properties LAPIS_SUSPENSION_PROPERTIES = new BaseFlowingFluid.Properties(
            ChaosEngineFluidTypes.LAPIS_SUSPENSION_FLUID_TYPE, SOURCE_LAPIS_SUSPENSION_FLUID, FLOWING_LAPIS_SUSPENSION_FLUID)
            .slopeFindDistance(2).levelDecreasePerBlock(1).block(LAPIS_SUSPENSION_FLUID_BLOCK).bucket(LAPIS_SUSPENSION_FLUID_BUCKET);


    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}
