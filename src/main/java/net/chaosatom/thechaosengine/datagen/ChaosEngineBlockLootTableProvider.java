package net.chaosatom.thechaosengine.datagen;

import net.chaosatom.thechaosengine.block.ChaosEngineBlocks;
import net.chaosatom.thechaosengine.item.ChaosEngineItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public class ChaosEngineBlockLootTableProvider extends BlockLootSubProvider {
    protected ChaosEngineBlockLootTableProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        dropSelf(ChaosEngineBlocks.IRON_DUST_BLOCK.get());
        dropSelf(ChaosEngineBlocks.GOLD_DUST_BLOCK.get());
        dropSelf(ChaosEngineBlocks.COPPER_DUST_BLOCK.get());

        dropSelf(ChaosEngineBlocks.COMPACT_COAL_GENERATOR.get());
        dropSelf(ChaosEngineBlocks.COMPACT_PULVERIZER.get());
        dropSelf(ChaosEngineBlocks.COMPACT_INDUCTION_FOUNDRY.get());
        dropSelf(ChaosEngineBlocks.ATMOSPHERIC_CONDENSER.get());

        dropSelf(ChaosEngineBlocks.BAUXITE.get());
        dropSelf(ChaosEngineBlocks.BAUXITE_STAIRS.get());
        this.add(ChaosEngineBlocks.BAUXITE_SLAB.get(),
                block -> createSlabItemTable(ChaosEngineBlocks.BAUXITE_SLAB.get()));
        dropSelf(ChaosEngineBlocks.BAUXITE_WALL.get());

        dropSelf(ChaosEngineBlocks.POLISHED_BAUXITE.get());
        dropSelf(ChaosEngineBlocks.POLISHED_BAUXITE_STAIRS.get());
        this.add(ChaosEngineBlocks.POLISHED_BAUXITE_SLAB.get(),
                block -> createSlabItemTable(ChaosEngineBlocks.POLISHED_BAUXITE_SLAB.get()));
        dropSelf(ChaosEngineBlocks.POLISHED_BAUXITE_WALL.get());

        dropSelf(ChaosEngineBlocks.BAUXITE_BRICKS.get());
        dropSelf(ChaosEngineBlocks.BAUXITE_BRICK_STAIRS.get());
        this.add(ChaosEngineBlocks.BAUXITE_BRICK_SLAB.get(),
                block -> createSlabItemTable(ChaosEngineBlocks.BAUXITE_BRICK_SLAB.get()));
        dropSelf(ChaosEngineBlocks.BAUXITE_BRICK_WALL.get());

        dropSelf(ChaosEngineBlocks.BAUXITE_TILES.get());
        dropSelf(ChaosEngineBlocks.BAUXITE_TILE_STAIRS.get());
        this.add(ChaosEngineBlocks.BAUXITE_TILE_SLAB.get(),
                block -> createSlabItemTable(ChaosEngineBlocks.BAUXITE_TILE_SLAB.get()));
        dropSelf(ChaosEngineBlocks.BAUXITE_TILE_WALL.get());
    }

    // Modified version of createCopperOreDrops to be more generic
    protected LootTable.Builder createMultipleOreDrops(Block block, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(block, this.applyExplosionDecay(block,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                                .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ChaosEngineBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
