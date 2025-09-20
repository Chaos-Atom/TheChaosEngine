package net.chaosatom.thechaosengine.datagen;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TheChaosEngine.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.IRON_DUST.get());
        basicItem(ModItems.GOLD_DUST.get());
        basicItem(ModItems.COPPER_DUST.get());
        basicItem(ModItems.COAL_DUST.get());
        basicItem(ModItems.LAPIS_LAZULI_DUST.get());

        basicItem(ModItems.IRON_CHUNK.get());
        basicItem(ModItems.GOLD_CHUNK.get());
        basicItem(ModItems.COPPER_CHUNK.get());

        basicItem(ModItems.ENCHANTED_IRON_SHARDS.get());
        basicItem(ModItems.ENCHANTED_GOLD_SHARDS.get());
        basicItem(ModItems.ENCHANTED_COPPER_SHARDS.get());

        basicItem(ModItems.PURIFIED_CRYSTAL_IRON.get());
        basicItem(ModItems.PURIFIED_CRYSTAL_GOLD.get());
        basicItem(ModItems.PURIFIED_CRYSTAL_COPPER.get());

        basicItem(ModItems.CRYSTAL_IRON.get());
        basicItem(ModItems.CRYSTAL_GOLD.get());
        basicItem(ModItems.CRYSTAL_COPPER.get());
    }
}
