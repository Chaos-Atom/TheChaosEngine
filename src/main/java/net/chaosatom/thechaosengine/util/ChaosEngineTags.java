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
import net.minecraft.world.level.material.Fluid;

public class ChaosEngineTags {
    public static class Blocks {
        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> COAL_GENERATOR_FUEL = createTag("coal_generator_fuel");

        public static final TagKey<Item> DUSTS_IRON = commonNeoForgeTag("dusts/iron");
        public static final TagKey<Item> DUSTS_GOLD = commonNeoForgeTag("dusts/gold");
        public static final TagKey<Item> DUSTS_COPPER = commonNeoForgeTag("dusts/copper");
        public static final TagKey<Item> DUSTS_COAL = commonNeoForgeTag("dusts/coal");
        public static final TagKey<Item> DUSTS_LAPIS = commonNeoForgeTag("dusts/lapis");
        public static final TagKey<Item> DUSTS_CHARCOAL = commonNeoForgeTag("dusts/charcoal");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, name));
        }

        private static TagKey<Item> commonNeoForgeTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }

    public static class Biomes {
        // Used by Atmospheric Condenser for effectiveness rating
        public static final TagKey<Biome> EXTREMELY_WET = createTag("extremely_wet");
        public static final TagKey<Biome> VERY_WET = createTag("very_wet");
        public static final TagKey<Biome> WET = createTag("wet");
        public static final TagKey<Biome> TEMPERATE = createTag("temperate");
        public static final TagKey<Biome> DRY = createTag("dry");
        public static final TagKey<Biome> ARID = createTag("arid");

        // For Bauxite Ore Generation
        public static final TagKey<Biome> BAUXITE_BIOMES = createTag("bauxite_biomes");

        private static TagKey<Biome> createTag(String name) {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, name));
        }
    }

    public static class Fluids {
    }
    private static TagKey<Fluid> createTag(String name) {
        return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, name));
    }
}
