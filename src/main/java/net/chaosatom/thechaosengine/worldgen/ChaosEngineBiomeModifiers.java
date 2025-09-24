package net.chaosatom.thechaosengine.worldgen;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.util.ChaosEngineTags;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ChaosEngineBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_BAUXITE_ORE_UPPER = registerKey("add_bauxite_ore_upper");
    public static final ResourceKey<BiomeModifier> ADD_BAUXITE_ORE_LOWER = registerKey("add_bauxite_ore_lower");

    public static void bootstrap (BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_BAUXITE_ORE_UPPER, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ChaosEngineTags.Biomes.BAUXITE_BIOMES),
                HolderSet.direct(placedFeatures.getOrThrow(ChaosEnginePlacedFeatures.BAUXITE_ORE_PLACED_UPPER_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_BAUXITE_ORE_LOWER, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ChaosEngineTags.Biomes.BAUXITE_BIOMES),
                HolderSet.direct(placedFeatures.getOrThrow(ChaosEnginePlacedFeatures.BAUXITE_ORE_PLACED_LOWER_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
    }

    private static ResourceKey<BiomeModifier> registerKey (String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, name));
    }
}
