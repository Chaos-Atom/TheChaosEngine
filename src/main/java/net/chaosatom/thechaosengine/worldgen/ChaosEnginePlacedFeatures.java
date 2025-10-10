package net.chaosatom.thechaosengine.worldgen;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

public class ChaosEnginePlacedFeatures {
    public static final ResourceKey<PlacedFeature> BAUXITE_ORE_PLACED_UPPER_KEY = registerKey("bauxite_ore_placed_upper");
    public static final ResourceKey<PlacedFeature> BAUXITE_ORE_PLACED_LOWER_KEY = registerKey("bauxite_ore_placed_lower");

    public static void bootstrap (BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, BAUXITE_ORE_PLACED_UPPER_KEY, configuredFeatures.getOrThrow(ChaosEngineConfiguredFeatures.OVERWORLD_BAUXITE_ORE_KEY),
                ChaosEngineOrePlacements.commonOrePlacement(5,
                        HeightRangePlacement.triangle(VerticalAnchor.absolute(48), VerticalAnchor.absolute(180))));
        register(context, BAUXITE_ORE_PLACED_LOWER_KEY, configuredFeatures.getOrThrow(ChaosEngineConfiguredFeatures.OVERWORLD_BAUXITE_ORE_KEY),
                ChaosEngineOrePlacements.rareOrePlacement(3, HeightRangePlacement.triangle(VerticalAnchor.absolute(32), VerticalAnchor.absolute(96))));
    }

    private static ResourceKey<PlacedFeature> registerKey (String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID, name));
    }

    private static void register (BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                  List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
