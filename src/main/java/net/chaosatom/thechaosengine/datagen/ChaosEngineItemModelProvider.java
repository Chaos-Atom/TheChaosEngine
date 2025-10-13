package net.chaosatom.thechaosengine.datagen;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.ChaosEngineBlocks;
import net.chaosatom.thechaosengine.fluid.ChaosEngineFluids;
import net.chaosatom.thechaosengine.item.ChaosEngineItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ChaosEngineItemModelProvider extends ItemModelProvider {
    public ChaosEngineItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TheChaosEngine.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ChaosEngineItems.IRON_DUST.get());
        basicItem(ChaosEngineItems.GOLD_DUST.get());
        basicItem(ChaosEngineItems.COPPER_DUST.get());
        basicItem(ChaosEngineItems.COAL_DUST.get());
        basicItem(ChaosEngineItems.LAPIS_LAZULI_DUST.get());
        basicItem(ChaosEngineItems.CHARCOAL_DUST.get());

        basicItem(ChaosEngineItems.IRON_CHUNK.get());
        basicItem(ChaosEngineItems.GOLD_CHUNK.get());
        basicItem(ChaosEngineItems.COPPER_CHUNK.get());

        basicItem(ChaosEngineItems.BAUXITE_CHUNK.get());

        basicItem(ChaosEngineItems.ENCHANTED_IRON_SHARDS.get());
        basicItem(ChaosEngineItems.ENCHANTED_GOLD_SHARDS.get());
        basicItem(ChaosEngineItems.ENCHANTED_COPPER_SHARDS.get());

        basicItem(ChaosEngineItems.PURIFIED_CRYSTAL_IRON.get());
        basicItem(ChaosEngineItems.PURIFIED_CRYSTAL_GOLD.get());
        basicItem(ChaosEngineItems.PURIFIED_CRYSTAL_COPPER.get());

        basicItem(ChaosEngineItems.CRYSTAL_IRON.get());
        basicItem(ChaosEngineItems.CRYSTAL_GOLD.get());
        basicItem(ChaosEngineItems.CRYSTAL_COPPER.get());

        basicItem(ChaosEngineItems.LAPIS_SUSPENSION_BUCKET.get());

        wallItem(ChaosEngineBlocks.BAUXITE_WALL, ChaosEngineBlocks.BAUXITE);
        wallItem(ChaosEngineBlocks.POLISHED_BAUXITE_WALL, ChaosEngineBlocks.POLISHED_BAUXITE);
        wallItem(ChaosEngineBlocks.BAUXITE_BRICK_WALL, ChaosEngineBlocks.BAUXITE_BRICKS);
        wallItem(ChaosEngineBlocks.BAUXITE_TILE_WALL, ChaosEngineBlocks.BAUXITE_TILES);
    }

    public void wallItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall", ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }
}
