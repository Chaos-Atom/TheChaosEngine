package net.chaosatom.thechaosengine.item;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.ChaosEngineBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ChaosEngineCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TheChaosEngine.MOD_ID);

    public static final Supplier<CreativeModeTab> THE_CHAOS_ENGINE_ITEMS_TAB = CREATIVE_MODE_TAB.register("the_chaos_engine_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ChaosEngineBlocks.COMPACT_COAL_GENERATOR.get()))
                    .title(Component.translatable("creativetab.thechaosengine.the_chaos_engine_items"))
                    .displayItems((parameters, output) -> {
                        // Ores
                        output.accept(ChaosEngineItems.IRON_DUST);
                        output.accept(ChaosEngineItems.GOLD_DUST);
                        output.accept(ChaosEngineItems.COPPER_DUST);
                        output.accept(ChaosEngineItems.COAL_DUST);
                        output.accept(ChaosEngineItems.LAPIS_LAZULI_DUST);

                        output.accept(ChaosEngineItems.IRON_CHUNK);
                        output.accept(ChaosEngineItems.GOLD_CHUNK);
                        output.accept(ChaosEngineItems.COPPER_CHUNK);

                        output.accept(ChaosEngineItems.ENCHANTED_IRON_SHARDS);
                        output.accept(ChaosEngineItems.ENCHANTED_GOLD_SHARDS);
                        output.accept(ChaosEngineItems.ENCHANTED_COPPER_SHARDS);

                        output.accept(ChaosEngineItems.PURIFIED_CRYSTAL_IRON);
                        output.accept(ChaosEngineItems.PURIFIED_CRYSTAL_GOLD);
                        output.accept(ChaosEngineItems.PURIFIED_CRYSTAL_COPPER);

                        output.accept(ChaosEngineItems.CRYSTAL_IRON);
                        output.accept(ChaosEngineItems.CRYSTAL_GOLD);
                        output.accept(ChaosEngineItems.CRYSTAL_COPPER);

                        output.accept(ChaosEngineItems.IRON_NANOPARTICLE);
                        output.accept(ChaosEngineItems.GOLD_NANOPARTICLE);
                        output.accept(ChaosEngineItems.COPPER_NANOPARTICLE);

                        // Full Blocks
                        output.accept(ChaosEngineBlocks.IRON_DUST_BLOCK);
                        output.accept(ChaosEngineBlocks.GOLD_DUST_BLOCK);
                        output.accept(ChaosEngineBlocks.COPPER_DUST_BLOCK);
                        output.accept(ChaosEngineBlocks.BAUXITE);
                        output.accept(ChaosEngineBlocks.POLISHED_BAUXITE);
                        output.accept(ChaosEngineBlocks.BAUXITE_BRICKS);

                        // Partial Blocks (Stairs, Slabs, Walls)
                        output.accept(ChaosEngineBlocks.BAUXITE_STAIRS);
                        output.accept(ChaosEngineBlocks.BAUXITE_SLAB);
                        output.accept(ChaosEngineBlocks.BAUXITE_WALL);

                        output.accept(ChaosEngineBlocks.POLISHED_BAUXITE_STAIRS);
                        output.accept(ChaosEngineBlocks.POLISHED_BAUXITE_SLAB);
                        output.accept(ChaosEngineBlocks.POLISHED_BAUXITE_WALL);

                        output.accept(ChaosEngineBlocks.BAUXITE_BRICK_STAIRS);
                        output.accept(ChaosEngineBlocks.BAUXITE_BRICK_SLAB);
                        output.accept(ChaosEngineBlocks.BAUXITE_BRICK_WALL);

                        // Machine Blocks (Single)
                        output.accept(ChaosEngineBlocks.COMPACT_COAL_GENERATOR);
                        output.accept(ChaosEngineBlocks.COMPACT_PULVERIZER);
                        output.accept(ChaosEngineBlocks.COMPACT_INDUCTION_FOUNDRY);
                        output.accept(ChaosEngineBlocks.ATMOSPHERIC_CONDENSER);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}