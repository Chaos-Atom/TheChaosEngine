package net.chaosatom.thechaosengine.block;

import com.mojang.serialization.MapCodec;
import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.custom.CompactCoalGeneratorBlock;
import net.chaosatom.thechaosengine.block.custom.CompactPulverizerBlock;
import net.chaosatom.thechaosengine.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TheChaosEngine.MOD_ID);

    // Block of Ore Dust
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

    // Complex Blocks
    public static  final DeferredBlock<Block> COMPACT_COAL_GENERATOR = registerBlock("compact_coal_generator",
            () -> new CompactCoalGeneratorBlock(BlockBehaviour.Properties.of().noOcclusion().strength(3f)));
    public static final DeferredBlock<Block> COMPACT_PULVERIZER = registerBlock("compact_pulverizer",
            () -> new CompactPulverizerBlock(BlockBehaviour.Properties.of().noOcclusion().strength(3f)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
