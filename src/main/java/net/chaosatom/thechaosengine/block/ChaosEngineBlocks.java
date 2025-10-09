package net.chaosatom.thechaosengine.block;

import com.mojang.serialization.MapCodec;
import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.custom.*;
import net.chaosatom.thechaosengine.fluid.ChaosEngineFluids;
import net.chaosatom.thechaosengine.item.ChaosEngineItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ChaosEngineBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TheChaosEngine.MOD_ID);

    // Block of Ore Dust (with Falling)
    public static final DeferredBlock<Block> IRON_DUST_BLOCK = registerBlock("iron_dust_block",
            () -> new FallingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(1f).sound(SoundType.SAND))
                {
                @Override
                protected MapCodec<? extends FallingBlock> codec() {
                    return null;
                }
                });
    public static final DeferredBlock<Block> GOLD_DUST_BLOCK = registerBlock("gold_dust_block",
            () -> new FallingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(1f).sound(SoundType.SAND))
            {
                @Override
                protected MapCodec<? extends FallingBlock> codec() {
                    return null;
                }
            });
    public static final DeferredBlock<Block> COPPER_DUST_BLOCK = registerBlock("copper_dust_block",
            () -> new FallingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(1f).sound(SoundType.SAND))
            {
                @Override
                protected MapCodec<? extends FallingBlock> codec() {
                    return null;
                }
            });

    // General Blocks
    // Bauxite-related
    public static final DeferredBlock<Block> BAUXITE = registerBlock("bauxite",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.35f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> BAUXITE_STAIRS = registerBlock("bauxite_stairs",
            () -> new StairBlock(ChaosEngineBlocks.BAUXITE.get().defaultBlockState() ,BlockBehaviour.Properties.of().strength(1.35f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> BAUXITE_SLAB = registerBlock("bauxite_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(1.35f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> BAUXITE_WALL = registerBlock("bauxite_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(1.35f).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final DeferredBlock<Block> POLISHED_BAUXITE = registerBlock("polished_bauxite",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.35f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> POLISHED_BAUXITE_STAIRS = registerBlock("polished_bauxite_stairs",
            () -> new StairBlock(ChaosEngineBlocks.POLISHED_BAUXITE.get().defaultBlockState() ,BlockBehaviour.Properties.of().strength(1.35f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> POLISHED_BAUXITE_SLAB = registerBlock("polished_bauxite_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(1.35f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> POLISHED_BAUXITE_WALL = registerBlock("polished_bauxite_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(1.35f).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final DeferredBlock<Block> BAUXITE_BRICKS = registerBlock("bauxite_bricks",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.35f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> BAUXITE_BRICK_STAIRS = registerBlock("bauxite_brick_stairs",
            () -> new StairBlock(ChaosEngineBlocks.BAUXITE_BRICKS.get().defaultBlockState() ,BlockBehaviour.Properties.of().strength(1.35f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> BAUXITE_BRICK_SLAB = registerBlock("bauxite_brick_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(1.35f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> BAUXITE_BRICK_WALL = registerBlock("bauxite_brick_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(1.35f).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final DeferredBlock<Block> BAUXITE_TILES = registerBlock("bauxite_tiles",
            () -> new Block(BlockBehaviour.Properties.of().strength(1.35f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> BAUXITE_TILE_STAIRS = registerBlock("bauxite_tile_stairs",
            () -> new StairBlock(ChaosEngineBlocks.BAUXITE_TILES.get().defaultBlockState() ,BlockBehaviour.Properties.of().strength(1.35f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> BAUXITE_TILE_SLAB = registerBlock("bauxite_tile_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(1.35f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> BAUXITE_TILE_WALL = registerBlock("bauxite_tile_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(1.35f).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    // Alumina Bronze
    public static final DeferredBlock<Block> ALUMINA_BRONZE_BLOCK = registerBlock("alumina_bronze_block",
            () -> new Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops()
                    .strength(4.5f, 5.0f).sound(SoundType.METAL)));
    public static final DeferredBlock<Block> ORNATE_ALUMINA_BRONZE = registerBlock("ornate_alumina_bronze",
            () -> new Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops()
                    .strength(4.5f, 5.0f).sound(SoundType.METAL)));
    public static final DeferredBlock<Block> ALUMINA_BRONZE_PANELS = registerBlock("alumina_bronze_panels",
            () -> new Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops()
                    .strength(4.5f, 5.0f).sound(SoundType.METAL)));
    public static final DeferredBlock<Block> CUBIC_ALUMINA_BRONZE = registerBlock("cubic_alumina_bronze",
            () -> new Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops()
                    .strength(4.5f, 5.0f).sound(SoundType.METAL)));

    // Complex Blocks
    public static  final DeferredBlock<Block> COMPACT_COAL_GENERATOR = registerBlock("compact_coal_generator",
            () -> new CompactCoalGeneratorBlock(BlockBehaviour.Properties.of().noOcclusion().strength(3f)));
    public static final DeferredBlock<Block> COMPACT_PULVERIZER = registerBlock("compact_pulverizer",
            () -> new CompactPulverizerBlock(BlockBehaviour.Properties.of().noOcclusion().strength(3f)));
    public static final DeferredBlock<Block> COMPACT_INDUCTION_FOUNDRY = registerBlock("compact_induction_foundry",
            () -> new CompactInductionFoundryBlock(BlockBehaviour.Properties.of().noOcclusion().strength(3f)));
    public static final DeferredBlock<Block> ATMOSPHERIC_CONDENSER = registerBlock("atmospheric_condenser",
            () -> new AtmosphericCondenserBlock(BlockBehaviour.Properties.of().noOcclusion().strength(3f)));
    public static final DeferredBlock<Block> SUSPENSION_MIXER = registerBlock("suspension_mixer",
            () -> new SuspensionMixerBlock(BlockBehaviour.Properties.of().noOcclusion().strength(3f)));
    public static final DeferredBlock<Block> DEPLOYABLE_SOLAR = registerBlock("deployable_solar",
            () -> new DeployableSolarBlock(BlockBehaviour.Properties.of().noOcclusion().strength(3f)));

    // Fluid Blocks
    public static final DeferredHolder<Block, LiquidBlock> LAPIS_SUSPENSION_BLOCK = BLOCKS.register("lapis_suspension_block",
            () -> new LiquidBlock(ChaosEngineFluids.LAPIS_SUSPENSION_SOURCE.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ChaosEngineItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
