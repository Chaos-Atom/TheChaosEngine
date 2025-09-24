package net.chaosatom.thechaosengine.util;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> COAL_GENERATOR_FUEL = createTag("coal_generator_fuel");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, name));
        }
    }

    public class Biomes {
        public static final TagKey<Biome> EXTREMELY_WET = create("extremely_wet");
        public static final TagKey<Biome> VERY_WET = create("very_wet");
        public static final TagKey<Biome> WET = create("wet");
        public static final TagKey<Biome> TEMPERATE = create("temperate");
        public static final TagKey<Biome> DRY = create("dry");
        public static final TagKey<Biome> ARID = create("arid");

        private static TagKey<Biome> create(String name) {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, name));
        }
    }
}
