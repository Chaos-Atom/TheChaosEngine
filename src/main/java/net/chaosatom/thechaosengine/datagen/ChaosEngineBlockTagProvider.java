package net.chaosatom.thechaosengine.datagen;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.ChaosEngineBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ChaosEngineBlockTagProvider extends BlockTagsProvider {
    public ChaosEngineBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TheChaosEngine.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ChaosEngineBlocks.IRON_DUST_BLOCK.get())
                .add(ChaosEngineBlocks.GOLD_DUST_BLOCK.get())
                .add(ChaosEngineBlocks.COPPER_DUST_BLOCK.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ChaosEngineBlocks.BAUXITE.get())
                .add(ChaosEngineBlocks.BAUXITE_STAIRS.get())
                .add(ChaosEngineBlocks.BAUXITE_SLAB.get())
                .add(ChaosEngineBlocks.BAUXITE_WALL.get())
                .add(ChaosEngineBlocks.POLISHED_BAUXITE.get())
                .add(ChaosEngineBlocks.POLISHED_BAUXITE_STAIRS.get())
                .add(ChaosEngineBlocks.POLISHED_BAUXITE_SLAB.get())
                .add(ChaosEngineBlocks.POLISHED_BAUXITE_WALL.get())
                .add(ChaosEngineBlocks.BAUXITE_BRICKS.get())
                .add(ChaosEngineBlocks.BAUXITE_BRICK_STAIRS.get())
                .add(ChaosEngineBlocks.BAUXITE_BRICK_SLAB.get())
                .add(ChaosEngineBlocks.BAUXITE_BRICK_WALL.get());

        this.tag(BlockTags.WALLS)
                .add(ChaosEngineBlocks.BAUXITE_WALL.get())
                .add(ChaosEngineBlocks.POLISHED_BAUXITE_WALL.get())
                .add(ChaosEngineBlocks.BAUXITE_BRICK_WALL.get());
    }
}
