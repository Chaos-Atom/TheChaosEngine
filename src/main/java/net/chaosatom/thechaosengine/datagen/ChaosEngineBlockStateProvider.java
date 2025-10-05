package net.chaosatom.thechaosengine.datagen;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.ChaosEngineBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ChaosEngineBlockStateProvider extends BlockStateProvider {
    public ChaosEngineBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TheChaosEngine.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ChaosEngineBlocks.POLISHED_BAUXITE);
        blockWithItem(ChaosEngineBlocks.BAUXITE_BRICKS);
        blockWithItem(ChaosEngineBlocks.BAUXITE_TILES);

        blockWithItem(ChaosEngineBlocks.ALUMINA_BRONZE_BLOCK);
        blockWithItem(ChaosEngineBlocks.ORNATE_ALUMINA_BRONZE);
        blockWithItem(ChaosEngineBlocks.ALUMINA_BRONZE_PANELS);
        blockWithItem(ChaosEngineBlocks.CUBIC_ALUMINA_BRONZE);

        stairsBlock(((StairBlock) ChaosEngineBlocks.BAUXITE_STAIRS.get()), blockTexture(ChaosEngineBlocks.BAUXITE.get()));
        slabBlock(((SlabBlock) ChaosEngineBlocks.BAUXITE_SLAB.get()), blockTexture(ChaosEngineBlocks.BAUXITE.get()), blockTexture(ChaosEngineBlocks.BAUXITE.get()));
        wallBlock(((WallBlock) ChaosEngineBlocks.BAUXITE_WALL.get()), blockTexture(ChaosEngineBlocks.BAUXITE.get()));

        stairsBlock(((StairBlock) ChaosEngineBlocks.POLISHED_BAUXITE_STAIRS.get()), blockTexture(ChaosEngineBlocks.POLISHED_BAUXITE.get()));
        slabBlock(((SlabBlock) ChaosEngineBlocks.POLISHED_BAUXITE_SLAB.get()), blockTexture(ChaosEngineBlocks.POLISHED_BAUXITE.get()), blockTexture(ChaosEngineBlocks.POLISHED_BAUXITE.get()));
        wallBlock(((WallBlock) ChaosEngineBlocks.POLISHED_BAUXITE_WALL.get()), blockTexture(ChaosEngineBlocks.POLISHED_BAUXITE.get()));

        stairsBlock(((StairBlock) ChaosEngineBlocks.BAUXITE_BRICK_STAIRS.get()), blockTexture(ChaosEngineBlocks.BAUXITE_BRICKS.get()));
        slabBlock(((SlabBlock) ChaosEngineBlocks.BAUXITE_BRICK_SLAB.get()), blockTexture(ChaosEngineBlocks.BAUXITE_BRICKS.get()), blockTexture(ChaosEngineBlocks.BAUXITE_BRICKS.get()));
        wallBlock(((WallBlock) ChaosEngineBlocks.BAUXITE_BRICK_WALL.get()), blockTexture(ChaosEngineBlocks.BAUXITE_BRICKS.get()));

        stairsBlock(((StairBlock) ChaosEngineBlocks.BAUXITE_TILE_STAIRS.get()), blockTexture(ChaosEngineBlocks.BAUXITE_TILES.get()));
        slabBlock(((SlabBlock) ChaosEngineBlocks.BAUXITE_TILE_SLAB.get()), blockTexture(ChaosEngineBlocks.BAUXITE_TILES.get()), blockTexture(ChaosEngineBlocks.BAUXITE_TILES.get()));
        wallBlock(((WallBlock) ChaosEngineBlocks.BAUXITE_TILE_WALL.get()), blockTexture(ChaosEngineBlocks.BAUXITE_TILES.get()));

        blockItem(ChaosEngineBlocks.BAUXITE_STAIRS);
        blockItem(ChaosEngineBlocks.BAUXITE_SLAB);

        blockItem(ChaosEngineBlocks.POLISHED_BAUXITE_STAIRS);
        blockItem(ChaosEngineBlocks.POLISHED_BAUXITE_SLAB);

        blockItem(ChaosEngineBlocks.BAUXITE_BRICK_STAIRS);
        blockItem(ChaosEngineBlocks.BAUXITE_BRICK_SLAB);

        blockItem(ChaosEngineBlocks.BAUXITE_TILE_STAIRS);
        blockItem(ChaosEngineBlocks.BAUXITE_TILE_SLAB);
    }

    private void blockWithItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("thechaosengine:block/" + deferredBlock.getId().getPath()));
    }

    // Attempt at better categorizing related blocks, have to premake the new texture directory first
//    private void blockItemCustomFilepath(DeferredBlock<Block> deferredBlock, String customDirectory) {
//        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("thechaosengine:block/" +
//                customDirectory + "/" + deferredBlock.getId().getPath()));
//    }
}
