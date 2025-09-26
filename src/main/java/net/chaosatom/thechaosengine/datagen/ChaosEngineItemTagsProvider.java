package net.chaosatom.thechaosengine.datagen;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.item.ChaosEngineItems;
import net.chaosatom.thechaosengine.util.ChaosEngineTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ChaosEngineItemTagsProvider extends ItemTagsProvider {
    public ChaosEngineItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                       CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, TheChaosEngine.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ChaosEngineTags.Items.COAL_GENERATOR_FUEL)
                .add(Items.COAL)
                .add(Items.CHARCOAL)
                .add(Items.COAL_BLOCK)
                .add(ChaosEngineItems.COAL_DUST.get())
                .add(ChaosEngineItems.CHARCOAL_DUST.get());

        tag(ChaosEngineTags.Items.DUSTS_IRON).add(ChaosEngineItems.IRON_DUST.get());
        tag(ChaosEngineTags.Items.DUSTS_GOLD).add(ChaosEngineItems.GOLD_DUST.get());
        tag(ChaosEngineTags.Items.DUSTS_COPPER).add(ChaosEngineItems.COPPER_DUST.get());
        tag(ChaosEngineTags.Items.DUSTS_COAL).add(ChaosEngineItems.COAL_DUST.get());
        tag(ChaosEngineTags.Items.DUSTS_LAPIS).add(ChaosEngineItems.LAPIS_LAZULI_DUST.get());
        tag(ChaosEngineTags.Items.DUSTS_CHARCOAL).add(ChaosEngineItems.CHARCOAL_DUST.get());
    }
}
