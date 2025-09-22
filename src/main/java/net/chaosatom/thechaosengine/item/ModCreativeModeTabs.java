package net.chaosatom.thechaosengine.item;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TheChaosEngine.MOD_ID);

    public static final Supplier<CreativeModeTab> THE_CHAOS_ENGINE_ITEMS_TAB = CREATIVE_MODE_TAB.register("the_chaos_engine_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.COMPACT_COAL_GENERATOR.get()))
                    .title(Component.translatable("creativetab.thechaosengine.the_chaos_engine_items"))
                    .displayItems((parameters, output) -> {
                        // Ores
                        output.accept(ModItems.IRON_DUST);
                        output.accept(ModItems.GOLD_DUST);
                        output.accept(ModItems.COPPER_DUST);
                        output.accept(ModItems.COAL_DUST);
                        output.accept(ModItems.LAPIS_LAZULI_DUST);
                        output.accept(ModItems.IRON_CHUNK);
                        output.accept(ModItems.GOLD_CHUNK);
                        output.accept(ModItems.COPPER_CHUNK);
                        output.accept(ModItems.ENCHANTED_IRON_SHARDS);
                        output.accept(ModItems.ENCHANTED_GOLD_SHARDS);
                        output.accept(ModItems.ENCHANTED_COPPER_SHARDS);
                        output.accept(ModItems.PURIFIED_CRYSTAL_IRON);
                        output.accept(ModItems.PURIFIED_CRYSTAL_GOLD);
                        output.accept(ModItems.PURIFIED_CRYSTAL_COPPER);
                        output.accept(ModItems.CRYSTAL_IRON);
                        output.accept(ModItems.CRYSTAL_GOLD);
                        output.accept(ModItems.CRYSTAL_COPPER);
                        output.accept(ModItems.IRON_NANOPARTICLE);
                        output.accept(ModItems.GOLD_NANOPARTICLE);
                        output.accept(ModItems.COPPER_NANOPARTICLE);

                        // Blocks
                        output.accept(ModBlocks.IRON_DUST_BLOCK);
                        output.accept(ModBlocks.GOLD_DUST_BLOCK);
                        output.accept(ModBlocks.COPPER_DUST_BLOCK);

                        // MACHINE BLOCKS (SINGLE)
                        output.accept(ModBlocks.COMPACT_COAL_GENERATOR);
                        output.accept(ModBlocks.COMPACT_PULVERIZER);
                        output.accept(ModBlocks.COMPACT_INDUCTION_FOUNDRY);
                        output.accept(ModBlocks.ATMOSPHERIC_CONDENSER);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}